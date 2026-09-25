package com.maaztausif.khallikarao.controller;

import com.maaztausif.khallikarao.config.EmailOtpService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class EmailVerificationController {

    private final EmailOtpService emailOtpService;

    public EmailVerificationController(EmailOtpService emailOtpService) {
        this.emailOtpService = emailOtpService;
    }

    @PostMapping("/verify-email")
    public EmailOtpService.VerificationResult verify(
            @RequestBody VerifyEmailRequest request) {
        return emailOtpService.verify(request.email(), request.otp());
    }

    @PostMapping("/send-otp")
    public EmailOtpService.VerificationResult sendOtp(
            @RequestBody SendOtpRequest request) {
        return emailOtpService.sendOtp(request.email());
    }

    public record SendOtpRequest(String email) {
    }

    public record VerifyEmailRequest(String email, String otp) {
    }
}