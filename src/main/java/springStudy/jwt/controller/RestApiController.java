package springStudy.jwt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RestApiController {

    @GetMapping("/home")
    public String home(){
        return "<h1>home</h1>";
    }
}
