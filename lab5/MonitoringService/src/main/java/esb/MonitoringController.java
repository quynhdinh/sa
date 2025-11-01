package esb;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitoringController {
    @PostMapping("/ping")
    public ResponseEntity<?> log(@RequestBody MessageDTO message) {
    
        System.out.println(message.source() + " monitoring: " + message.order());
        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }
}
record MessageDTO(String source, Order order) {}

