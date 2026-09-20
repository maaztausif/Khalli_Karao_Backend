package com.maaztausif.khallikarao.service.AuthService.impl;

import com.maaztausif.khallikarao.dto.request.LoginRequest;
import com.maaztausif.khallikarao.dto.request.SignupRequest;
import com.maaztausif.khallikarao.dto.response.LoginResponse;
import com.maaztausif.khallikarao.dto.response.SignupResponse;
import com.maaztausif.khallikarao.entity.User;
import com.maaztausif.khallikarao.repository.AuthRepo;
import com.maaztausif.khallikarao.service.AuthService.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    public AuthRepo repo;

    @Override
    public LoginResponse login(LoginRequest request) {
        System.out.println("testet----=-=-=-=--=");
        System.out.println(request.getEmail());
        User user = repo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
        return  null;
    }

    @Override
    public SignupResponse registerUser(SignupRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmailAddress());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPassword(request.getPassword());

        Optional<User> existingUser = repo.findByEmail(request.getEmailAddress())
//                .orElseThrow(()-> new RuntimeException("user not found"));

        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User saved = repo.save(user);

        return new SignupResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getFullName(),
                saved.getPassword()
        );
    }
}
