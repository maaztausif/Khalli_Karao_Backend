package com.maaztausif.khallikarao.config;

import com.maaztausif.khallikarao.entity.User;
import com.maaztausif.khallikarao.repository.AuthRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Locale;

@Service
public class EmailOtpService {

    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final AuthRepo repo;
    private final String from;
    private final SecureRandom random = new SecureRandom();

    public EmailOtpService(
            JavaMailSender mailSender,
            PasswordEncoder passwordEncoder,
            AuthRepo repo,
            @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.repo = repo;
        this.from = from;
    }

    @Transactional
    public void sendSignupOtp(User user) {
        String otp = String.format(
                Locale.ROOT, "%06d", random.nextInt(1_000_000)
        );

        user.setEmailVerified(false);
        user.setOtpHash(passwordEncoder.encode(otp));
        user.setOtpExpiresAt(Instant.now().plusSeconds(300));
        user.setOtpAttempts(0);

        repo.saveAndFlush(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(user.getEmail());
        message.setSubject("Verify your Khalli Karao email");
        message.setText(
                "Your verification code is: " + otp
                        + "\n\nThis code expires in 5 minutes."
                        + "\nIf you did not request this, ignore this email."
        );

        mailSender.send(message);
    }

    @Transactional
    public VerificationResult sendOtp(String email) {
        if (email == null || email.isBlank()) {
            return new VerificationResult(false, "Email is required");
        }

        var existingUser = repo.findByEmailForUpdate(email.trim());

        if (existingUser.isEmpty()) {
            return new VerificationResult(false, "User not found");
        }

        User user = existingUser.get();

        if (user.isEmailVerified()) {
            return new VerificationResult(
                    false, "Email is already verified"
            );
        }

        // Your OTP lasts 300 seconds; allow resending after 60 seconds.
        if (user.getOtpExpiresAt() != null) {
            Instant nextAllowedSend =
                    user.getOtpExpiresAt().minusSeconds(240);

            if (Instant.now().isBefore(nextAllowedSend)) {
                return new VerificationResult(
                        false,
                        "Please wait 60 seconds before requesting another OTP"
                );
            }
        }

        sendSignupOtp(user);

        return new VerificationResult(
                true, "OTP sent. It expires in 5 minutes."
        );
    }

    @Transactional
    public VerificationResult verify(String email, String otp) {
        if (email == null || email.isBlank()
                || otp == null || !otp.matches("[0-9]{6}")) {
            return new VerificationResult(
                    false, "Email and a 6-digit OTP are required"
            );
        }

        var existingUser = repo.findByEmailForUpdate(email.trim());

        if (existingUser.isEmpty()) {
            return new VerificationResult(
                    false, "Invalid verification details"
            );
        }

        User user = existingUser.get();

        if (user.isEmailVerified()) {
            return new VerificationResult(
                    false, "Email is already verified"
            );
        }

        if (user.getOtpHash() == null
                || user.getOtpExpiresAt() == null
                || !Instant.now().isBefore(user.getOtpExpiresAt())) {
            return new VerificationResult(
                    false, "OTP expired. Please request a new code."
            );
        }

        if (user.getOtpAttempts() >= 5) {
            return new VerificationResult(
                    false, "Too many incorrect attempts. Request a new code."
            );
        }

        if (!passwordEncoder.matches(otp, user.getOtpHash())) {
            user.setOtpAttempts(user.getOtpAttempts() + 1);
            repo.save(user);

            return new VerificationResult(false, "Incorrect OTP");
        }

        user.setEmailVerified(true);
        user.setOtpHash(null);
        user.setOtpExpiresAt(null);
        user.setOtpAttempts(0);
        repo.save(user);

        return new VerificationResult(
                true, "Email verified successfully"
        );
    }

    public record VerificationResult(boolean status, String message) {
    }
}