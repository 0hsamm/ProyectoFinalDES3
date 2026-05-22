package co.edu.unbosque.proyectofinal.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.proyectofinal.dto.ArchivoAdjuntoDTO;
import co.edu.unbosque.proyectofinal.entity.ArchivoAdjunto;
import co.edu.unbosque.proyectofinal.entity.Mensaje;
import co.edu.unbosque.proyectofinal.repository.ArchivoAdjuntoRepository;
import co.edu.unbosque.proyectofinal.repository.MensajeRepository;
import co.edu.unbosque.proyectofinal.service.CloudinaryService;

@RestController
@RequestMapping("/mensajes")
public class ArchivoAdjuntoController {

    private final MensajeRepository mensajeRepository;
    private final ArchivoAdjuntoRepository archivoAdjuntoRepository;
    private final CloudinaryService cloudinaryService;

    public ArchivoAdjuntoController(
            MensajeRepository mensajeRepository,
            ArchivoAdjuntoRepository archivoAdjuntoRepository,
            CloudinaryService cloudinaryService) {

        this.mensajeRepository = mensajeRepository;
        this.archivoAdjuntoRepository = archivoAdjuntoRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/{mensajeId}/adjunto")
    public ResponseEntity<?> subirAdjunto(
            @PathVariable Long mensajeId,
            @RequestParam MultipartFile archivo) {

        Mensaje mensaje =
                mensajeRepository.findById(mensajeId)
                        .orElse(null);

        if (mensaje == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Mensaje no encontrado");
        }

        if (archivo == null || archivo.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Debes enviar un archivo");
        }

        String url =
                cloudinaryService.subirArchivo(archivo);

        ArchivoAdjunto adjunto =
                new ArchivoAdjunto();

        adjunto.setMensaje(mensaje);
        adjunto.setRutaAlmacenamiento(url);
        adjunto.setNombreOriginalArchivo(
                archivo.getOriginalFilename());
        adjunto.setFormatoArchivo(
                archivo.getContentType());
        adjunto.setTamanoArchivo(
                archivo.getSize());
        adjunto.setVi("NO_CIFRADO");
        adjunto.setUrl(url);
        adjunto.setPublicId(url);

        archivoAdjuntoRepository.save(adjunto);

        ArchivoAdjuntoDTO dto =
                new ArchivoAdjuntoDTO(
                        adjunto.getId(),
                        mensaje.getId(),
                        adjunto.getNombreOriginalArchivo(),
                        adjunto.getFormatoArchivo(),
                        adjunto.getTamanoArchivo(),
                        adjunto.getUrl(),
                        adjunto.getPublicId(),
                        null);

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Adjunto subido correctamente",
                        "adjunto",
                        dto));
    }
}
