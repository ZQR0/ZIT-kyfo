package zit.kyfo.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zit.kyfo.backend.dao.entity.AirlinesEntity;
import zit.kyfo.backend.dao.entity.FlightEntity;
import zit.kyfo.backend.dao.entity.TicketEntity;
import zit.kyfo.backend.dao.repository.AirlinesRepository;
import zit.kyfo.backend.dao.repository.FlightRepository;
import zit.kyfo.backend.dao.repository.TicketRepository;
import zit.kyfo.backend.dto.airlines.AirlinesDto;
import zit.kyfo.backend.dto.flights.FlightDto;
import zit.kyfo.backend.dto.ticket.TicketDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirlinesService {

    private final AirlinesRepository airlinesRepository;

    public AirlinesDto findById(int id) {
        AirlinesEntity entity = airlinesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Авиакомпания с id " + id + " не найдена"));
        return mapToDto(entity);
    }

    public AirlinesEntity findEntityById(int id) {
        return airlinesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Авиакомпания с id " + id + " не найдена"));
    }

    private AirlinesDto mapToDto(AirlinesEntity entity) {
        AirlinesDto dto = new AirlinesDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLogin(entity.getLogin());
        return dto;
    }
}
