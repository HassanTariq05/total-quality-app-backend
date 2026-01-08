package com.i2p.accreditations.controller.accessModule;

import com.i2p.accreditations.Request.LoginRequest;
import com.i2p.accreditations.dto.RegisterUserRequestDto;
import com.i2p.accreditations.dto.UpdateUserRequestDto;
import com.i2p.accreditations.model.access.User;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.model.role.Role;
import com.i2p.accreditations.repository.access.UserRepository;
import com.i2p.accreditations.repository.organisation.OrganisationRepository;
import com.i2p.accreditations.repository.role.RoleRepository;
import com.i2p.accreditations.service.access.MyUserDetailsService;
import com.i2p.accreditations.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired private RoleRepository roleRepository;

    @PreAuthorize("hasRole('Super Admin')")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {

        List<User> users = userRepo.findAll();

        List<Map<String, Object>> response = users.stream()
                .map(this::mapUserResponse)
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Users fetched successfully",
                        "users", response
                )
        );
    }

    @PreAuthorize("hasAnyRole('Super Admin', 'Administrator')")
    @GetMapping("/users/org/{orgId}")
    public ResponseEntity<?> getUsersByOrgId(@PathVariable UUID orgId) {

        if (!organisationRepository.existsById(orgId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "Organisation not found"
                    ));
        }

        List<User> users = userRepo.findByOrganisationId(orgId);

        List<Map<String, Object>> response = users.stream()
                .map(this::mapUserResponse)
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Users fetched successfully",
                        "users", response
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequestDto req) {

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("success", false, "message", "Email already in use")
            );
        }

        Organisation organisation = organisationRepository.findById(req.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        Role role = roleRepository.findById(req.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setStatus(req.getStatus());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setOrganisation(organisation);
        user.setRole(role);

        User savedUser = userRepo.save(user);

        String token = jwtUtil.generateToken(savedUser);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "User registered successfully",
                        "user", mapUserResponse(savedUser),
                        "token", token
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginReq) {

        User user = userRepo.findByEmail(loginReq.getEmail())
                .orElseThrow(() -> new RuntimeException("Wrong email or password"));

        if (!passwordEncoder.matches(loginReq.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong email or password");
        }

        String token = jwtUtil.generateToken(user);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Login successful",
                        "user", mapUserResponse(user),
                        "token", token
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("success", false, "message", "User not authenticated")
            );
        }

        User user = userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "User fetched successfully",
                        "user", mapUserResponse(user)
                )
        );
    }

    @PreAuthorize("hasAnyRole('Super Admin', 'Administrator')")
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequestDto req
    ) {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getName() != null && !req.getName().isBlank()) {
            user.setName(req.getName());
        }

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            user.setEmail(req.getEmail());
        }

        if (req.getPhoneNumber() != null) {
            user.setPhoneNumber(req.getPhoneNumber().trim());
        }

        if (req.getStatus() != null) {
            user.setStatus(req.getStatus());
        }

        if (req.getOrganizationId() != null) {
            Organisation org = organisationRepository.findById(req.getOrganizationId())
                    .orElseThrow(() -> new RuntimeException("Organisation not found"));
            user.setOrganisation(org);
        }

        if (req.getRoleId() != null) {
            Role role = roleRepository.findById(req.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        }

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        User updatedUser = userRepo.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "User updated successfully",
                        "user", mapUserResponse(updatedUser)
                )
        );
    }

    private Map<String, Object> mapUserResponse(User user) {

        Organisation org = user.getOrganisation();
        Role role = user.getRole();

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("status", user.getStatus());

        response.put("organisation", org == null ? null : Map.of(
                "id", org.getId(),
                "name", org.getName(),
                "status", org.getStatus()
        ));

        if (role != null) {
            response.put("role", Map.of(
                    "id", role.getId(),
                    "name", role.getName(),
                    "permissions", role.getPermissions()
                            .stream()
                            .map(Enum::name)
                            .toList()
            ));

            response.put("permissions", role.getPermissions()
                    .stream()
                    .map(Enum::name)
                    .toList());
        } else {
            response.put("role", null);
            response.put("permissions", List.of());
        }

        return response;
    }


}
