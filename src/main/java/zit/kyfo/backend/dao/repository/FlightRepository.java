package zit.kyfo.backend.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zit.kyfo.backend.dao.entity.AirlinesEntity;
import zit.kyfo.backend.dao.entity.AirportsEntity;
import zit.kyfo.backend.dao.entity.FlightEntity;
import zit.kyfo.backend.dto.FinanceReporting.DelayReasonDto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<FlightEntity, Integer> {

    List<FlightEntity> findByAirline(AirlinesEntity airline);

    List<FlightEntity> findByAirportFrom(AirportsEntity airportFrom);

    List<FlightEntity> findByAirportTo(AirportsEntity airportTo);

    @Query("""
            SELECT AVG(f.delayMinutes)
            FROM flight_entity f
                        WHERE f.timeOut
                        BETWEEN :start AND :end
            """)
    Integer averageDelayByPeriod(@Param("start") ZonedDateTime start, @Param("end") ZonedDateTime end);

    @Query("""
            SELECT
                CASE
                    WHEN COUNT(f) = 0 THEN 0
                    ELSE ROUND((COUNT(CASE WHEN f.delayMinutes = 0 THEN 1 END) * 100.0 / COUNT(f)), 2)
                END
            FROM flight_entity f
            WHERE f.airline.id = :airlineId
            """)
    BigDecimal percentOnTimeByAirline(int airlineId);

    @Query("""
            SELECT NEW zit.kyfo.backend.dto.FinanceReporting.DelayReasonDto(
                 f.reasonDelay,
                 COUNT(f)
            )
            FROM flight_entity f
            WHERE f.delayMinutes > 0 AND f.reasonDelay IS NOT NULL
            GROUP BY f.reasonDelay
            ORDER BY COUNT(f) DESC
            """)
    List<DelayReasonDto> mostCommonDelayReasons();

    List<FlightEntity> findByDelayMinutesGreaterThanEqualOrderByDelayMinutesDesc(int delayMinutes);
}
