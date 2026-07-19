package zit.kyfo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zit.kyfo.backend.dto.other.PaymentResponseDto;
import zit.kyfo.backend.service.FlightsService;
import zit.kyfo.backend.service.ServicePointService;
import zit.kyfo.backend.service.TicketService;

import java.math.BigDecimal;

@Tag(name = "Точки продаж", description = "API для управления точками продаж")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class ServicePointsController {

    private final FlightsService flightsService;
    private final TicketService ticketService;
    private final ServicePointService servicePointService;

    @Operation(summary = "Проверить баланс по номеру билета",
            description = "Возвращает баланс определенного билета по его номеру")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Талон успешно найден"),
            @ApiResponse(responseCode = "404", description = "Талон не найден")
    })
    @GetMapping("/checkBalance")
    public ResponseEntity<BigDecimal> checkBalance(@RequestParam("ticketNumber") String ticketNumber) {
        return ResponseEntity.ok(ticketService.checkBalance(ticketNumber));
    }
    //

    @Operation(summary = "Списать сумму с посадочного талона",
            description = "Выполняет списание денежных средств с баланса, привязанного к конкретному посадочному талону")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Талон успешно найден. Списание выполнено успешно. Сумма списана с баланса"),
            @ApiResponse(responseCode = "404", description = "Талон не найден"),
            @ApiResponse(responseCode = "409", description = "Недостаточно средств на балансе для списания (или талон уже аннулирован).")
    })
    @PostMapping("/pay")
    public ResponseEntity<PaymentResponseDto> pay(@RequestBody PaymentResponseDto request) {
        PaymentResponseDto response = ticketService.processPayment(
                request.getTicketNumber(),
                request.getAmount()
        );
        return ResponseEntity.ok(request);
    }
}
