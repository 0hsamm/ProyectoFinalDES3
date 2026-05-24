package co.edu.unbosque.proyectofinal.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private static final String ALLOWED_ORIGINS =
            "http://localhost:4200,http://localhost:4201";

    @Mock
    private JavaMailSender mailSender;

    @Test
    void enviarCorreoVerificacionUsaEnlaceYRemitenteConfigurado() {
        EmailService emailService =
                new EmailService(
                        mailSender,
                        "smtp.gmail.com",
                        587,
                        "notificaciones@wzchat.com",
                        "app-password",
                        "http://localhost:4200",
                        "notificaciones@wzchat.com",
                        ALLOWED_ORIGINS);

        emailService.enviarCorreoVerificacion(
                "destino@correo.com",
                "token-123");

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(
                        SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage mensaje =
                captor.getValue();

        Assertions.assertEquals(
                "notificaciones@wzchat.com",
                mensaje.getFrom());
        Assertions.assertEquals(
                "Verifica tu cuenta",
                mensaje.getSubject());
        Assertions.assertEquals(
                "destino@correo.com",
                mensaje.getTo()[0]);
        Assertions.assertTrue(
                mensaje.getText().contains(
                        "http://localhost:4200/verificar?token=token-123"));
        Assertions.assertTrue(
                mensaje.getText().contains("token-123"));
    }

    @Test
    void enviarCorreoVerificacionFallaSiNoHayRemitenteConfigurado() {
        EmailService emailService =
                new EmailService(
                        mailSender,
                        "smtp.gmail.com",
                        587,
                        "",
                        "app-password",
                        "http://localhost:4200",
                        "",
                        ALLOWED_ORIGINS);

        assertThrows(
                IllegalStateException.class,
                () -> emailService.enviarCorreoVerificacion(
                        "destino@correo.com",
                        "token-123"));
    }

    @Test
    void enviarCorreoVerificacionAceptaOrigenDelFrontCelular() {
        EmailService emailService =
                new EmailService(
                        mailSender,
                        "smtp.gmail.com",
                        587,
                        "notificaciones@wzchat.com",
                        "app-password",
                        "http://localhost:4200",
                        "notificaciones@wzchat.com",
                        ALLOWED_ORIGINS);

        emailService.enviarCorreoVerificacion(
                "destino@correo.com",
                "token-321",
                "http://localhost:4201");

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(
                        SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        Assertions.assertTrue(
                captor.getValue().getText().contains(
                        "http://localhost:4201/verificar?token=token-321"));
    }
}
