package co.edu.unbosque.proyectofinal.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.exception.MensajeNoEncontradoException;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;
import co.edu.unbosque.proyectofinal.security.JwtUtil;
import co.edu.unbosque.proyectofinal.service.MensajeService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/mensajes")
public class ArchivoAdjuntoController {

    private final MensajeService mensajeService;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public ArchivoAdjuntoController(
            MensajeService mensajeService,
            UsuarioRepository usuarioRepository,
            JwtUtil jwtUtil) {

        this.mensajeService = mensajeService;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    private Usuario obtenerUsuarioAutenticado(
            HttpServletRequest request) {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String correo =
                jwtUtil.extractUsername(
                        authHeader.substring(7));

        return usuarioRepository
                .findByCorreo(correo)
                .orElse(null);
    }

    private boolean esAdmin(
            Usuario usuario) {

        return usuario != null
                && usuario.getRol() != null
                && "ROLE_ADMIN".equals(
                        usuario.getRol().name());
    }

    @PostMapping("/{mensajeId}/adjunto")
    public ResponseEntity<?> subirAdjunto(
            @PathVariable Long mensajeId,
            @RequestParam MultipartFile archivo,
            @RequestParam(required = false)
            String fraseSecreta,
            HttpServletRequest request) {

        Usuario usuarioAutenticado =
                obtenerUsuarioAutenticado(request);

        if (usuarioAutenticado == null) {
            return new ResponseEntity<>(
                    "No autorizado",
                    HttpStatus.UNAUTHORIZED);
        }

        try {
            return ResponseEntity.ok(
                    mensajeService.subirAdjunto(
                            mensajeId,
                            archivo,
                            usuarioAutenticado.getId(),
                            esAdmin(usuarioAutenticado),
                            fraseSecreta));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{mensajeId}/adjunto")
    public ResponseEntity<?> obtenerAdjunto(
            @PathVariable Long mensajeId) {

        try {
            MensajeService.ArchivoAdjuntoDescargable adjunto =
                    mensajeService.obtenerAdjunto(
                            mensajeId);

            return ResponseEntity.ok()
                    .contentType(
                            resolverMediaType(
                                    adjunto.getContentType()))
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.inline()
                                    .filename(
                                            adjunto.getNombreArchivo(),
                                            StandardCharsets.UTF_8)
                                    .build()
                                    .toString())
                    .body(new ByteArrayResource(
                            adjunto.getContenido()));

        } catch (MensajeNoEncontradoException e) {
            return ResponseEntity.status(
                    HttpStatus.NOT_FOUND)
                    .body("No se encontró el mensaje");

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                    HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    private MediaType resolverMediaType(
            String valor) {

        try {
            return MediaType.parseMediaType(
                    valor);
        } catch (IllegalArgumentException e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
