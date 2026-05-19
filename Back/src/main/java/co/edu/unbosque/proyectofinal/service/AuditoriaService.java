package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.SolicitudGeminiDTO;
import co.edu.unbosque.proyectofinal.entity.RegistroAuditoria;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.TipoAccionIa;
import co.edu.unbosque.proyectofinal.repository.RegistroAuditoriaRepository;

/**
 * Servicio centralizado para registrar auditoria administrativa.
 */
@Service
public class AuditoriaService {

    private final RegistroAuditoriaRepository registroAuditoriaRepository;

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
