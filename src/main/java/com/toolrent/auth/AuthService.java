package com.toolrent.auth;

import com.toolrent.config.JwtService;
import com.toolrent.tenant.Tenant;
import com.toolrent.tenant.TenantRepository;
import com.toolrent.user.User;
import com.toolrent.user.UserRepository;
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

        // verifica se email já está cadastrado
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email already registered");
        }

        // cria tenant
        Tenant tenant = Tenant.create(request.companyName());
        tenantRepository.save(tenant);

        // cria user admin
        String passwordHash = passwordEncoder.encode(request.password());
        User user = User.createAdmin(tenant.getId(), request.userName(), request.email(), passwordHash);
        userRepository.save(user);

        // gera token
        String token = jwtService.generate(user.getId(), tenant.getId());
        return new RegisterResponse(token, user.getName(), user.getEmail());
    }
}
