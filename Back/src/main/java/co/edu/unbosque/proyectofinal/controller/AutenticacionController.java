package co.edu.unbosque.proyectofinal.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.proyectofinal.dto.RespuestaAutenticacionDTO;
import co.edu.unbosque.proyectofinal.dto.LoginDTO;
import co.edu.unbosque.proyectofinal.dto.RegistroDTO;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.exception.CredencialesInvalidasException;
import co.edu.unbosque.proyectofinal.exception.UsuarioYaExisteException;
import co.edu.unbosque.proyectofinal.security.JwtUtil;
import co.edu.unbosque.proyectofinal.service.AutenticacionService;

/**
 * Controlador encargado de manejar
 * la autenticación y registro
 * de usuarios.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {
        "http://localhost:8081",
        "*"
})
public class AutenticacionController {

    private final AutenticacionService authService;

    private final JwtUtil jwtUtil;

    /**
     * Constructor que inyecta
     * el servicio de autenticación.
     */
    public AutenticacionController(
            AutenticacionService authService,
            JwtUtil jwtUtil) {

        this.authService =
                authService;

        this.jwtUtil =
                jwtUtil;
    }

    /**
     * Permite registrar
     * un nuevo usuario
     * en el sistema.
     *
     * @param registerRequest
     * datos del usuario
     * a registrar
     *
     * @return respuesta
     * del proceso de registro
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registro(
            @RequestBody RegistroDTO registerRequest) {

        try {

            RegistroDTO dto =
                    new RegistroDTO();

            dto.setUsuario(
                    registerRequest.getUsuario());

            dto.setCorreo(
                    registerRequest.getCorreo());

            dto.setNombrePersona(
                    registerRequest.getNombrePersona());

            dto.setContrasena(
                    registerRequest.getContrasena());

            dto.setFechaNacimiento(
                    LocalDate.parse(
                            registerRequest
                                    .getFechaNacimiento()
                                    .toString()));

            Usuario usuarioCreado =
                    authService.registrar(dto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(usuarioCreado);

        }

        catch (
                UsuarioYaExisteException e
        ) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }

        catch (
                RuntimeException e
        ) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Realiza el proceso
     * de autenticación
     * de un usuario.
     *
     * @param loginRequest
     * datos de login
     *
     * @return usuario autenticado
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginDTO loginRequest) {

        try {

            LoginDTO dto =
                    new LoginDTO();

            dto.setUsuario(
                    loginRequest.getUsuario());

            dto.setContrasena(
                    loginRequest.getContrasena());

            Usuario usuarioLogeado =
                    authService.login(dto);

            String token =
                    jwtUtil.generateToken(
                            usuarioLogeado);

            RespuestaAutenticacionDTO respuesta =
                    new RespuestaAutenticacionDTO(
                            token,
                            "Bearer",
                            usuarioLogeado.getId(),
                            usuarioLogeado.getUsuario(),
                            usuarioLogeado.getCorreo(),
                            usuarioLogeado.getNombrePersona());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(respuesta);
        }

        catch (
                CredencialesInvalidasException e
        ) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Verifica una cuenta
     * utilizando el token
     * enviado al correo.
     *
     * @param token token de verificación
     *
     * @return estado de la verificación
     */
    @GetMapping("/verificar")
    public ResponseEntity<?> verificarCuenta(
            @RequestParam String token) {

        boolean verificado =
                authService.verificarCuenta(token);

        if (verificado) {

            return ResponseEntity.ok(
                    "Cuenta verificada correctamente");
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Token invalido o expirado");
    }
}
