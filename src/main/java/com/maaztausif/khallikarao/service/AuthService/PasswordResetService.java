package com.maaztausif.khallikarao.service.AuthService;

//package com.maaztausif.khallikarao.config;

import com.maaztausif.khallikarao.entity.User;
import com.maaztausif.khallikarao.repository.AuthRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Locale;

@Service
public class PasswordResetService {

    private static final String REQUEST_MESSAGE =
            "If an eligible account exists, a reset code will be emailed.";

    private final AuthRepo repo;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String from;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(
            AuthRepo repo,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String from) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.from = from;
    }

    @Transactional
    public Result forgotPassword(String email) {
        if (email == null || email.isBlank()) {
            return new Result(false, "Email is required");
        }

        var existingUser = repo.findByEmailForUpdate(email.trim());

        // Same response for missing or unverified accounts.
        if (existingUser.isEmpty()
                || !existingUser.get().isEmailVerified()) {
            return new Result(true, REQUEST_MESSAGE);
        }

        User user = existingUser.get();
        Instant now = Instant.now();

        // Wait at least 60 seconds between emails.
        if (user.getPasswordResetOtpSentAt() != null
                && now.isBefore(
                user.getPasswordResetOtpSentAt().plusSeconds(60))) {
            return new Result(true, REQUEST_MESSAGE);
        }

        // Do not let resending bypass a locked challenge.
        if (user.getPasswordResetOtpAttempts() >= 5
                && user.getPasswordResetOtpExpiresAt() != null
                && now.isBefore(user.getPasswordResetOtpExpiresAt())) {
            return new Result(true, REQUEST_MESSAGE);
        }

        String otp = String.format(
                Locale.ROOT, "%06d", random.nextInt(1_000_000)
        );

        user.setPasswordResetOtpHash(passwordEncoder.encode(otp));
        user.setPasswordResetOtpExpiresAt(now.plusSeconds(300));
        user.setPasswordResetOtpSentAt(now);
        user.setPasswordResetOtpAttempts(0);

        repo.saveAndFlush(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(user.getEmail());
        message.setSubject("Reset your Khalli Karao password");
        message.setText(
                "Your password reset code is: " + otp
                        + "\n\nThis code expires in 5 minutes."
                        + "\nIf you did not request this, ignore this email."
        );

        mailSender.send(message);

        return new Result(true, REQUEST_MESSAGE);
    }

    @Transactional
    public Result resetPassword(
            String email,
            String otp,
            String newPassword) {

        if (email == null || email.isBlank()
                || otp == null || !otp.matches("[0-9]{6}")) {
            return new Result(false, "Email and a 6-digit OTP are required");
        }

        // BCrypt supports passwords up to 72 UTF-8 bytes.
        if (newPassword == null
                || newPassword.isBlank()
                || newPassword.length() < 12
                || newPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            return new Result(
                    false,
                    "Use a password with at least 12 characters and at most 72 UTF-8 bytes"
            );
        }

        var existingUser = repo.findByEmailForUpdate(email.trim());

        if (existingUser.isEmpty()) {
            return invalidCode();
        }

        User user = existingUser.get();

        if (!user.isEmailVerified()
                || user.getPasswordResetOtpHash() == null
                || user.getPasswordResetOtpExpiresAt() == null
                || !Instant.now().isBefore(user.getPasswordResetOtpExpiresAt())
                || user.getPasswordResetOtpAttempts() >= 5) {
            return invalidCode();
        }

        if (!passwordEncoder.matches(
                otp, user.getPasswordResetOtpHash())) {

            user.setPasswordResetOtpAttempts(
                    user.getPasswordResetOtpAttempts() + 1
            );
            repo.save(user);

            return invalidCode();
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        // Consume the code so it cannot be reused.
        user.setPasswordResetOtpHash(null);
        user.setPasswordResetOtpExpiresAt(null);
        user.setPasswordResetOtpAttempts(0);

        // Invalidate previously issued JWTs.
        user.setTokenVersion(user.getTokenVersion() + 1);

        repo.save(user);

        return new Result(
                true, "Password reset successfully. Please log in."
        );
    }

    private Result invalidCode() {
        return new Result(
                false,
                "Invalid, expired, or blocked reset code. Request a new code."
        );
    }

    public record Result(boolean status, String message) {
    }
}