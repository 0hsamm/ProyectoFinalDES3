package co.edu.unbosque.proyectofinal.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.repository.ParticipanteConversacionRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

/**
 * Servicio de seguridad especifico para validar operaciones de IA.
 */
@Service
public class SeguridadIaService {

    private final UsuarioRepository usuarioRepository;

    private final ParticipanteConversacionRepository
            participanteConversacionRepository;

    public SeguridadIaService(
            UsuarioRepository usuarioRepository,
            ParticipanteConversacionRepository
                    participanteConversacionRepository) {

        this.usuarioRepository = usuarioRepository;
        this.participanteConversacionRepository =
                participanteConversacionRepository;
    }

    /**
     * Obtiene el usuario autenticado desde Spring Security.
     *
     * @return usuario autenticado
     */
    public Usuario obtenerUsuarioAutenticado() {

        Authentication autenticacion =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (autenticacion == null
                || !autenticacion.isAuthenticated()) {

            throw new AccessDeniedException(
                    "Usuario no autenticado");
        }

        Object principal =
                autenticacion.getPrincipal();

        if (principal instanceof Usuario) {
            return (Usuario) principal;
        }

        String correo =
                autenticacion.getName();

        return usuarioRepository
                .findByCorreo(correo)
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Usuario autenticado no existe"));
    }

    /**
     * Valida que el usuario pertenezca a la conversacion antes de usarla
     * como contexto de IA.
     *
     * @param conversacionId identificador de conversacion
     * @param usuario usuario autenticado
     */
    public void validarAccesoConversacion(
            Long conversacionId,
            Usuario usuario) {

        if (conversacionId == null) {
            return;
        }

        boolean participante =
                participanteConversacionRepository
                        .existsByConversacion_IdAndUsuario_Id(
                                conversacionId,
                                usuario.getId());

        if (!participante) {
            throw new AccessDeniedException(
                    "No tienes permiso para usar esta conversacion con IA");
        }
    }
}
