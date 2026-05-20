package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.SolicitudGeminiDTO;
import co.edu.unbosque.proyectofinal.entity.RegistroAuditoria;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.TipoAccionIa;
import co.edu.unbosque.proyectofinal.repository.RegistroAuditoriaRepository;
import java.util.List;
import co.edu.unbosque.proyectofinal.dto.RegistroAuditoriaDTO;

/**
 * Servicio centralizado para registrar auditoria administrativa.
 */
@Service
public class AuditoriaService {

    private RegistroAuditoriaRepository registroAuditoriaRepository;

    public AuditoriaService(
            RegistroAuditoriaRepository registroAuditoriaRepository) {

        this.registroAuditoriaRepository =
                registroAuditoriaRepository;
    }

    /**
     * Registra el uso de una funcion de IA sin guardar el prompt completo.
     *
     * @param usuario usuario que ejecuta la accion
     * @param tipoAccion tipo de accion de IA
     * @param solicitud datos de ubicacion y conversacion
     * @param ip direccion IP detectada
     * @param navegador agente del navegador
     * @param exitoso indica si la accion fue exitosa
     */
    public void registrarUsoIa(
            Usuario usuario,
            TipoAccionIa tipoAccion,
            SolicitudGeminiDTO solicitud,
            String ip,
            String navegador,
            boolean exitoso) {

        RegistroAuditoria registro =
                new RegistroAuditoria();

        registro.setUsuario(usuario);
        registro.setAccion(tipoAccion.name());
        registro.setModulo("IA");
        registro.setDescripcion(
                "Uso del asistente Gemini");
        registro.setFechaAccion(
                LocalDateTime.now());
        registro.setIp(ip);
        registro.setNavegador(
                recortar(navegador, 250));
        registro.setLatitud(
                solicitud.getLatitud());
        registro.setLongitud(
                solicitud.getLongitud());
        registro.setUbicacion(
                recortar(solicitud.getUbicacion(), 160));
        registro.setConversacionId(
                solicitud.getConversacionId());
        registro.setExitoso(exitoso);

        registroAuditoriaRepository.save(registro);
    }
    
    /**
     * Método genérico para registrar cualquier
     * acción del sistema.
     *
     * @param usuario    usuario que ejecuta la acción
     * @param accion     nombre de la acción (ej. LOGIN, ENVIAR_MENSAJE)
     * @param modulo     módulo del sistema (ej. AUTH, MENSAJES)
     * @param descripcion descripción legible de lo ocurrido
     * @param ip         dirección IP
     * @param navegador  agente del navegador
     * @param latitud    latitud del usuario
     * @param longitud   longitud del usuario
     * @param ubicacion  texto de ubicación
     * @param exitoso    si la acción fue exitosa
     */
    public void registrar(
            Usuario usuario,
            String accion,
            String modulo,
            String descripcion,
            String ip,
            String navegador,
            Double latitud,
            Double longitud,
            String ubicacion,
            boolean exitoso) {

        RegistroAuditoria registro = new RegistroAuditoria();
        registro.setUsuario(usuario);
        registro.setAccion(recortar(accion, 80));
        registro.setModulo(recortar(modulo, 80));
        registro.setDescripcion(recortar(descripcion, 250));
        registro.setFechaAccion(LocalDateTime.now());
        registro.setIp(recortar(ip, 80));
        registro.setNavegador(recortar(navegador, 250));
        registro.setLatitud(latitud);
        registro.setLongitud(longitud);
        registro.setUbicacion(recortar(ubicacion, 160));
        registro.setExitoso(exitoso);
        registroAuditoriaRepository.save(registro);
    }

    public List<RegistroAuditoriaDTO> obtenerTodos() {
        return registroAuditoriaRepository
                .findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<RegistroAuditoriaDTO> obtenerPorUsuario(Long idUsuario) {
        return registroAuditoriaRepository
                .findAll()
                .stream()
                .filter(r -> r.getUsuario() != null
                        && r.getUsuario().getId() == idUsuario)
                .map(this::convertirADTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public RegistroAuditoriaDTO obtenerPorId(Long id) {
        return registroAuditoriaRepository
                .findById(id)
                .map(this::convertirADTO)
                .orElse(null);
    }

    private RegistroAuditoriaDTO convertirADTO(
            RegistroAuditoria registro) {

        RegistroAuditoriaDTO dto = new RegistroAuditoriaDTO();
        dto.setId(registro.getId());
        dto.setAccion(registro.getAccion());
        dto.setModulo(registro.getModulo());
        dto.setDescripcion(registro.getDescripcion());
        dto.setFechaAccion(registro.getFechaAccion());
        dto.setIp(registro.getIp());
        dto.setNavegador(registro.getNavegador());
        dto.setLatitud(registro.getLatitud());
        dto.setLongitud(registro.getLongitud());
        dto.setUbicacion(registro.getUbicacion());
        dto.setConversacionId(registro.getConversacionId());
        dto.setExitoso(registro.isExitoso());

        if (registro.getUsuario() != null) {
            dto.setUsuarioCorreo(
                    registro.getUsuario().getCorreo());
            dto.setUsuarioNombre(
                    registro.getUsuario().getNombrePersona());
        }

        return dto;
    }

    private String recortar(
            String valor,
            int maximo) {

        if (valor == null) {
            return null;
        }

        if (valor.length() <= maximo) {
            return valor;
        }

        return valor.substring(0, maximo);
    }
}
