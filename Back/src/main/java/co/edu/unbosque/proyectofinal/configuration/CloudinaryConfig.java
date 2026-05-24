package co.edu.unbosque.proyectofinal.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

    private static final String CLOUDINARY_CLOUD_NAME_RESPALDO =
            "doxpihrec";

    private static final String CLOUDINARY_API_KEY_RESPALDO =
            "543442182314184";

    private static final String CLOUDINARY_API_SECRET_RESPALDO =
            "H2mMWCJ3SmiDC9VVxen4-Knc2d4";

    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    @Bean
    Cloudinary cloudinary() {

        Map<String, String> valuesMap = new HashMap<>();

        valuesMap.put(
                "cloud_name",
                obtenerValorConfigurado(
                        cloudName,
                        CLOUDINARY_CLOUD_NAME_RESPALDO));
        valuesMap.put(
                "api_key",
                obtenerValorConfigurado(
                        apiKey,
                        CLOUDINARY_API_KEY_RESPALDO));
        valuesMap.put(
                "api_secret",
                obtenerValorConfigurado(
                        apiSecret,
                        CLOUDINARY_API_SECRET_RESPALDO));

        return new Cloudinary(valuesMap);
    }

    private String obtenerValorConfigurado(
            String valor,
            String respaldo) {

        if (valor != null
                && !valor.isBlank()) {
            return valor.trim();
        }

        return respaldo;
    }
}
