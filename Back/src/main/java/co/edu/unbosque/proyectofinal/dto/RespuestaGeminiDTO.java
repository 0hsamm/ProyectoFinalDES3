package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.TipoAccionIa;

/**
 * Respuesta enviada por el backend a Angular despues de consultar Gemini.
 */
public class RespuestaGeminiDTO {

    private String respuesta;

    private TipoAccionIa tipoAccion;

    private Long conversacionId;

    private LocalDateTime fechaCreacion;

    public RespuestaGeminiDTO() {
    }

    public RespuestaGeminiDTO(
            String respuesta,
            TipoAccionIa tipoAccion,
            Long conversacionId,
            LocalDateTime fechaCreacion) {

        this.respuesta = respuesta;
        this.tipoAccion = tipoAccion;
        this.conversacionId = conversacionId;
        this.fechaCreacion = fechaCreacion;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public TipoAccionIa getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(TipoAccionIa tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public Long getConversacionId() {
        return conversacionId;
    }

    public void setConversacionId(Long conversacionId) {
        this.conversacionId = conversacionId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                conversacionId,
                fechaCreacion,
                respuesta,
                tipoAccion);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        RespuestaGeminiDTO other =
                (RespuestaGeminiDTO) obj;

        return tipoAccion == other.tipoAccion
                && Objects.equals(conversacionId, other.conversacionId)
                && Objects.equals(fechaCreacion, other.fechaCreacion)
                && Objects.equals(respuesta, other.respuesta);
    }
}
