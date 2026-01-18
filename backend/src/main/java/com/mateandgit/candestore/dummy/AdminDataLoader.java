package com.mateandgit.candestore.dummy;

import com.mateandgit.candestore.domain.user.entity.UserEntity;
import com.mateandgit.candestore.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.mateandgit.candestore.domain.user.entity.UserRole.ADMIN;

@Component
@RequiredArgsConstructor
public class AdminDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {

            UserEntity admin = UserEntity.builder()
                    .email("admin@gmail.com")
                    .username("admin")
                    .password(passwordEncoder.encode("1234"))
                    .role(ADMIN)
                    .build();
            userRepository.save(admin);

            System.out.println("========================================");
            System.out.println("성공: 관리자 계정(admin)이 자동 생성되었습니다!");
            System.out.println("========================================");
        }
    }
}