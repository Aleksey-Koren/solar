package io.solar.service.mail;

import io.solar.entity.PasswordToken;
import io.solar.entity.User;
import io.solar.repository.PasswordTokenRepository;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${app.email.forgot_password_template}")
    private String forgotPasswordTemplate;

    @Value("${app.email.token_lifetime_hours}")
    private Integer tokenLifetimeHours;

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final PasswordTokenRepository passwordTokenRepository;

    public void sendSimpleEmail(User user, String title, String message) {
        if (Objects.nonNull(user.getEmail())) {

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject(title);
            mailMessage.setText(message);
            emailSender.send(mailMessage);
        }
    }

    public void sendForgotPasswordEmail(User user) {
        Optional<PasswordToken> passwordTokenOptional = passwordTokenRepository.findById(user.getId());

        if (passwordTokenOptional.isPresent() && passwordTokenOptional.get().getExpireAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password can reset once every " + tokenLifetimeHours + " hours "
            );
        }

        String token = UUID.randomUUID().toString();

        passwordTokenRepository.save(PasswordToken.builder()
                .user(user)
                .expireAt(Instant.now().plus(tokenLifetimeHours, ChronoUnit.HOURS))
                .isActivated(false)
                .token(token)
                .build()
        );

        sendSimpleEmail(user, "Forgot password", forgotPasswordTemplate + token);
    }

    public void sendTemplateEmail(TemplateEmail templateEmail) {
        try {
            Context context = new Context();
            context.setVariables(templateEmail.getTemplateVariables());

            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            String html = templateEngine.process(templateEmail.getTemplateFilename(), context);

            helper.setText(html, true);
            helper.setTo(templateEmail.getSendAddress());
            helper.setSubject(templateEmail.getTitle());

            emailSender.send(mimeMessage);

        } catch (MessagingException e) {
            //todo: log.error("Error while sending mail to {}", email.getSendAddress());
            throw new ServiceException(String.format("Cannot send email to %s", templateEmail.getSendAddress()));
        }
    }
}
