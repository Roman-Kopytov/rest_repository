package ru.kopytov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kopytov.dto.LoginRequest;
import ru.kopytov.dto.SingupRequest;
import ru.kopytov.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/singin")
    public ResponseEntity<?> singin(@RequestBody LoginRequest loginRequest) {
        ResponseEntity<?> responseEntity = authService.authenticateUser(loginRequest);
        return responseEntity;
    }


    @PostMapping("/singup")
    public ResponseEntity<?> singup(@RequestBody SingupRequest singupRequest) {
        ResponseEntity<?> responseEntity = authService.registerUser(singupRequest);
        return responseEntity;
    }
}
