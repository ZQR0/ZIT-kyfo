-- liquibase formatted sql

--changeset init:yaroslav-01


-- ============================================================
-- УЛУЧШЕННАЯ СХЕМА
-- PostgreSQL. Точечные правки поверх исходного варианта —
-- без лишних сущностей (ролей, каталогов товаров, аудита и т.п.)
-- ============================================================

-- нужно заранее — используется в DEFAULT для ticket.ticket_number
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE airlines (
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    -- добавлено: без этого нечем авторизовать "личный кабинет авиакомпании" из ТЗ
    login         VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE airports (
    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    unique_code VARCHAR(10)  NOT NULL UNIQUE,  -- явный UNIQUE вместо неявного требования
    town        VARCHAR(100),
    address     VARCHAR(255),
    -- формат по образцу IATA-кода аэропорта (3 латинские буквы) —
    -- тоже подсмотрено в примере boarding_passes.sql
    CHECK (unique_code ~ '^[A-Z]{3}$')
);

CREATE TABLE flight (
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id    INT NOT NULL REFERENCES airlines(id),
    airplane      VARCHAR(100),
    airport_from  INT NOT NULL REFERENCES airports(id),
    airport_to    INT NOT NULL REFERENCES airports(id),
    time_out      TIMESTAMP NOT NULL,          -- плановое время вылета
    time_in       TIMESTAMP NOT NULL,          -- плановое время прилёта
    -- заменили плоский флаг delayed на длительность задержки в минутах:
    -- 1) флаг легко получить как delay_minutes > 0
    -- 2) компенсация по ФАП-82 зависит именно от длительности задержки,
    --    а не от факта её наличия — это нужно для отчётов и для логики начислений
    delay_minutes INT NOT NULL DEFAULT 0 CHECK (delay_minutes >= 0),
    CHECK (airport_from <> airport_to),
    CHECK (time_in > time_out)
);

-- внешние ключи часто участвуют в JOIN/WHERE — в Postgres они не индексируются
-- автоматически (в отличие от PK), поэтому индексы добавлены явно
CREATE INDEX idx_flight_company     ON flight(company_id);
CREATE INDEX idx_flight_airport_from ON flight(airport_from);
CREATE INDEX idx_flight_airport_to   ON flight(airport_to);

CREATE TABLE passenger (
    id               INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100) NOT NULL,
    patronymic       VARCHAR(100),
    -- паспорт РФ разбит на серию и номер, как в самом документе,
    -- а не хранится одной строкой — это удобнее для поиска/сверки
    -- и для валидации формата каждой части по отдельности
    passport_series  CHAR(4) NOT NULL,
    passport_number  CHAR(6) NOT NULL,
    CHECK (passport_series ~ '^[0-9]{4}$'),
    CHECK (passport_number ~ '^[0-9]{6}$'),
    UNIQUE (passport_series, passport_number)   -- один паспорт = один пассажир
);

CREATE TABLE ticket (
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    -- добавлено: то, что реально сканируется/вводится в кафе и в API,
    -- не должно совпадать с внутренним последовательным id (его легко перебрать)
    ticket_number VARCHAR(20) UNIQUE NOT NULL DEFAULT encode(gen_random_bytes(8), 'hex'),
    flight_id     INT NOT NULL REFERENCES flight(id),
    passenger_id  INT NOT NULL REFERENCES passenger(id),
    seat          VARCHAR(10),
    balance       NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (balance >= 0),
    -- формат места (номер ряда + буква) — идея из примера boarding_passes.sql,
    -- недорогая проверка, отсекающая опечатки при вводе
    CHECK (seat IS NULL OR seat ~ '^[0-9]{1,3}[A-Z]$'),
    UNIQUE (flight_id, seat)                  -- одно место на рейсе — один талон
);

CREATE INDEX idx_ticket_flight    ON ticket(flight_id);
CREATE INDEX idx_ticket_passenger ON ticket(passenger_id);

CREATE TABLE service_points (
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    -- заменили связь по "натуральному" airport_code на FK по id —
    -- так связь короче, быстрее (join по int) и не ломается при смене кода аэропорта
    airport_id    INT NOT NULL REFERENCES airports(id),
    contact_phone VARCHAR(20),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_service_points_airport ON service_points(airport_id);

CREATE TABLE transaction (
    id                INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_id         INT NOT NULL REFERENCES ticket(id),
    service_point_id  INT NOT NULL REFERENCES service_points(id),
    amount            NUMERIC(10,2) NOT NULL CHECK (amount > 0),
    -- явно ограничили допустимые типы операции: пополнение (при задержке)
    -- и покупка (списание в кафе/точке обслуживания)
    type              VARCHAR(20) NOT NULL CHECK (type IN ('topup', 'purchase')),
    created_at        TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_transaction_ticket ON transaction(ticket_id);
CREATE INDEX idx_transaction_sp     ON transaction(service_point_id);

-- ============================================================
-- ticket.balance — это "кэш" итогового остатка, а вся история
-- пополнений/списаний хранится в transaction. Актуальность баланса
-- на уровне БД триггером не поддерживается (сознательно, чтобы
-- не прятать бизнес-логику в PL/pgSQL) — обновление balance
-- и вставку строки в transaction должно делать приложение
-- одним и тем же DB-транзакшеном (BEGIN...COMMIT), чтобы
-- они не могли разойтись при сбое на середине операции.
-- CHECK (balance >= 0) на ticket — единственная страховка на
-- уровне БД: она не даст балансу уйти в минус, даже если
-- приложение ошибётся в расчёте.
-- ============================================================

-- ============================================================
-- Комментарии к таблицам и колонкам
-- ============================================================

COMMENT ON TABLE airlines IS 'Авиакомпании — владельцы рейсов и пользователи личного кабинета';
COMMENT ON COLUMN airlines.id IS 'Внутренний идентификатор авиакомпании';
COMMENT ON COLUMN airlines.name IS 'Название авиакомпании';
COMMENT ON COLUMN airlines.login IS 'Логин для входа в личный кабинет авиакомпании';
COMMENT ON COLUMN airlines.password_hash IS 'Хеш пароля для входа в личный кабинет (пароль в открытом виде не хранится)';

COMMENT ON TABLE airports IS 'Аэропорты, между которыми выполняются рейсы';
COMMENT ON COLUMN airports.id IS 'Внутренний идентификатор аэропорта';
COMMENT ON COLUMN airports.name IS 'Название аэропорта';
COMMENT ON COLUMN airports.unique_code IS 'Уникальный код аэропорта в формате IATA (3 латинские буквы)';
COMMENT ON COLUMN airports.town IS 'Город, в котором расположен аэропорт';
COMMENT ON COLUMN airports.address IS 'Адрес аэропорта';

COMMENT ON TABLE flight IS 'Рейсы авиакомпаний между аэропортами';
COMMENT ON COLUMN flight.id IS 'Внутренний идентификатор рейса';
COMMENT ON COLUMN flight.company_id IS 'Авиакомпания, выполняющая рейс';
COMMENT ON COLUMN flight.airplane IS 'Тип/бортовой номер воздушного судна';
COMMENT ON COLUMN flight.airport_from IS 'Аэропорт вылета';
COMMENT ON COLUMN flight.airport_to IS 'Аэропорт прилёта';
COMMENT ON COLUMN flight.time_out IS 'Плановое время вылета';
COMMENT ON COLUMN flight.time_in IS 'Плановое время прилёта';
COMMENT ON COLUMN flight.delay_minutes IS 'Длительность задержки рейса в минутах (0 — рейс не задержан); используется для начисления компенсации по ФАП-82 и для отчётов';

COMMENT ON TABLE passenger IS 'Пассажиры, оформившие посадочные талоны';
COMMENT ON COLUMN passenger.id IS 'Внутренний идентификатор пассажира';
COMMENT ON COLUMN passenger.first_name IS 'Имя пассажира';
COMMENT ON COLUMN passenger.last_name IS 'Фамилия пассажира';
COMMENT ON COLUMN passenger.patronymic IS 'Отчество пассажира (если есть)';
COMMENT ON COLUMN passenger.passport_series IS 'Серия паспорта, 4 цифры';
COMMENT ON COLUMN passenger.passport_number IS 'Номер паспорта, 6 цифр';

COMMENT ON TABLE ticket IS 'Посадочные талоны — привязка пассажира к рейсу и его баланс на еду/напитки';
COMMENT ON COLUMN ticket.id IS 'Внутренний идентификатор талона';
COMMENT ON COLUMN ticket.ticket_number IS 'Публичный номер талона — используется в API и при сканировании в кафе, не совпадает с внутренним id';
COMMENT ON COLUMN ticket.flight_id IS 'Рейс, на который оформлен талон';
COMMENT ON COLUMN ticket.passenger_id IS 'Пассажир, которому принадлежит талон';
COMMENT ON COLUMN ticket.seat IS 'Номер места на рейсе';
COMMENT ON COLUMN ticket.balance IS 'Текущий остаток средств на талоне (кэш, актуальность поддерживается приложением на основе истории в transaction)';

COMMENT ON TABLE service_points IS 'Точки обслуживания (кафе и т.п.) в аэропортах, подключённые к сервису';
COMMENT ON COLUMN service_points.id IS 'Внутренний идентификатор точки обслуживания';
COMMENT ON COLUMN service_points.name IS 'Название точки обслуживания';
COMMENT ON COLUMN service_points.airport_id IS 'Аэропорт, в котором расположена точка обслуживания';
COMMENT ON COLUMN service_points.contact_phone IS 'Контактный телефон точки обслуживания';
COMMENT ON COLUMN service_points.is_active IS 'Признак того, что точка обслуживания сейчас подключена и работает';

COMMENT ON TABLE transaction IS 'История пополнений и списаний по талонам';
COMMENT ON COLUMN transaction.id IS 'Внутренний идентификатор транзакции';
COMMENT ON COLUMN transaction.ticket_id IS 'Талон, по которому проведена транзакция';
COMMENT ON COLUMN transaction.service_point_id IS 'Точка обслуживания, в которой совершена покупка (или через которую проведено пополнение)';
COMMENT ON COLUMN transaction.amount IS 'Сумма операции (всегда положительная, знак операции определяется полем type)';
COMMENT ON COLUMN transaction.type IS 'Тип операции: topup — пополнение при задержке рейса, purchase — покупка в точке обслуживания';
COMMENT ON COLUMN transaction.created_at IS 'Дата и время проведения операции';