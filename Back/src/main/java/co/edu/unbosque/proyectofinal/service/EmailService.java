package co.edu.unbosque.proyectofinal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;

  

    public void enviarCorreoVerificacion(

            String destino,

            String token) {

        String enlace =

                frontendUrl + "/verificar?token="
                        + token;

        SimpleMailMessage mensaje =
                new SimpleMailMessage();

        mensaje.setTo(destino);

        mensaje.setSubject(
                "Verifica tu cuenta");

        mensaje.setText(

                "Haz clic en el siguiente enlace:\n\n"
                        + enlace);

        mailSender.send(mensaje);
    }
}