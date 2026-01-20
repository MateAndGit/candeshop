package com.mateandgit.candestore.domain.user.service;

import com.mateandgit.candestore.domain.user.dto.CustomUserDetails;
import com.mateandgit.candestore.domain.user.dto.JoinRequest;
import com.mateandgit.candestore.domain.user.entity.UserEntity;
import com.mateandgit.candestore.domain.user.entity.UserRole;
import com.mateandgit.candestore.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mateandgit.candestore.domain.user.entity.UserRole.USER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(JoinRequest joinRequest) {

        if (userRepository.existsByEmail(joinRequest.getEmail())) {
            throw new RuntimeException("El email ya existe");
        }

        UserEntity user = UserEntity.builder()
                .email(joinRequest.getEmail())
                .username(joinRequest.getUsername())
                .password(passwordEncoder.encode(joinRequest.getPassword()))
                .role(USER)
                .build();

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return new CustomUserDetails(userEntity);
    }
}
