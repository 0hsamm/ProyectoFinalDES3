package co.edu.unbosque.proyectofinal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.proyectofinal.dto.RegistroAuditoriaDTO;
import co.edu.unbosque.proyectofinal.service.AuditoriaService;

@RestController
@RequestMapping("/admin/auditoria")
@CrossOrigin(origins = {"http://localhost:8081", "*"})
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(
            AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<List<RegistroAuditoriaDTO>> obtenerTodos() {
        return new ResponseEntity<>(
                auditoriaService.obtenerTodos(),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroAuditoriaDTO> obtenerPorId(
            @PathVariable Long id) {

        RegistroAuditoriaDTO dto =
                auditoriaService.obtenerPorId(id);

        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<RegistroAuditoriaDTO>> obtenerPorUsuario(
            @PathVariable Long idUsuario) {

        return new ResponseEntity<>(
                auditoriaService.obtenerPorUsuario(idUsuario),
                HttpStatus.OK);
    }
}