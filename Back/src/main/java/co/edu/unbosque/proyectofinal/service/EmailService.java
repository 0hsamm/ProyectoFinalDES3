package co.edu.unbosque.proyectofinal.service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final String MAIL_HOST_RESPALDO =
            "smtp.gmail.com";

    private static final int MAIL_PORT_RESPALDO = 587;

    private static final String MAIL_USERNAME_RESPALDO =
            "reload.proyecto.des3@gmail.com";

    private static final String MAIL_PASSWORD_RESPALDO =
            "wdcl ckjn wbkl sbag";

    private static final String MAIL_FROM_RESPALDO =
            "reload.proyecto.des3@gmail.com";

    private final JavaMailSender mailSender;

    private final String mailHost;

    private final int mailPort;

    private final String mailUsername;

    private final String mailPassword;

    private final String frontendUrl;

    private final String mailFrom;

    private final Set<String> origenesFrontPermitidos;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.host:}")
            String mailHost,
            @Value("${spring.mail.port:0}")
            int mailPort,
            @Value("${spring.mail.username:}")
            String mailUsername,
            @Value("${spring.mail.password:}")
            String mailPassword,
            @Value("${app.frontend.url}")
            String frontendUrl,
            @Value("${app.mail.from:}")
            String mailFrom,
            @Value("${app.frontend.allowed-origins}")
            String allowedOriginsRaw) {

        this.mailSender = mailSender;
        this.mailHost =
                resolverTexto(
                        mailHost,
                        MAIL_HOST_RESPALDO);
        this.mailPort =
                mailPort > 0
                        ? mailPort
                        : MAIL_PORT_RESPALDO;
        this.mailUsername =
                resolverTexto(
                        mailUsername,
                        MAIL_USERNAME_RESPALDO);
        this.mailPassword =
                resolverTexto(
                        mailPassword,
                        MAIL_PASSWORD_RESPALDO);
        this.frontendUrl = frontendUrl;
        this.mailFrom =
                resolverTexto(
                        mailFrom,
                        MAIL_FROM_RESPALDO);
        this.origenesFrontPermitidos =
                Arrays.stream(
                                allowedOriginsRaw.split(","))
                        .map(String::trim)
                        .filter(origin -> !origin.isBlank())
                        .collect(Collectors.toSet());

        configurarMailSender();
    }

    public void enviarCorreoVerificacion(
            String destino,
            String token) {

        enviarCorreoVerificacion(
                destino,
                token,
                null);
    }

    public void enviarCorreoVerificacion(
            String destino,
            String token,
            String frontendUrlOverride) {

        validarConfiguracionCorreo();

        String enlace =
                resolverFrontendUrl(
                        frontendUrlOverride)
                        + "/verificar?token="
                        + token;

        SimpleMailMessage mensaje =
                new SimpleMailMessage();

        mensaje.setFrom(mailFrom);
        mensaje.setTo(destino);
        mensaje.setSubject("Verifica tu cuenta");
        mensaje.setText(
                "Haz clic en este enlace para verificar tu cuenta:\n\n"
                        + enlace
                        + "\n\n"
                        + "Si prefieres hacerlo manualmente, usa este token:\n\n"
                        + token);

        mailSender.send(mensaje);
    }

    private void validarConfiguracionCorreo() {

        validarTexto(
                mailHost,
                "spring.mail.host");

        if (mailPort <= 0) {
            throw new IllegalStateException(
                    "No hay un puerto SMTP valido configurado. "
                            + "Define spring.mail.port o SPRING_MAIL_PORT.");
        }

        validarTexto(
                mailUsername,
                "spring.mail.username");

        validarTexto(
                mailPassword,
                "spring.mail.password");

        if (mailFrom == null
                || mailFrom.isBlank()) {
            throw new IllegalStateException(
                    "No hay remitente configurado para el correo. "
                            + "Define app.mail.from o SPRING_MAIL_USERNAME.");
        }

        if (frontendUrl == null
                || frontendUrl.isBlank()) {
            throw new IllegalStateException(
                    "No hay URL de frontend configurada para la verificacion. "
                            + "Define APP_FRONTEND_URL.");
        }
    }

    private void validarTexto(
            String valor,
            String propiedad) {

        if (valor == null
                || valor.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar la propiedad "
                            + propiedad
                            + " para el envio de correos.");
        }
    }

    private void configurarMailSender() {

        if (!(mailSender instanceof JavaMailSenderImpl javaMailSender)) {
            return;
        }

        javaMailSender.setHost(mailHost);
        javaMailSender.setPort(mailPort);
        javaMailSender.setUsername(mailUsername);
        javaMailSender.setPassword(mailPassword);
        javaMailSender.getJavaMailProperties().put(
                "mail.smtp.auth",
                "true");
        javaMailSender.getJavaMailProperties().put(
                "mail.smtp.starttls.enable",
                "true");
        javaMailSender.getJavaMailProperties().put(
                "mail.smtp.starttls.required",
                "true");
    }

    private String resolverTexto(
            String valor,
            String respaldo) {

        if (valor != null
                && !valor.isBlank()) {
            return valor.trim();
        }

        return respaldo;
    }

    private String normalizarFrontendUrl() {

        if (frontendUrl.endsWith("/")) {
            return frontendUrl.substring(
                    0,
                    frontendUrl.length() - 1);
        }

        return frontendUrl;
    }

    private String resolverFrontendUrl(
            String frontendUrlOverride) {

        if (frontendUrlOverride != null
                && !frontendUrlOverride.isBlank()) {

            String origenNormalizado =
                    normalizarUrl(frontendUrlOverride);

            if (origenesFrontPermitidos.contains(
                    origenNormalizado)) {
                return origenNormalizado;
            }
        }

        return normalizarFrontendUrl();
    }

    private String normalizarUrl(
            String url) {

        if (url.endsWith("/")) {
            return url.substring(
                    0,
                    url.length() - 1);
        }

        return url;
    }
}
