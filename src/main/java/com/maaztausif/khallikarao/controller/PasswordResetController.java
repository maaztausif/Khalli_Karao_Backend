package com.maaztausif.khallikarao.controller;
import com.maaztausif.khallikarao.service.password.PasswordResetService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final PasswordResetService service;

    public PasswordResetController(PasswordResetService service) {
        this.service = service;
    }

    @PostMapping("/forgot-password")
    public PasswordResetService.Result forgotPassword(
            @RequestBody ForgotPasswordRequest request) {
        return service.forgotPassword(request.email());
    }

    @PostMapping("/reset-password")
    public PasswordResetService.Result resetPassword(
            @RequestBody ResetPasswordRequest request) {
        return service.resetPassword(
                request.email(),
                request.otp(),
                request.newPassword()
        );
    }

    public record ForgotPasswordRequest(String email) {
    }

    public record ResetPasswordRequest(
            String email,
            String otp,
            String newPassword) {
    }
    @PostMapping("/send-reset-otp")
    public PasswordResetService.Result sendResetOtp(@RequestBody ForgotPasswordRequest request){
        return service.forgotPassword(request.email);
    }
}