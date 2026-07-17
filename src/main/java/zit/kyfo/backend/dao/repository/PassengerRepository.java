package zit.kyfo.backend.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zit.kyfo.backend.dao.entity.PassengerEntity;

import java.util.List;
import java.util.Optional;

public interface PassengerRepository extends JpaRepository<PassengerEntity, Integer> {

    Optional<PassengerEntity> findByPassportSeriesAndPassportNumber(String series, String number);

    List<PassengerEntity> findByFirstNameAndLastNameAndPatronymic(String firstName, String lastName, String patronymic);

    List<PassengerEntity> findByFirstNameAndLastName(String firstName, String lastName);

}
