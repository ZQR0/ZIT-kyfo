package zit.kyfo.backend.dto.ticket;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private Integer id;
    private String ticketNumber;
    private Integer passengerId;
    private Integer flightId;
    private String seat;
    private BigDecimal balance;
}