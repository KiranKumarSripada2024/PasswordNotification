package com.alerts.PasswordNotification.service;

import com.alerts.PasswordNotification.model.NotificationType;
import com.alerts.PasswordNotification.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTests {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private User collibraUser;
    private User oracleUser;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(emailService, "senderEmail", "test@example.com");
        ReflectionTestUtils.setField(emailService, "firstNotificationDays", 30);
        ReflectionTestUtils.setField(emailService, "secondNotificationDays", 15);

        collibraUser = new User(
                "testCollibraUser",
                "Collibra CDIP",
                "TEST",
                90,
                LocalDate.now().minusDays(60),
                "collibra-support@example.com"
        );

        oracleUser = new User(
                "testOracleUser",
                "Oracle DB",
                "TEST",
                180,
                LocalDate.now().minusDays(60),
                "oracle-db-support@example.com"
        );

        when(emailSender.createMimeMessage()).thenReturn(new MimeMessage((Session)null));
    }

    @Test
    public void testSendPasswordResetNotificationForCollibra() throws MessagingException {
        Session session = Session.getInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(session);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendPasswordResetNotification(collibraUser, NotificationType.FIRST_NOTIFICATION);

        verify(emailSender, times(1)).send(any(MimeMessage.class));

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(emailSender).send(messageCaptor.capture());
        MimeMessage sentMessage = messageCaptor.getValue();

        assertNotNull(sentMessage);
    }

    @Test
    public void testSendPasswordResetNotificationForOracle() throws MessagingException {
        Session session = Session.getInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(session);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendPasswordResetNotification(oracleUser, NotificationType.SECOND_NOTIFICATION);

        verify(emailSender, times(1)).send(any(MimeMessage.class));

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(emailSender).send(messageCaptor.capture());
        MimeMessage sentMessage = messageCaptor.getValue();

        assertNotNull(sentMessage);
    }

    @Test
    public void testHandleMessagingException() throws MessagingException {
        doThrow(new MessagingException("Test exception")).when(emailSender).send(any(MimeMessage.class));

        emailService.sendPasswordResetNotification(collibraUser, NotificationType.FIRST_NOTIFICATION);

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }
}