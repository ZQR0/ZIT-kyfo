package zit.kyfo.backend.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "transaction_entity")
@Table(name = "transaction")
@NoArgsConstructor
@Setter
@Getter
public class TransactionEntity extends AbstractEntity<Integer> implements Serializable {

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @ManyToOne
    @JoinColumn(name = "service_point_id", nullable = false)
    private ServicePointEntity servicePoint;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public enum Type {topUp, purchase}

    public TransactionEntity(BigDecimal amount, LocalDateTime createdAt, ServicePointEntity servicePoint, TicketEntity ticket, Type type) {
        setAmount(amount);
        setServicePoint(servicePoint);
        setCreatedAt(createdAt);
        setType(type);
        setTicket(ticket);
    }
}
