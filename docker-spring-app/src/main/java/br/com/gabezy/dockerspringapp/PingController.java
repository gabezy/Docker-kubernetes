package br.com.gabezy.dockerspringapp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class PingController {

    @GetMapping(value = "")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pinged development!!!");
    }

    @GetMapping(value = "/greetings")
    public ResponseEntity<String> grettins(@RequestParam("name") String name) {
        return ResponseEntity.ok("Hello " + name);
    }

}
