package oauth2.oauth2study.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    private OAuth2AuthenticationToken home(final OAuth2AuthenticationToken token) {
        return token;
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }
}
