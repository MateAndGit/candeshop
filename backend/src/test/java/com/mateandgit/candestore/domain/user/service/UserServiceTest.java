package com.mateandgit.candestore.domain.user.service;

import com.mateandgit.candestore.domain.user.dto.JoinRequest;
import com.mateandgit.candestore.domain.user.entity.UserEntity;
import com.mateandgit.candestore.domain.user.entity.UserRole;
import com.mateandgit.candestore.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void join_success() {
        // given
        JoinRequest request = new JoinRequest();
        request.setEmail("test@example.com");
        request.setUsername("testuser");
        request.setPassword("password123");

        // when
        userService.join(request);

        // then
        UserEntity savedUser = userRepository.findByEmail("test@example.com")
                .orElseThrow();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
        assertThat(passwordEncoder.matches("password123", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("중복 이메일로 회원가입 시 예외 발생")
    void join_duplicateEmail_throwsException() {
        // given
        JoinRequest request1 = new JoinRequest();
        request1.setEmail("test@example.com");
        request1.setUsername("user1");
        request1.setPassword("password123");

        JoinRequest request2 = new JoinRequest();
        request2.setEmail("test@example.com");
        request2.setUsername("user2");
        request2.setPassword("password456");

        userService.join(request1);

        // when & then
        assertThatThrownBy(() -> userService.join(request2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("사용자 로드 성공")
    void loadUserByUsername_success() {
        // given
        UserEntity user = UserEntity.builder()
                .email("test@example.com")
                .username("testuser")
                .password(passwordEncoder.encode("password123"))
                .role(UserRole.USER)
                .build();
        userRepository.save(user);

        // when
        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        // then
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getAuthorities()).hasSize(1);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로드 시 예외 발생")
    void loadUserByUsername_notFound_throwsException() {
        // when & then
        assertThatThrownBy(() -> userService.loadUserByUsername("notfound@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
