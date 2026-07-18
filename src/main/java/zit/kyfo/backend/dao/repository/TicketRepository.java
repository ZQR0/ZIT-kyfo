package zit.kyfo.backend.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zit.kyfo.backend.dao.entity.TicketEntity;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, Integer> {

    Optional<TicketEntity> findByTicketNumber(String ticketNumber);

    List<TicketEntity> findByPassengerId(int passengerId);

    List<TicketEntity> findByFlightId(int flightId);

    Optional<TicketEntity> findByFlightIdAndSeat(int flightId, String seat);

    List<TicketEntity> findByFlightAirlineId(int airlineId);
}
