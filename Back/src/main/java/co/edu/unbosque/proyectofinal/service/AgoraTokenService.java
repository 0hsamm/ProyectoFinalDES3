package co.edu.unbosque.proyectofinal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.agora.media.*;

@Service
public class AgoraTokenService {

    private static final int DURACION_TOKEN_SEGUNDOS = 3600;

    @Value("${agora.app-id}")
    private String appId;

    @Value("${agora.app-certificate}")
    private String appCertificate;

    public String generarToken(
            String canal,
            int uid) {

        validarAppId();

        if (appCertificate == null
                || appCertificate.isBlank()) {
            return null;
        }

        try {
            RtcTokenBuilder2 tokenBuilder =
                    new RtcTokenBuilder2();

            return tokenBuilder.buildTokenWithUid(
                    appId.trim(),
                    appCertificate.trim(),
                    canal,
                    uid,
                    RtcTokenBuilder2.Role.ROLE_PUBLISHER,
                    DURACION_TOKEN_SEGUNDOS,
                    DURACION_TOKEN_SEGUNDOS);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error generando token de Agora",
                    e);
        }
    }

    public String getAppId() {

        return appId == null
                ? ""
                : appId.trim();
    }

    private void validarAppId() {

        if (appId == null || appId.isBlank()) {
            throw new IllegalStateException(
                    "AGORA_APP_ID no esta configurado en el backend");
        }
    }
}
