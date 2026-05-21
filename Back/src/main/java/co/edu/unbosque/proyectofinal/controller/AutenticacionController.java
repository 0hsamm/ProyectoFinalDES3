package co.edu.unbosque.proyectofinal.controller;

import java.time.LocalDate;
import java.util.Optional;

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
import co.edu.unbosque.proyectofinal.service.AuditoriaService;
import co.edu.unbosque.proyectofinal.service.AutenticacionService;
import jakarta.servlet.http.HttpServletRequest;

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
    
    private final AuditoriaService auditoriaService;

    private final JwtUtil jwtUtil;

    /**
     * Constructor que inyecta
     * el servicio de autenticación.
     */
    public AutenticacionController(
            AutenticacionService authService,
            JwtUtil jwtUtil,
            AuditoriaService auditoriaService) {

        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.auditoriaService = auditoriaService;
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
            RegistroDTO dto = new RegistroDTO();
            dto.setUsuario(registerRequest.getUsuario());
            dto.setCorreo(registerRequest.getCorreo());
            dto.setNombrePersona(registerRequest.getNombrePersona());
            dto.setContrasena(registerRequest.getContrasena());
            dto.setFechaNacimiento(
                    LocalDate.parse(
                            registerRequest.getFechaNacimiento().toString()));

            authService.registrar(dto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Usuario registrado correctamente. Revisa tu correo para verificar tu cuenta.");

        } catch (UsuarioYaExisteException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginDTO loginRequest,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        String navegador = request.getHeader("User-Agent");

        try {
            LoginDTO dto = new LoginDTO();
            dto.setUsuario(loginRequest.getUsuario());
            dto.setContrasena(loginRequest.getContrasena());

            Usuario usuarioLogeado = authService.login(dto);

            String token = jwtUtil.generateToken(usuarioLogeado);

            RespuestaAutenticacionDTO respuesta =
                    new RespuestaAutenticacionDTO(
                            token,
                            "Bearer",
                            usuarioLogeado.getId(),
                            usuarioLogeado.getUsuario(),
                            usuarioLogeado.getCorreo(),
                            usuarioLogeado.getNombrePersona());

            auditoriaService.registrar(
                    usuarioLogeado,
                    "LOGIN",
                    "AUTH",
                    "Inicio de sesion exitoso",
                    ip,
                    navegador,
                    null, null, null,
                    true);

            return ResponseEntity.status(HttpStatus.OK).body(respuesta);

        } catch (CredencialesInvalidasException e) {

            auditoriaService.registrar(
                    null,
                    "LOGIN_FALLIDO",
                    "AUTH",
                    "Intento de login fallido: " + loginRequest.getUsuario(),
                    ip,
                    navegador,
                    null, null, null,
                    false);

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
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String correo = jwtUtil.extractUsername(jwt);
            
            Optional<Usuario> usuarioOpt = 
                    authService.buscarPorCorreo(correo);
                    
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                usuario.setEnLinea(false);
                authService.guardar(usuario);
            }
        }
        
        return ResponseEntity.ok("Sesion cerrada correctamente");
    }
}
