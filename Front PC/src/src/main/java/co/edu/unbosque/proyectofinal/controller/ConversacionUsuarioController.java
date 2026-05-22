package co.edu.unbosque.proyectofinal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.proyectofinal.dto.ConversacionDTO;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;
import co.edu.unbosque.proyectofinal.security.JwtUtil;
import co.edu.unbosque.proyectofinal.service.ConversacionService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/conversaciones")
public class ConversacionUsuarioController {

    private final ConversacionService conversacionService;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public ConversacionUsuarioController(
            ConversacionService conversacionService,
            UsuarioRepository usuarioRepository,
            JwtUtil jwtUtil) {

        this.conversacionService = conversacionService;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/mis")
    public ResponseEntity<List<ConversacionDTO>> obtenerMisConversaciones(
            HttpServletRequest request) {

        String correo = extraerCorreo(request);

        if (correo == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Usuario usuario =
                usuarioRepository.findByCorreo(correo)
                        .orElse(null);

        if (usuario == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(
                conversacionService.getConUltimoMensaje(
                        usuario.getId()));
    }

    private String extraerCorreo(
            HttpServletRequest request) {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader != null
                && authHeader.startsWith("Bearer ")) {

            return jwtUtil.extractUsername(
                    authHeader.substring(7));
        }

        return null;
    }
}
