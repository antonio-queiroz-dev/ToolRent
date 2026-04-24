package com.toolrent.auth.service;

import com.toolrent.auth.web.RegisterRequest;
import com.toolrent.auth.web.RegisterResponse;
import com.toolrent.config.JwtService;
import com.toolrent.tenant.domain.Tenant;
import com.toolrent.tenant.persistence.TenantEntity;
import com.toolrent.tenant.persistence.TenantRepository;
import com.toolrent.user.domain.User;
import com.toolrent.user.persistence.UserEntity;
import com.toolrent.user.persistence.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(TenantRepository tenantRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email already registered");
        }

        Tenant tenant = Tenant.create(request.companyName());
        tenantRepository.save(TenantEntity.fromDomain(tenant));

        String passwordHash = passwordEncoder.encode(request.password());
        User user = User.createAdmin(tenant.id(), request.userName(), request.email(), passwordHash);
        userRepository.save(UserEntity.fromDomain(user));

        String token = jwtService.generate(user.id(), tenant.id());
        return new RegisterResponse(token, user.name(), user.email());
    }
}
