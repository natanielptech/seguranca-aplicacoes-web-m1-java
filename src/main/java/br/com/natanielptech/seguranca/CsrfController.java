package br.com.natanielptech.seguranca;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @GetMapping("/csrf")
    CsrfToken csrfToken(CsrfToken csrfToken) {
        return csrfToken;
    }
}
