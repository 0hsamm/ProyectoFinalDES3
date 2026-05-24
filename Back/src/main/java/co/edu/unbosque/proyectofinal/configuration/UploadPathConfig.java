package co.edu.unbosque.proyectofinal.configuration;

import java.nio.file.Path;

public final class UploadPathConfig {

    private UploadPathConfig() {
    }

    public static Path obtenerCarpetaUploads() {

        return Path
                .of(
                        System.getProperty("user.home"),
                        "proyectofinal-uploads")
                .toAbsolutePath()
                .normalize();
    }
}
