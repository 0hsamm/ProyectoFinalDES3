package co.edu.unbosque.proyectofinal.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    @Bean
    Cloudinary cloudinary() {

        Map<String, String> valuesMap = new HashMap<>();

        valuesMap.put("cloud_name", cloudName);
        valuesMap.put("api_key", apiKey);
        valuesMap.put("api_secret", apiSecret);

        return new Cloudinary(valuesMap);
    }
}
