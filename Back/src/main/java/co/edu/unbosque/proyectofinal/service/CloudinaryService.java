package co.edu.unbosque.proyectofinal.service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import co.edu.unbosque.proyectofinal.configuration.UploadPathConfig;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            Cloudinary cloudinary) {

        this.cloudinary = cloudinary;
    }

    public Map<String, Object> subirAudio(
            MultipartFile archivo)
            throws IOException {

        if (!cloudinaryConfigurado()) {

            String url = guardarArchivoLocal(archivo);

            return Map.of(
                    "secure_url", url,
                    "url", url,
                    "public_id", url,
                    "resource_type", "video");
        }

        return cloudinary.uploader().upload(

                archivo.getBytes(),

                ObjectUtils.asMap(

                        "resource_type",
                        "video",

                        "folder",
                        "audios_chat"));
    }
    
    public String subirArchivo(MultipartFile archivo) {

        try {

            if (!cloudinaryConfigurado()) {
                return guardarArchivoLocal(archivo);
            }

            Map<?, ?> resultado = cloudinary.uploader().upload(
                    archivo.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type",
                            "auto",
                            "folder",
                            "proyectofinal"));

            return resultado.get("secure_url").toString();

        } catch (Exception e) {

            try {
                return guardarArchivoLocal(archivo);
            } catch (RuntimeException localException) {
                throw new RuntimeException(
                        "No se pudo guardar el archivo en el servidor",
                        localException);
            }

        }
    }

    private boolean cloudinaryConfigurado() {

        return cloudinary.config.cloudName != null
                && !cloudinary.config.cloudName.isBlank()
                && cloudinary.config.apiKey != null
                && !cloudinary.config.apiKey.isBlank()
                && cloudinary.config.apiSecret != null
                && !cloudinary.config.apiSecret.isBlank();
    }

    private String guardarArchivoLocal(
            MultipartFile archivo) {

        try {
            Path carpetaUploads =
                    UploadPathConfig
                            .obtenerCarpetaUploads();

            Files.createDirectories(carpetaUploads);

            String extension =
                    obtenerExtension(
                            archivo.getOriginalFilename());

            String nombreArchivo =
                    UUID.randomUUID() + extension;

            Path destino =
                    carpetaUploads
                            .resolve(nombreArchivo)
                            .normalize();

            Files.copy(
                    archivo.getInputStream(),
                    destino,
                    StandardCopyOption.REPLACE_EXISTING);

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/uploads/")
                    .path(nombreArchivo)
                    .toUriString();

        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo guardar el archivo en "
                            + UploadPathConfig
                                    .obtenerCarpetaUploads()
                            + ": "
                            + e.getMessage());
        }
    }

    private String obtenerExtension(
            String nombreOriginal) {

        if (nombreOriginal == null
                || !nombreOriginal.contains(".")) {

            return "";
        }

        String extension =
                nombreOriginal.substring(
                        nombreOriginal.lastIndexOf("."));

        return extension.replaceAll(
                "[^a-zA-Z0-9.]",
                "");
    }
    
}
