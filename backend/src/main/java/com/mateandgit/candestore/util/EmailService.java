package com.mateandgit.candestore.util;

import com.mateandgit.candestore.domain.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    // 테스트용 내 이메일 변수로 고정
    private final String MY_TEST_EMAIL = "candealvarez444@gmail.com";

    @Async
    public void sendOrderConfirmation(String userEmail, Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("sola201@naver.com"); // 내 메일로 고정
        message.setSubject("[Cande Store] 주문 입금 안내");
        message.setText("총 금액: " + order.getTotalPrice() + "원\n계좌: [아르헨티나 계좌]");
        mailSender.send(message);
    }

    @Async
    public void sendAdminNotification(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(MY_TEST_EMAIL); // 여기도 내 메일로 고정!
        message.setSubject("[관리자 알림] 주문 접수: " + order.getId());
        message.setText("주문자: " + order.getUser().getEmail());
        mailSender.send(message);
    }
}