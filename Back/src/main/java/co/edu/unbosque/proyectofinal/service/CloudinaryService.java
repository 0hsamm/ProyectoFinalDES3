package co.edu.unbosque.proyectofinal.service;
import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

            Map<?, ?> resultado = cloudinary.uploader().upload(
                    archivo.getBytes(),
                    ObjectUtils.emptyMap());

            return resultado.get("secure_url").toString();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error subiendo archivo a Cloudinary");
        }
    }
    
}