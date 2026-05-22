package co.edu.unbosque.proyectofinal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.proyectofinal.dto.AmistadDTO;
import co.edu.unbosque.proyectofinal.dto.CrearSolicitudAmistadDTO;
import co.edu.unbosque.proyectofinal.dto.SolicitudAmistadDTO;
import co.edu.unbosque.proyectofinal.security.JwtUtil;
import co.edu.unbosque.proyectofinal.service.AuditoriaService;
import co.edu.unbosque.proyectofinal.service.SolicitudAmistadService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/amistades")
public class AmistadController {

    private final SolicitudAmistadService solicitudAmistadService;

    private final AuditoriaService auditoriaService;

    private final JwtUtil jwtUtil;

    public AmistadController(
            SolicitudAmistadService solicitudAmistadService,
            AuditoriaService auditoriaService,
            JwtUtil jwtUtil) {
        this.solicitudAmistadService =
                solicitudAmistadService;
        this.auditoriaService =
                auditoriaService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/solicitudes")
    public ResponseEntity<String> enviarSolicitud(
            @RequestBody CrearSolicitudAmistadDTO dto,
            HttpServletRequest request) {

        String correo =
                extraerCorreo(request);

        if (correo == null) {
            return new ResponseEntity<>(
                    "No autorizado",
                    HttpStatus.UNAUTHORIZED);
        }

        int estado =
                solicitudAmistadService.enviarSolicitud(
                        correo,
                        dto);

        if (estado == 0) {
            auditoriaService.registrarConCorreo(
                    correo,
                    "ENVIAR_SOLICITUD_AMISTAD",
                    "AMISTADES",
                    "Solicitud enviada a: "
                            + dto.getUsernameDestino(),
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"),
                    null, null, null,
                    true);
            return new ResponseEntity<>(
                    "Solicitud enviada correctamente",
                    HttpStatus.CREATED);
        }

        switch (estado) {
        case 1:
            return new ResponseEntity<>(
                    "Debes indicar el nombre de usuario destino",
                    HttpStatus.BAD_REQUEST);
        case 2:
            return new ResponseEntity<>(
                    "Usuario autenticado no encontrado",
                    HttpStatus.NOT_FOUND);
        case 3:
            return new ResponseEntity<>(
                    "El usuario destino no existe",
                    HttpStatus.NOT_FOUND);
        case 4:
            return new ResponseEntity<>(
                    "No puedes enviarte una solicitud a ti mismo",
                    HttpStatus.BAD_REQUEST);
        case 5:
            return new ResponseEntity<>(
                    "Ya enviaste una solicitud pendiente a ese usuario",
                    HttpStatus.BAD_REQUEST);
        case 6:
            return new ResponseEntity<>(
                    "Ya tienes una solicitud pendiente de ese usuario",
                    HttpStatus.BAD_REQUEST);
        case 7:
            return new ResponseEntity<>(
                    "Ya existe una amistad con ese usuario",
                    HttpStatus.BAD_REQUEST);
        case 8:
            return new ResponseEntity<>(
                    "No puedes enviar una solicitud a ese usuario",
                    HttpStatus.BAD_REQUEST);
        default:
            return new ResponseEntity<>(
                    "Error al enviar la solicitud",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/solicitudes/recibidas")
    public ResponseEntity<List<SolicitudAmistadDTO>>
    obtenerSolicitudesRecibidas(
            HttpServletRequest request) {

        String correo =
                extraerCorreo(request);

        if (correo == null) {
            return new ResponseEntity<>(
                    HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(
                solicitudAmistadService
                        .obtenerSolicitudesRecibidas(
                                correo),
                HttpStatus.OK);
    }

    @GetMapping("/solicitudes/enviadas")
    public ResponseEntity<List<SolicitudAmistadDTO>>
    obtenerSolicitudesEnviadas(
            HttpServletRequest request) {

        String correo =
                extraerCorreo(request);

        if (correo == null) {
            return new ResponseEntity<>(
                    HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(
                solicitudAmistadService
                        .obtenerSolicitudesEnviadas(
                                correo),
                HttpStatus.OK);
    }

    @PostMapping("/solicitudes/{id}/aceptar")
    public ResponseEntity<String> aceptarSolicitud(
            @PathVariable Long id,
            HttpServletRequest request) {

        String correo =
                extraerCorreo(request);

        if (correo == null) {
            return new ResponseEntity<>(
                    "No autorizado",
                    HttpStatus.UNAUTHORIZED);
        }

        int estado =
                solicitudAmistadService.aceptarSolicitud(
                        correo,
                        id);

        if (estado == 0) {
            auditoriaService.registrarConCorreo(
                    correo,
                    "ACEPTAR_SOLICITUD_AMISTAD",
                    "AMISTADES",
                    "Solicitud aceptada: " + id,
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"),
                    null, null, null,
                    true);
            return new ResponseEntity<>(
                    "Solicitud aceptada correctamente",
                    HttpStatus.OK);
        }

        switch (estado) {
        case 1:
            return new ResponseEntity<>(
                    "Usuario autenticado no encontrado",
                    HttpStatus.NOT_FOUND);
        case 2:
            return new ResponseEntity<>(
                    "La solicitud no existe",
                    HttpStatus.NOT_FOUND);
        case 3:
            return new ResponseEntity<>(
                    "No puedes responder esa solicitud",
                    HttpStatus.FORBIDDEN);
        case 4:
            return new ResponseEntity<>(
                    "La solicitud ya fue respondida",
                    HttpStatus.BAD_REQUEST);
        default:
            return new ResponseEntity<>(
                    "Error al aceptar la solicitud",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<String> rechazarSolicitud(
            @PathVariable Long id,
            HttpServletRequest request) {

        String correo =
                extraerCorreo(request);

        if (correo == null) {
            return new ResponseEntity<>(
                    "No autorizado",
                    HttpStatus.UNAUTHORIZED);
        }

        int estado =
                solicitudAmistadService.rechazarSolicitud(
                        correo,
                        id);

        if (estado == 0) {
            auditoriaService.registrarConCorreo(
                    correo,
                    "RECHAZAR_SOLICITUD_AMISTAD",
                    "AMISTADES",
                    "Solicitud rechazada: " + id,
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"),
                    null, null, null,
                    true);
            return new ResponseEntity<>(
                    "Solicitud rechazada correctamente",
                    HttpStatus.OK);
        }

        switch (estado) {
        case 1:
            return new ResponseEntity<>(
                    "Usuario autenticado no encontrado",
                    HttpStatus.NOT_FOUND);
        case 2:
            return new ResponseEntity<>(
                    "La solicitud no existe",
                    HttpStatus.NOT_FOUND);
        case 3:
            return new ResponseEntity<>(
                    "No puedes responder esa solicitud",
                    HttpStatus.FORBIDDEN);
        case 4:
            return new ResponseEntity<>(
                    "La solicitud ya fue respondida",
                    HttpStatus.BAD_REQUEST);
        default:
            return new ResponseEntity<>(
                    "Error al rechazar la solicitud",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<AmistadDTO>> obtenerAmigos(
            HttpServletRequest request) {

        String correo =
                extraerCorreo(request);

        if (correo == null) {
            return new ResponseEntity<>(
                    HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(
                solicitudAmistadService
                        .obtenerAmigos(correo),
                HttpStatus.OK);
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<String> eliminarAmistad(
            @PathVariable Long usuarioId,
            HttpServletRequest request) {

        String correo =
                extraerCorreo(request);

        if (correo == null) {
            return new ResponseEntity<>(
                    "No autorizado",
                    HttpStatus.UNAUTHORIZED);
        }

        int estado =
                solicitudAmistadService.eliminarAmistad(
                        correo,
                        usuarioId);

        if (estado == 0) {
            auditoriaService.registrarConCorreo(
                    correo,
                    "ELIMINAR_AMISTAD",
                    "AMISTADES",
                    "Amistad eliminada con usuario: "
                            + usuarioId,
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"),
                    null, null, null,
                    true);
            return new ResponseEntity<>(
                    "Amistad eliminada correctamente",
                    HttpStatus.OK);
        }

        switch (estado) {
        case 1:
            return new ResponseEntity<>(
                    "Usuario autenticado no encontrado",
                    HttpStatus.NOT_FOUND);
        case 2:
            return new ResponseEntity<>(
                    "No existe una amistad con ese usuario",
                    HttpStatus.NOT_FOUND);
        default:
            return new ResponseEntity<>(
                    "Error al eliminar la amistad",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
