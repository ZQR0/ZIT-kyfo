package zit.kyfo.backend.dto.passenger;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDto {

    private Integer id;
    private String firstName;
    private String lastName;
    private String patronymic;
}