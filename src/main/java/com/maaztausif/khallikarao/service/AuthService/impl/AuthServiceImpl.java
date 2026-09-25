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
            return new LoginResponse(false,"Email and password are required",false,null);
        }
        if (existingUser.isEmpty()) {
            return new LoginResponse(
                    false, "Invalid email or password", false,null
            );
        }

        User user = existingUser.get();

        if (!user.isEmailVerified()){
            return new LoginResponse(
                    false, "Email is not verified!", false,null
            );
        }
        if(!passwordEncoder.matches(
                request.getPassword(), user.getPassword()
        )){

            return new LoginResponse(
                    false,"Invalid email or password" ,false, null
            );
        }
        String token = jwtService.generateToken(user.getId());

        return new LoginResponse(
                true,
                "User is present",
                user.isEmailVerified(),
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

        // Validation
        if(request.getEmailAddress() == null
                || request.getEmailAddress().isBlank()
                || request.getFullName() == null
                || request.getFullName().isBlank()
                || request.getPassword() == null
                || request.getPassword().isBlank()){
            return new SignupResponse(false,"All fields are req",false,null);

        }

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


        emailOtpService.sendSignupOtp(saved);

        return new SignupResponse(
                true,
                "Signup successful. Check your email for the OTP.",
                false,
                new SignupResponse.UserData(
                        user.getId(),
                        user.getEmail(),
                        user.getFullName()
                )
        );


    }
}
