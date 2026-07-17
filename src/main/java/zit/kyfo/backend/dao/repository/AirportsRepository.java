package zit.kyfo.backend.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zit.kyfo.backend.dao.entity.AirportsEntity;

import java.util.List;
import java.util.Optional;

public interface AirportsRepository extends JpaRepository<AirportsEntity, Integer> {

    Optional<AirportsEntity> findByUniqueCode(String uniqueCode);

    List<AirportsEntity> findByTown(String town);

    boolean existsByUniqueCode(String uniqueCode);
}
