package springboot.login.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MessagesController {

    @GetMapping("/messages")
    public ResponseEntity<List<String>> messages() {
        return ResponseEntity
                .ok(Arrays.asList("first", "second"));
    }
}
