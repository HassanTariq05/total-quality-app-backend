package com.i2p.accreditations.controller.accessModule;

import com.i2p.accreditations.Request.LoginRequest;
import com.i2p.accreditations.dto.RegisterUserRequestDto;
import com.i2p.accreditations.model.access.User;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.repository.access.UserRepository;
import com.i2p.accreditations.repository.organisation.OrganisationRepository;
import com.i2p.accreditations.service.access.MyUserDetailsService;
import com.i2p.accreditations.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private MyUserDetailsService userDetailsService;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequestDto req) {

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body(
                    Map.of("success", false, "message", "Email already in use")
            );
        }

        Organisation organisation = organisationRepository.findById(req.getOrganisationId())
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setOrganisation(organisation);

        User savedUser = userRepo.save(user);

        final String token = jwtUtil.generateToken(savedUser);

        Map<String, Object> userData = Map.of(
                "id", savedUser.getId(),
                "name", savedUser.getName(),
                "email", savedUser.getEmail(),
                "organisation", Map.of(
                        "id", organisation.getId(),
                        "name", organisation.getName(),
                        "status", organisation.getStatus()
                )
        );

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "User registered successfully",
                "user", userData,
                "token", token
        );

        return ResponseEntity.ok(response);
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginReq) {

        User user = userRepo.findByEmail(loginReq.getEmail())
                .orElseThrow(() -> new RuntimeException("Wrong email or password"));

        if (!passwordEncoder.matches(loginReq.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong email or password");
        }

        String token = jwtUtil.generateToken(user);

        Organisation org = user.getOrganisation();

        Map<String, Object> userData = Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "organisation", Map.of(
                        "id", org.getId(),
                        "name", org.getName(),
                        "status", org.getStatus()
                )
        );

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Login successful",
                "user", userData,
                "token", token
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(
                    Map.of("success", false, "message", "User not authenticated")
            );
        }

        User user = userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Organisation org = user.getOrganisation();

        Map<String, Object> userData = Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "organisation", Map.of(
                        "id", org.getId(),
                        "name", org.getName(),
                        "status", org.getStatus()
                )
        );

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "User fetched successfully",
                "user", userData
        );

        return ResponseEntity.ok(response);
    }

}
