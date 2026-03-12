package poly.edu.service;

import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromAddress", "noreply@gomet.vn");
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:5173/");
    }

    @Test
    @DisplayName("sendOtpEmail sends HTML mail with encoded verification link")
    void sendOtpEmail_sendsEncodedVerificationLink() throws Exception {
        MimeMessage mimeMessage = newMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendOtpEmail("chef+test@gomet.vn", "Bao", "123456");

        MimeMessage sent = captureSentMessage();
        String body = htmlBody(sent);

        assertThat(sent.getSubject()).isEqualTo("GoMet — Your Verification Code");
        assertThat(body).contains("verify-email?");
        assertThat(body).contains("email=chef%2Btest%40gomet.vn");
        assertThat(body).contains("token=123456");
        assertThat(body).contains("Hi <strong style=\"color:#1C1917;\">Bao</strong>");
    }

    @Test
    @DisplayName("sendResetPasswordEmail sends reset link with encoded token")
    void sendResetPasswordEmail_sendsEncodedResetLink() throws Exception {
        MimeMessage mimeMessage = newMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendResetPasswordEmail("chef@gomet.vn", "", "token with spaces+");

        MimeMessage sent = captureSentMessage();
        String body = htmlBody(sent);

        assertThat(sent.getSubject()).isEqualTo("GoMet — Reset Your Password");
        assertThat(body).contains("reset-password?token=token+with+spaces%2B");
        assertThat(body).contains("Chef");
    }

    @Test
    @DisplayName("sendSuspiciousLoginEmail includes both verify and wasnt-me links")
    void sendSuspiciousLoginEmail_includesVerificationLinks() throws Exception {
        MimeMessage mimeMessage = newMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendSuspiciousLoginEmail(
                "chef@gomet.vn",
                "Bao",
                "verify-token",
                "wasnt-me-token",
                "10.0.0.1",
                "Chrome on Windows"
        );

        MimeMessage sent = captureSentMessage();
        String body = htmlBody(sent);

        assertThat(sent.getSubject()).isEqualTo("⚠️ GoMet — Verify New Login from Chrome on Windows");
        assertThat(body).contains("/auth/verify-login?token=verify-token");
        assertThat(body).contains("/auth/verify-login?");
        assertThat(body).contains("token=wasnt-me-token");
        assertThat(body).contains("action=wasnt-me");
        assertThat(body).contains("10.0.0.1");
    }

    @Test
    @DisplayName("sendProfileUpdateEmail swallows send failures because the profile update already succeeded")
    void sendProfileUpdateEmail_doesNotThrowWhenMailSendFails() {
        MimeMessage mimeMessage = newMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP down")).when(mailSender).send(any(MimeMessage.class));

        assertThatCode(() -> emailService.sendProfileUpdateEmail(
                "chef@gomet.vn",
                "Bao",
                List.of("Avatar", "Bio")
        )).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("sendOtpEmail fails fast when SMTP sender is missing")
    void sendOtpEmail_failsFastWhenSenderMissing() {
        ReflectionTestUtils.setField(emailService, "fromAddress", "   ");
        when(mailSender.createMimeMessage()).thenReturn(newMimeMessage());

        assertThatThrownBy(() -> emailService.sendOtpEmail("chef@gomet.vn", "Bao", "654321"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("SMTP sender address is not configured");
    }

    private MimeMessage captureSentMessage() {
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        return captor.getValue();
    }

    private MimeMessage newMimeMessage() {
        return new MimeMessage(Session.getInstance(new Properties()));
    }

    private String htmlBody(MimeMessage message) throws Exception {
        return extractText(message.getContent());
    }

    private String extractText(Object content) throws Exception {
        if (content instanceof String stringContent) {
            return stringContent;
        }
        if (content instanceof Multipart multipart) {
            for (int index = 0; index < multipart.getCount(); index++) {
                BodyPart bodyPart = multipart.getBodyPart(index);
                String text = extractText(bodyPart.getContent());
                if (text != null && !text.isBlank()) {
                    return text;
                }
            }
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(String.valueOf(content).getBytes(StandardCharsets.UTF_8));
        return output.toString(StandardCharsets.UTF_8);
    }
}