package zit.kyfo.backend.dto.other;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestoreDto {
    private boolean success;
    private String message;
    private int ticketsRestored;
}