package webavanzada.pucmm.practica6.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import webavanzada.pucmm.practica6.encapsulations.AuthRequest;
import webavanzada.pucmm.practica6.encapsulations.AuthResponse;
import webavanzada.pucmm.practica6.services.AuthService;

import java.util.Optional;

@Controller()
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView getLoginPage(@RequestParam Optional<String> error) {
        return new ModelAndView("login", "error", error);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

}