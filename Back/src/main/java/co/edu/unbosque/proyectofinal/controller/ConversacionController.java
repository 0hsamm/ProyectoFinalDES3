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
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.proyectofinal.dto.ConversacionDTO;
import co.edu.unbosque.proyectofinal.dto.ParticipanteConversacionDTO;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;
import co.edu.unbosque.proyectofinal.security.JwtUtil;
import co.edu.unbosque.proyectofinal.service.AuditoriaService;
import co.edu.unbosque.proyectofinal.service.ConversacionService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/conversaciones")
public class ConversacionController {

	@Autowired
	private ConversacionService conversacionService;
	
	@Autowired
	private AuditoriaService auditoriaService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private String extraerCorreo(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtUtil.extractUsername(authHeader.substring(7));
        }
        return null;
    }

    private Usuario obtenerUsuarioAutenticado(
            HttpServletRequest request) {

        String correo = extraerCorreo(request);

        if (correo == null) {
            return null;
        }

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
	public ResponseEntity<String> crear(
	        @RequestBody ConversacionDTO dto,
	        HttpServletRequest request) {

	    String ip = request.getRemoteAddr();
	    String navegador = request.getHeader("User-Agent");

	    int status = conversacionService.create(dto);

	    if (status == 0) {
	    	 auditoriaService.registrarConCorreo(
	                    extraerCorreo(request),
	                    "CREAR_CONVERSACION",
	                    "CONVERSACIONES",
	                    "Nueva conversacion creada",
	                    ip, navegador, null, null, null, true);
	    	 return new ResponseEntity<>(
	                "Conversacion creada correctamente",
	                HttpStatus.CREATED);
	    }
	    if (status == 5) {
	        return new ResponseEntity<>(
	                "La frase secreta es obligatoria para proteger la conversacion",
	                HttpStatus.BAD_REQUEST);
	    }
	    return new ResponseEntity<>(
	            "Error al crear la conversacion",
	            HttpStatus.BAD_REQUEST);
	}

	@GetMapping
	public ResponseEntity<?> getAll(
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
                    "No tienes permiso para consultar todas las conversaciones",
                    HttpStatus.FORBIDDEN);
        }

		return new ResponseEntity<>(
				conversacionService.getAll(),
				HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(
			@PathVariable Long id,
            HttpServletRequest request) {

        Usuario usuarioAutenticado =
                obtenerUsuarioAutenticado(request);

        if (usuarioAutenticado == null) {
            return new ResponseEntity<>(
                    "No autorizado",
                    HttpStatus.UNAUTHORIZED);
        }

        if (!esAdmin(usuarioAutenticado)
                && !conversacionService.usuarioPertenece(
                        id,
                        usuarioAutenticado.getId())) {
            return new ResponseEntity<>(
                    "No tienes acceso a esta conversacion",
                    HttpStatus.FORBIDDEN);
        }

		ConversacionDTO dto =
				conversacionService.getById(id);

		if (dto != null) {
			return new ResponseEntity<>(
					dto,
					HttpStatus.OK);
		}

		return new ResponseEntity<>(
				HttpStatus.NOT_FOUND);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(
	        @PathVariable Long id,
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

        if (!esAdmin(usuarioAutenticado)) {
            return new ResponseEntity<>(
                    "Solo un administrador puede eliminar la conversacion globalmente",
                    HttpStatus.FORBIDDEN);
        }

	    int status = conversacionService.deleteById(id);

	    if (status == 0) {
            auditoriaService.registrarConCorreo(
                    extraerCorreo(request),
                    "ELIMINAR_CONVERSACION",
                    "CONVERSACIONES",
                    "Conversacion eliminada: " + id,
                    ip, navegador, null, null, null, true);
            return new ResponseEntity<>("Conversacion eliminada", HttpStatus.OK);
        }
        return new ResponseEntity<>(
                "No se encontro la conversacion",
                HttpStatus.NOT_FOUND);
    }

	@DeleteMapping("/{id}/ocultar")
	public ResponseEntity<String> ocultarParaMi(
			@PathVariable Long id,
			HttpServletRequest request) {

		Usuario usuarioAutenticado =
				obtenerUsuarioAutenticado(request);

		if (usuarioAutenticado == null) {
			return new ResponseEntity<>(
					"No autorizado",
					HttpStatus.UNAUTHORIZED);
		}

		int status =
				conversacionService.ocultarParaUsuario(
						id,
						usuarioAutenticado.getId());

		if (status == 0) {
			return ResponseEntity.ok(
					"Conversacion eliminada de tu lista");
		}

		if (status == 2) {
			return new ResponseEntity<>(
					"No tienes acceso a esta conversacion",
					HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(
				"No se encontro la conversacion",
				HttpStatus.NOT_FOUND);
	}

	@PostMapping("/agregarParticipante")
	public ResponseEntity<String> agregarParticipante(
			@RequestBody ParticipanteConversacionDTO dto) {

		int r =
				conversacionService
						.agregarParticipante(dto);

		switch (r) {

		case 0:
			return ResponseEntity.ok(
					"Participante agregado");

		case 1:
			return ResponseEntity.badRequest()
					.body("Conversacion no existe");

		case 2:
			return ResponseEntity.badRequest()
					.body("Usuario no existe");

		case 3:
			return ResponseEntity.badRequest()
					.body("Usuario ya pertenece");

		default:
			return ResponseEntity.internalServerError()
					.body("Error interno");
		}
	}

	@GetMapping("/{id}/participantes")
	public ResponseEntity<?> getParticipantes(
			@PathVariable Long id,
            HttpServletRequest request) {

        Usuario usuarioAutenticado =
                obtenerUsuarioAutenticado(request);

        if (usuarioAutenticado == null) {
            return new ResponseEntity<>(
                    "No autorizado",
                    HttpStatus.UNAUTHORIZED);
        }

        if (!esAdmin(usuarioAutenticado)
                && !conversacionService.usuarioPertenece(
                        id,
                        usuarioAutenticado.getId())) {
            return new ResponseEntity<>(
                    "No tienes acceso a esta conversacion",
                    HttpStatus.FORBIDDEN);
        }

		return ResponseEntity.ok(
				conversacionService.getParticipantes(id));
	}

	@PostMapping("/{id}/join/{usuarioId}")
	public ResponseEntity<String> joinCanal(
			@PathVariable Long id,
			@PathVariable Long usuarioId) {

		ParticipanteConversacionDTO dto =
				new ParticipanteConversacionDTO();

		dto.setConversacionId(id);
		dto.setUsuarioId(usuarioId);

		int r =
				conversacionService
						.agregarParticipante(dto);

		if (r == 0) {
			return ResponseEntity.ok(
					"Te uniste al canal");
		}

		return ResponseEntity.badRequest()
				.body("No se pudo unir");
	}
	

}
