package zit.kyfo.backend.dto.transaction;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Integer id;
    private Integer ticketId;
    private Integer servicePointId;
    private BigDecimal amount;
    private String type;
    private LocalDateTime createdAt;
}