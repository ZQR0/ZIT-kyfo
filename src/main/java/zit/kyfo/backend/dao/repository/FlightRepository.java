package zit.kyfo.backend.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zit.kyfo.backend.dao.entity.AirlinesEntity;
import zit.kyfo.backend.dao.entity.AirportsEntity;
import zit.kyfo.backend.dao.entity.FlightEntity;

import java.util.List;

public interface FlightRepository extends JpaRepository<FlightEntity, Integer> {

    List<FlightEntity> findByAirline(AirlinesEntity airline);

    List<FlightEntity> findByAirportFrom(AirportsEntity airportFrom);

    List<FlightEntity> findByAirportTo(AirportsEntity airportTo);

}
