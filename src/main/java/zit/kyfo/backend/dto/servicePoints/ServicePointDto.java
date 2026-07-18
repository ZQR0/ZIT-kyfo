package zit.kyfo.backend.dto.servicePoints;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicePointDto {
    private Integer id;
    private String name;
    private String airportCode;
    private String contactPhone;
    private Boolean active;
}