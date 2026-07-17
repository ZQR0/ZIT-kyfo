package zit.kyfo.backend.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zit.kyfo.backend.dao.entity.TransactionEntity;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {

    List<TransactionEntity> findByTicketId(Integer ticketId);

    List<TransactionEntity> findByServicePointId(Integer servicePointId);
}
