package ru.kopytov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kopytov.dto.JwtResponse;
import ru.kopytov.dto.LoginRequest;
import ru.kopytov.dto.SingupRequest;
import ru.kopytov.model.Role;
import ru.kopytov.model.RoleEnum;
import ru.kopytov.model.User;
import ru.kopytov.repository.RoleRepository;
import ru.kopytov.repository.UserRepository;
import ru.kopytov.util.JwtUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> registerUser(SingupRequest singupRequest) {
        if (userRepository.existsByLogin(singupRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");

        }
        if (userRepository.existsByEmail(singupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setLogin(singupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(singupRequest.getPassword()));
        user.setEmail(singupRequest.getEmail());

        Set<String> strRoles = singupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role role = roleRepository.findByName(RoleEnum.USER).orElseThrow(() -> new RuntimeException("Role note found"));
            roles.add(role);
        } else {
            for (String role : strRoles) {
                if (role.equals("admin")) {
                    Role adminRole = roleRepository.findByName(RoleEnum.ADMIN).orElseThrow(() -> new RuntimeException("Role note found"));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(RoleEnum.USER).orElseThrow(() -> new RuntimeException("Role note found"));
                    roles.add(userRole);
                }
            }

        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok().body("User registered successfully");
    }

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwt(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }
}
