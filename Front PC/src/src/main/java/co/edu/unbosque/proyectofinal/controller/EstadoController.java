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

    private final EstadoService estadoService;

    public EstadoController(
            EstadoService estadoService) {

        this.estadoService = estadoService;
    }

    @PostMapping
    public ResponseEntity<EstadoDTO> crearEstado(
            @RequestParam Long usuarioId,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) MultipartFile archivo,
            @RequestParam TipoEstado tipo) {

        return ResponseEntity.ok(
                estadoService.crearEstado(
                        usuarioId,
                        texto,
                        archivo,
                        tipo));
    }

    @GetMapping
    public ResponseEntity<List<EstadoDTO>> obtenerEstadosActivos() {

        return ResponseEntity.ok(
                estadoService.obtenerEstadosActivos());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EstadoDTO>> obtenerEstadosUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                estadoService.obtenerEstadosUsuario(usuarioId));
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

    @GetMapping("/{estadoId}/vistas")
    public ResponseEntity<Integer> obtenerCantidadVistas(
            @PathVariable Long estadoId) {

        return ResponseEntity.ok(
                estadoService.obtenerCantidadVistas(estadoId));
    }

    @DeleteMapping("/expirados")
    public ResponseEntity<String> eliminarEstadosExpirados() {

        estadoService.eliminarEstadosExpirados();

        return ResponseEntity.ok("Estados expirados eliminados");
    }
}
