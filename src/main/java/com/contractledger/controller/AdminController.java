package com.contractledger.controller;

import com.contractledger.model.User;
import com.contractledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id",       user.getId());
            map.put("username", user.getUsername());
            map.put("fullName", user.getFullName() != null ? user.getFullName() : "");
            map.put("phone",    user.getPhone() != null ? user.getPhone() : "");
            map.put("role",     user.getRole().name());
            map.put("active",   user.isActive());
            map.put("createdAt", user.getCreatedAt() != null
                    ? user.getCreatedAt().toLocalDate().toString() : "");
            return map;
        }).collect(Collectors.toList());
    }

    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<?> toggleUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            user.setActive(!user.isActive());
            userRepository.save(user);
            return ResponseEntity.ok("User " + (user.isActive() ? "enabled" : "disabled"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id,
                                            @RequestBody Map<String, String> body) {
        String newPassword = body.get("password");
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters");
        }

        return userRepository.findById(id).map(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return ResponseEntity.ok("Password updated successfully");
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted");
    }
}
