package zit.kyfo.backend.dto.airlines;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirlinesDto {

    private Integer id;
    private String name;
    private String login;
}