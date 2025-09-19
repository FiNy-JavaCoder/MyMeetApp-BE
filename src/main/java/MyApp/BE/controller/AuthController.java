// Vytvoření REST API pro autentifikaci (základní verze)
// src/main/java/MyApp/BE/controller/AuthController.java
package MyApp.BE.controller;

import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.dto.RegistrationDTO;
import MyApp.BE.service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDTO registrationDTO) {
        try {
            return userService.registerUser(registrationDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Chyba při registraci: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Implementujte přihlašovací logiku
            // Pro demonstraci vrátíme úspěch
            return ResponseEntity.ok().body(new LoginResponse("success", "Přihlášení úspěšné"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Chyba při přihlášení: " + e.getMessage()));
        }
    }

    // Pomocné třídy pro přihlášení
    public static class LoginRequest {
        private String email;
        private String password;

        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String status;
        private String message;

        public LoginResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        // Getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}