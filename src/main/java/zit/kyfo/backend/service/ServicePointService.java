package zit.kyfo.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zit.kyfo.backend.dao.entity.ServicePointEntity;
import zit.kyfo.backend.dao.repository.ServicePointRepository;
import zit.kyfo.backend.dto.servicePoints.ServicePointDto;

@Service
@RequiredArgsConstructor
public class ServicePointService {

    private ServicePointRepository servicePointRepository;

    @Transactional
    public ServicePointDto validatePoint(int pointId) {
        ServicePointEntity point = servicePointRepository.findById(pointId)
                .orElseThrow(() -> new RuntimeException("Точка обслуживания с id " + pointId + " не найдена"));

        point.setActive(true);
        ServicePointEntity saved = servicePointRepository.save(point);

        return mapToDto(saved);
    }


    private ServicePointDto mapToDto(ServicePointEntity entity) {
        ServicePointDto dto = new ServicePointDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAirportCode(entity.getAirport().getUniqueCode());
        dto.setContactPhone(entity.getContactPhone());
        dto.setActive(entity.isActive());
        return dto;
    }
}
