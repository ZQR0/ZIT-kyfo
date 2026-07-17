package zit.kyfo.backend.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zit.kyfo.backend.dao.entity.ServicePointEntity;

import java.util.List;

public interface ServicePointRepository extends JpaRepository<ServicePointEntity, Integer> {

    List<ServicePointEntity> findByActiveTrue();

    List<ServicePointEntity> findByAirportId(Integer airportId);

    List<ServicePointEntity> findByAirportIdAndActiveTrue(Integer airportId);
}
