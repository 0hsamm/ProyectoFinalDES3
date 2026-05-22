package co.edu.unbosque.proyectofinal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.proyectofinal.dto.EstadoDTO;
import co.edu.unbosque.proyectofinal.enums.TipoEstado;
import co.edu.unbosque.proyectofinal.service.EstadoService;

@RestController
@RequestMapping("/estados")
public class EstadoController {

	private EstadoService estadoService;
	
	
	@PostMapping
    public ResponseEntity<EstadoDTO> crearEstado(

            @RequestParam Long usuarioId,

            @RequestParam(required = false)
            String texto,

            @RequestParam(required = false)
            MultipartFile archivo,

            @RequestParam TipoEstado tipo) {

        EstadoDTO estado =
                estadoService.crearEstado(
                        usuarioId,
                        texto,
                        archivo,
                        tipo);

        return ResponseEntity.ok(estado);
    }

    /**
     * Obtener todos los estados activos.
     */
    @GetMapping
    public ResponseEntity<List<EstadoDTO>>
        obtenerEstadosActivos() {

        List<EstadoDTO> estados =
                estadoService.obtenerEstadosActivos();

        return ResponseEntity.ok(estados);
    }

    /**
     * Obtener estados de un usuario.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EstadoDTO>>
        obtenerEstadosUsuario(

            @PathVariable Long usuarioId) {

        List<EstadoDTO> estados =
                estadoService.obtenerEstadosUsuario(
                        usuarioId);

        return ResponseEntity.ok(estados);
    }

    /**
     * Registrar visualización de estado.
     */
    @PostMapping("/{estadoId}/ver")
    public ResponseEntity<String> registrarVista(

            @PathVariable Long estadoId,

            @RequestParam Long usuarioId) {

        estadoService.registrarVista(
                estadoId,
                usuarioId);

        return ResponseEntity.ok(
                "Vista registrada");
    }

    /**
     * Obtener cantidad de vistas.
     */
    @GetMapping("/{estadoId}/vistas")
    public ResponseEntity<Integer>
        obtenerCantidadVistas(

            @PathVariable Long estadoId) {

        int cantidad =
                estadoService.obtenerCantidadVistas(
                        estadoId);

        return ResponseEntity.ok(cantidad);
    }

    /**
     * Eliminar estados expirados manualmente.
     * (normalmente esto debería hacerse
     * automáticamente con Scheduler)
     */
    @DeleteMapping("/expirados")
    public ResponseEntity<String>
        eliminarEstadosExpirados() {

        estadoService.eliminarEstadosExpirados();

        return ResponseEntity.ok(
                "Estados expirados eliminados");
    }
	
	
}
