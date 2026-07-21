package zit.kyfo.backend.dto.flights;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDto {

    private Integer id;
    private String airlineName;
    private String airplane;
    private String airportFrom;
    private String airportTo;
    private ZonedDateTime timeOut;
    private ZonedDateTime timeIn;
    private Integer delayMinutes;
    private String reasonDelay;
    private boolean delayed;
}