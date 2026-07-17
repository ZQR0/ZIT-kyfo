package zit.kyfo.backend.controller;

import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Авиалинии", description = "API для управления авиалиниями")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/airlines")
public class AirlinesController {

    @Operation(summary = "Список рейсов", description = "Возвращает список всех рейсов")
    @ApiResponse(responseCode = "200", description = "Рейсы успешно получены")
    @GetMapping("/flights")
    public Object getFlights() {
        return null;
    }

    @Operation(summary = "Список талонов", description = "Возвращает список всех талонов")
    @ApiResponse(responseCode = "200", description = "Талоны успешно получены")
    @GetMapping("/tickets")
    public Object getTickets() {
        return null;
    }

    @Operation(summary = "Получить рейс по id", description = "Возвращение рейса по его индивидуальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Рейс успешно найден"),
            @ApiResponse(responseCode = "404", description = "Рейс не найден")
    })
    @GetMapping("/flights/{id}")
    public Object getFlightById(@PathVariable("id") Integer id) {
        return null;
    }

    @Operation(summary = "Получить талон по номеру билета", description = "")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Талон успешно найден"),
            @ApiResponse(responseCode = "404", description = "Талон не найден")
    })
    @GetMapping("/tickets/{ticketNumber}")
    public Object getTicketByNumber(@PathVariable("ticketNumber") String ticketNumber) {
        return null;
    }

    @Operation(summary = "Все талоны на посадку для конкретного рейса", description = "Возвращает все талоны на посадку для рейса с определенным идентификатором")
    @GetMapping("/flights/{id}/boardingPasses")
    public Object getBoardingPasses(@PathVariable("id") Integer id) {
        return null;
    }

    @Operation(summary = "Отчетность", description = "Основные показатели системы")
    @GetMapping("/reports")
    public Object getReports() {
        return null;
    }

    @Operation(summary = "", description = "")
    @PostMapping("/flights/{id}/payment/process")
    public Object processPayment(@PathVariable("id") Integer id, @RequestBody Object amount) {
        return null;
    }

    @Operation(summary = "", description = "")
    @PutMapping("/validatePoint")
    public Object validatePoint(@RequestParam("pointId") Integer pointId) {
        return null;
    }

    @Operation(summary = "", description = "")
    @DeleteMapping("/flights/{id}/payment/restore")
    public Object restoreFlightPayment(@PathVariable("id") Integer id) {
        return null;
    }

    @Operation(summary = "", description = "")
    @DeleteMapping("/ticket/{ticketNumber}/payment/restore")
    public Object restoreTicketPayment(@PathVariable("ticketNumber") String ticketNumber) {
        return null;
    }
}
