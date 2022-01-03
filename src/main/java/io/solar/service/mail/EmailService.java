package io.solar.service.mail;

import io.solar.entity.User;
import io.solar.service.UserService;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final UserService userService;

    public void sendSimpleEmail(Long userId, String title, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiveEmailFromLogin(userId));
        mailMessage.setSubject(title);
        mailMessage.setText(message);
        emailSender.send(mailMessage);
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

    private String receiveEmailFromLogin(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ServiceException(String.format("Cannot find user with id = %d for send email", userId)));

        if (user.getLogin().contains("@")) {
            return user.getLogin();
        } else {
            throw new ServiceException(String.format("User with id = %d does not contain email", userId));
        }
    }

}
