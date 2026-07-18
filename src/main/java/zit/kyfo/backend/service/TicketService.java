package zit.kyfo.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zit.kyfo.backend.dao.entity.AirlinesEntity;
import zit.kyfo.backend.dao.entity.FlightEntity;
import zit.kyfo.backend.dao.entity.TicketEntity;
import zit.kyfo.backend.dao.repository.TicketRepository;
import zit.kyfo.backend.dto.other.PaymentResponseDto;
import zit.kyfo.backend.dto.other.RestoreDto;
import zit.kyfo.backend.dto.other.TopUpProcessDto;
import zit.kyfo.backend.dto.ticket.TicketDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    private final AirlinesService airlinesService;
    private final FlightsService flightsService;
    private final TransactionService transactionService;

    public TicketDto findById(int id) {
        TicketEntity entity = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Билет с id " + id + " не найден"));
        return mapToTicketDto(entity);
    }

    public TicketDto findByTicketNumber(String ticketNumber) {
        TicketEntity entity = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Билет с кодом " + ticketNumber + " не найден"));
        return mapToTicketDto(entity);
    }

    private List<TicketDto> getTicketsByFlightId(int flightId) {
        FlightEntity flightEntity = flightsService.findEntityById(flightId);

        List<TicketEntity> tickets = ticketRepository.findByFlightId(flightEntity.getId());

        return tickets.stream()
                .map(this::mapToTicketDto)
                .collect(Collectors.toList());
    }

    public List<TicketDto> getTicketsByAirlineId(int airlineId) {
        AirlinesEntity airlinesEntity = airlinesService.findEntityById(airlineId);

        List<TicketEntity> tickets = ticketRepository.findByFlightAirlineId(airlinesEntity.getId());

        return tickets.stream()
                .map(this::mapToTicketDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TopUpProcessDto processTopUp(int flightId, BigDecimal amount) {
        FlightEntity flightEntity = flightsService.findEntityById(flightId);

        List<TicketEntity> tickets = ticketRepository.findByFlightId(flightId);

        if (tickets.isEmpty()) {
            throw new RuntimeException("На рейс нет билетов");
        }

        for (TicketEntity ticket : tickets) {
            ticket.setBalance(ticket.getBalance().add(amount));
            ticketRepository.save(ticket);

            transactionService.createTopUpTransaction(ticket, amount);
        }

        TopUpProcessDto response = new TopUpProcessDto();
        response.setSuccess(true);
        response.setMessage("Компенсация начислена на " + tickets.size() + " талонов");
        response.setAmount(amount);
        response.setTicketsAffected(tickets.size());

        return response;
    }

    @Transactional
    public PaymentResponseDto processPayment(String ticketNumber, BigDecimal amount) {
        TicketEntity ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Талон с номером " + ticketNumber + " не найден"));

        if (ticket.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств на балансе");
        }

        ticket.setBalance(ticket.getBalance().subtract(amount));
        ticketRepository.save(ticket);

        transactionService.createPurchaseTransaction(ticket, amount);

        PaymentResponseDto response = new PaymentResponseDto();
        response.setSuccess(true);
        response.setMessage("Оплата прошла успешно");
        response.setTicketNumber(ticketNumber);
        response.setAmount(amount);
        response.setNewBalance(ticket.getBalance());

        return response;
    }

    @Transactional
    public RestoreDto restoreFlightPayments(int flightId) {
        flightsService.findEntityById(flightId);

        List<TicketEntity> tickets = ticketRepository.findByFlightId(flightId);

        if (tickets.isEmpty()) {
            throw new RuntimeException("На рейс нет билетов");
        }

        int restoredCount = 0;

        for (TicketEntity ticket : tickets) {
            BigDecimal currentBalance = ticket.getBalance();

            if (currentBalance.compareTo(BigDecimal.ZERO) > 0) {
                transactionService.createPurchaseTransaction(ticket, currentBalance.negate());

                ticket.setBalance(BigDecimal.ZERO);
                ticketRepository.save(ticket);

                restoredCount++;
            }
        }

        RestoreDto response = new RestoreDto();
        response.setSuccess(true);
        response.setMessage("Начисления откачены для " + restoredCount + " талонов");
        response.setTicketsRestored(restoredCount);

        return response;
    }

    @Transactional
    public RestoreDto restoreTicketPayment(String ticketNumber) {
        TicketEntity ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Талон с номером " + ticketNumber + " не найден"));

        BigDecimal currentBalance = ticket.getBalance();

        if (currentBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Баланс уже нулевой");
        }

        transactionService.createPurchaseTransaction(ticket, currentBalance.negate());

        ticket.setBalance(BigDecimal.ZERO);
        ticketRepository.save(ticket);

        RestoreDto response = new RestoreDto();
        response.setSuccess(true);
        response.setMessage("Начисление откачено для талона " + ticketNumber);
        response.setTicketsRestored(1);

        return response;
    }

    private TicketDto mapToTicketDto(TicketEntity entity) {
        TicketDto dto = new TicketDto();
        dto.setId(entity.getId());
        dto.setTicketNumber(entity.getTicketNumber());
        dto.setPassengerId(entity.getPassenger().getId());
        dto.setFlightId(entity.getFlight().getId());
        dto.setSeat(entity.getSeat());
        dto.setBalance(entity.getBalance());
        return dto;
    }
}
