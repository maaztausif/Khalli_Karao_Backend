package com.maaztausif.khallikarao.service.AuthService.impl;

import com.maaztausif.khallikarao.config.EmailOtpService;
import com.maaztausif.khallikarao.config.JwtService;
import com.maaztausif.khallikarao.dto.request.LoginRequest;
import com.maaztausif.khallikarao.dto.request.SignupRequest;
import com.maaztausif.khallikarao.dto.response.LoginResponse;
import com.maaztausif.khallikarao.dto.response.SignupResponse;
import com.maaztausif.khallikarao.entity.User;
import com.maaztausif.khallikarao.repository.AuthRepo;
import com.maaztausif.khallikarao.service.AuthService.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmailOtpService emailOtpService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    public AuthRepo repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        Optional<User> existingUser = repo.findByEmail(request.getEmail());

        if(request.getPassword() == null || request.getPassword() == null ){
            return new LoginResponse(false,"Email and password are required",null);
        }
        if (existingUser.isEmpty()) {
            return new LoginResponse(
                    false, "Invalid email or password", null
            );
        }

        User user = existingUser.get();

        if(!passwordEncoder.matches(
                request.getPassword(), user.getPassword()
        )){

            return new LoginResponse(
                    false, "Invalid email or password", null
            );
        }
        String token = jwtService.generateToken(user.getId());

        return new LoginResponse(
                true,
                "User is present",
                new LoginResponse.UserData(
                        user.getId(),
                        user.getEmail(),
                        user.getFullName(),
                        token
                )
        );
    }

    @Override
    public SignupResponse registerUser(SignupRequest request) {

        // Getting existing User
        Optional<User> existingUser = repo.findByEmail(request.getEmailAddress());
//                .orElseThrow(()-> new RuntimeException("user not found"));
        if (existingUser.isPresent()) {
            return new SignupResponse(
                    false,
                    "email is already registered",
                    false,
                    null
            );
        }

// Saving New User
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmailAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = repo.save(user);

        return new SignupResponse(
                true,
                "Registration Successful",
                false,
                new SignupResponse.UserData(
                        user.getId(),
                        user.getEmail(),
                        user.getFullName()
                )
        );


    }
}
