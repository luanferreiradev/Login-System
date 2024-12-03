package com.sistema.newloginspring.controller;

import com.sistema.newloginspring.model.User;
import com.sistema.newloginspring.repository.UserRepository;
import com.sistema.newloginspring.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Endpoint to create a new account
    @PostMapping("/create")
    public ResponseEntity<String> createAccount(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already registered.");
        }
        user.setPassword(crypt(user.getPassword()));
        userRepository.save(user);
        emailService.sendEmail(user.getEmail(), "Account Created", "Account created in Login System");
        return ResponseEntity.ok("Account successfully created.");
    }

    // Endpoint for login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getActive() && verifyPassword(password, user.getPassword())) {
            return ResponseEntity.ok("Login successful.");
        }
        return ResponseEntity.badRequest().body("Incorrect email or password.");
    }

    // Endpoint for password recovery
    @PostMapping("/recover-password")
    public ResponseEntity<String> recoverPassword(@RequestParam String email) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getActive()) {
            String resetLink = "http://localhost:8080/reset-password?email=" + email; // Password reset link
            String message = "Click the link to reset your password: " + resetLink;
            emailService.sendEmail(email, "Password Recovery", message);
            return ResponseEntity.ok("Recovery email sent to " + email);
        }
        return ResponseEntity.badRequest().body("User not found.");
    }

    // Endpoint to reset password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getActive()) {
            user.setPassword(crypt(newPassword));
            userRepository.save(user);
            emailService.sendEmail(email, "Password Reset", "Password successfully changed in Login System");
            return ResponseEntity.ok("Password successfully reset.");
        }
        return ResponseEntity.badRequest().body("User not found.");
    }

    // Endpoint to send support email
    @PostMapping("/send-support-email")
    public ResponseEntity<String> sendSupportEmail(@RequestParam String email, @RequestParam String subject, @RequestParam String message) {
        String supportEmail = "bankmalvader@gmail.com";
        String fullMessage = "Email from: " + email + "\n\n" + message;
        emailService.sendEmail(supportEmail, subject, fullMessage);
        return ResponseEntity.ok("Support email successfully sent.");
    }

    // Endpoint to list all users
    @GetMapping
    public ResponseEntity<List<User>> listUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Method to encrypt the password
    private String crypt(String password) {
        return passwordEncoder.encode(password);
    }

    // Method to verify the password
    private boolean verifyPassword(String password, String encryptedPassword) {
        return passwordEncoder.matches(password, encryptedPassword);
    }
}