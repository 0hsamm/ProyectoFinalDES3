package co.edu.unbosque.proyectofinal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.agora.media.*;

@Service
public class AgoraTokenService {

    private static final String AGORA_APP_ID_RESPALDO =
            "640281ec7e064092b7824ac9372401be";

    private static final String AGORA_APP_CERTIFICATE_RESPALDO =
            "b7303e8509e246d5892c64750453d848";

    private static final int DURACION_TOKEN_SEGUNDOS = 3600;

    @Value("${agora.app-id}")
    private String appId;

    @Value("${agora.app-certificate}")
    private String appCertificate;

    public String generarToken(
            String canal,
            int uid) {

        String appIdConfigurada =
                getAppId();

        validarAppId(appIdConfigurada);

        String appCertificateConfigurado =
                getAppCertificate();

        if (appCertificateConfigurado.isBlank()) {
            return null;
        }

        try {
            RtcTokenBuilder2 tokenBuilder =
                    new RtcTokenBuilder2();

            return tokenBuilder.buildTokenWithUid(
                    appIdConfigurada,
                    appCertificateConfigurado,
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

        if (appId != null
                && !appId.isBlank()) {
            return appId.trim();
        }

        return AGORA_APP_ID_RESPALDO;
    }

    private String getAppCertificate() {

        if (appCertificate != null
                && !appCertificate.isBlank()) {
            return appCertificate.trim();
        }

        return AGORA_APP_CERTIFICATE_RESPALDO;
    }

    private void validarAppId(
            String appIdConfigurada) {

        if (appIdConfigurada == null
                || appIdConfigurada.isBlank()) {
            throw new IllegalStateException(
                    "AGORA_APP_ID no esta configurado en el backend");
        }
    }
}
