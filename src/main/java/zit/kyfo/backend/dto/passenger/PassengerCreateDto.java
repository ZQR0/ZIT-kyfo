package zit.kyfo.backend.dto.passenger;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PassengerCreateDto {
    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    private String patronymic;

    @NotNull
    @Pattern(regexp = "^[0-9]{4}$")
    private String passportSeries;

    @NotNull
    @Pattern(regexp = "^[0-9]{6}$")
    private String passportNumber;
}
