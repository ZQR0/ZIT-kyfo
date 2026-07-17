package zit.kyfo.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/points")
public class ServicePointsController {

    @GetMapping("/checkBalance")
    public Object checkBalance(@RequestParam("ticketNumber") String ticketNumber) {
        return null;
    }

    @PostMapping("/pay")
    public Object pay(@RequestBody Object request) {
        return null;
    }
}
