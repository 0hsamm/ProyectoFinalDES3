package co.edu.unbosque.proyectofinal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.proyectofinal.dto.MensajeDTO;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;
import co.edu.unbosque.proyectofinal.security.JwtUtil;
import co.edu.unbosque.proyectofinal.service.AuditoriaService;
import co.edu.unbosque.proyectofinal.service.MensajeService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/mensajes")
public class MensajeController {

	@Autowired
	private MensajeService mensajeService;
	
	@Autowired
	private AuditoriaService auditoriaService;

	@Autowired
	private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

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

	@PostMapping
	public ResponseEntity<?> enviarMensaje(
	        @RequestBody MensajeDTO dto,
	        HttpServletRequest request) {

	    String ip = request.getRemoteAddr();
	    String navegador = request.getHeader("User-Agent");
        Usuario usuarioAutenticado =
                obtenerUsuarioAutenticado(request);

        if (usuarioAutenticado == null) {
            return new ResponseEntity<>(
                    "No autorizado",
                    HttpStatus.UNAUTHORIZED);
        }

        dto.setRemitenteId(
                usuarioAutenticado.getId());

	    MensajeService.ResultadoCreacionMensaje resultado =
                mensajeService.create(dto);

        int status =
                resultado.getCodigo();

	    if (status == 0) {
	        auditoriaService.registrar(
	                usuarioAutenticado,
	                "ENVIAR_MENSAJE",
	                "MENSAJES",
	                "Mensaje enviado en conversacion: " + dto.getConversacionId(),
	                ip, navegador, null, null, null, true);
	        return new ResponseEntity<>(
                    resultado.getMensaje(),
                    HttpStatus.CREATED);
	    } else if (status == 1) {
	        return new ResponseEntity<>("Usuario no encontrado", HttpStatus.BAD_REQUEST);
	    } else if (status == 4) {
	        return new ResponseEntity<>("El usuario no tiene permiso", HttpStatus.FORBIDDEN);
	    } else if (status == 5) {
	        return new ResponseEntity<>("Datos del mensaje incompletos", HttpStatus.BAD_REQUEST);
	    } else {
	        return new ResponseEntity<>("Error interno", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	@GetMapping
	public ResponseEntity<?> obtenerTodos(
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

        if (!esAdmin(usuarioAutenticado)) {
            return new ResponseEntity<>(
                    "No tienes permiso para consultar todos los mensajes",
                    HttpStatus.FORBIDDEN);
        }

		return ResponseEntity.ok(
                mensajeService.getAll(fraseSecreta));
	}

	@GetMapping("/conversacion/{id}")
	public ResponseEntity<?> obtenerPorConversacion(
			@PathVariable Long id,
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

		return ResponseEntity.ok(
                mensajeService.getMensajesPorConversacion(
                        id,
                        usuarioAutenticado.getId(),
                        fraseSecreta));
	}

	@GetMapping("/conversacion/{id}/recientes")
	public ResponseEntity<?> obtenerRecientes(
			@PathVariable Long id,
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

		return ResponseEntity.ok(
                mensajeService.getUltimosMensajes(
                        id,
                        usuarioAutenticado.getId(),
                        fraseSecreta));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(
			@PathVariable Long id,
            HttpServletRequest request) {

        Usuario usuarioAutenticado =
                obtenerUsuarioAutenticado(request);

        if (usuarioAutenticado == null) {
            return new ResponseEntity<>(
                    "No autorizado",
                    HttpStatus.UNAUTHORIZED);
        }

		return ResponseEntity.ok(
                mensajeService.deleteById(
                        id,
                        usuarioAutenticado.getId(),
                        esAdmin(usuarioAutenticado)));
	}
}
