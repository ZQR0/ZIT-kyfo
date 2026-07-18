package zit.kyfo.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zit.kyfo.backend.dao.repository.AirportsRepository;

@Service
@RequiredArgsConstructor
public class AirportsService {

    private final AirportsRepository airportsRepository;
}
