package co.edu.unbosque.proyectofinal.controller;

import java.util.List; 

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.proyectofinal.dto.EstadoDTO;
import co.edu.unbosque.proyectofinal.dto.EstadoInteraccionDTO;
import co.edu.unbosque.proyectofinal.enums.TipoEstado;
import co.edu.unbosque.proyectofinal.service.EstadoService;

@RestController
@RequestMapping("/estados")
public class EstadoController {

    private final EstadoService estadoService;

    public EstadoController(
            EstadoService estadoService) {

        this.estadoService = estadoService;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> manejarErrores(
            RuntimeException e) {

        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @PostMapping
    public ResponseEntity<EstadoDTO> crearEstado(
            @RequestParam Long usuarioId,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) MultipartFile archivo,
            @RequestParam(required = false) TipoEstado tipo) {

        return ResponseEntity.ok(
                estadoService.crearEstado(
                        usuarioId,
                        texto,
                        archivo,
                        tipo));
    }

    @GetMapping
    public ResponseEntity<List<EstadoDTO>> obtenerEstadosActivos(
            @RequestParam(required = false) Long usuarioId) {

        return ResponseEntity.ok(
                estadoService.obtenerEstadosActivos(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EstadoDTO>> obtenerEstadosUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) Long viewerId) {

        return ResponseEntity.ok(
                estadoService.obtenerEstadosUsuario(
                        usuarioId,
                        viewerId));
    }

    @DeleteMapping("/{estadoId}")
    public ResponseEntity<String> eliminarEstado(
            @PathVariable Long estadoId,
            @RequestParam Long usuarioId) {

        estadoService.eliminarEstado(
                estadoId,
                usuarioId);

        return ResponseEntity.ok("Estado eliminado");
    }

    @PostMapping("/{estadoId}/ver")
    public ResponseEntity<String> registrarVista(
            @PathVariable Long estadoId,
            @RequestParam Long usuarioId) {

        estadoService.registrarVista(
                estadoId,
                usuarioId);

        return ResponseEntity.ok("Vista registrada");
    }

    @PostMapping("/{estadoId}/like")
    public ResponseEntity<EstadoDTO> darLike(
            @PathVariable Long estadoId,
            @RequestParam Long usuarioId) {

        return ResponseEntity.ok(
                estadoService.darLike(
                        estadoId,
                        usuarioId));
    }

    @DeleteMapping("/{estadoId}/like")
    public ResponseEntity<EstadoDTO> quitarLike(
            @PathVariable Long estadoId,
            @RequestParam Long usuarioId) {

        return ResponseEntity.ok(
                estadoService.quitarLike(
                        estadoId,
                        usuarioId));
    }

    @GetMapping("/{estadoId}/vistas")
    public ResponseEntity<Integer> obtenerCantidadVistas(
            @PathVariable Long estadoId,
            @RequestParam Long usuarioId) {

        return ResponseEntity.ok(
                estadoService.obtenerCantidadVistas(
                        estadoId,
                        usuarioId));
    }

    @GetMapping("/{estadoId}/vistas/detalle")
    public ResponseEntity<List<EstadoInteraccionDTO>> obtenerVistasDetalle(
            @PathVariable Long estadoId,
            @RequestParam Long usuarioId) {

        return ResponseEntity.ok(
                estadoService.obtenerVistasDetalle(
                        estadoId,
                        usuarioId));
    }

    @GetMapping("/{estadoId}/likes")
    public ResponseEntity<List<EstadoInteraccionDTO>> obtenerLikesDetalle(
            @PathVariable Long estadoId,
            @RequestParam Long usuarioId) {

        return ResponseEntity.ok(
                estadoService.obtenerLikesDetalle(
                        estadoId,
                        usuarioId));
    }

    @DeleteMapping("/expirados")
    public ResponseEntity<String> eliminarEstadosExpirados() {

        estadoService.eliminarEstadosExpirados();

        return ResponseEntity.ok("Estados expirados eliminados");
    }
}
