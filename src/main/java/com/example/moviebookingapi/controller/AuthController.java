package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.dto.LoginRequest;
import com.example.moviebookingapi.dto.LoginResponse;
import com.example.moviebookingapi.dto.RegisterRequest;
import com.example.moviebookingapi.dto.RegisterResponse;
import com.example.moviebookingapi.model.User;
import com.example.moviebookingapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        String token = authService.login(loginRequest.getEmail(),  loginRequest.getPassword());

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request.getEmail(), request.getPassword());

        RegisterResponse response = new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                "User registered successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
