package zit.kyfo.backend.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zit.kyfo.backend.dao.entity.TicketEntity;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, Integer> {

    Optional<TicketEntity> findByTicketNumber(String ticketNumber);

    List<TicketEntity> findByPassengerId(Integer passengerId);

    List<TicketEntity> findByFlightId(Integer flightId);

    Optional<TicketEntity> findByFlightIdAndSeat(Integer flightId, String seat);
}
