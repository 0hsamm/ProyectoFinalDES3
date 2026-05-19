package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;

import co.edu.unbosque.proyectofinal.enums.TipoAccionIa;

/**
 * Vista segura del historial de uso de IA.
 * No incluye el texto completo enviado a Gemini.
 */
public class HistorialIaDTO {

    private Long id;

    private TipoAccionIa tipoAccion;

    private Long conversacionId;

    private String vistaPreviaSolicitud;

    private String vistaPreviaRespuesta;

    private boolean exitoso;

    private LocalDateTime fechaCreacion;

    public HistorialIaDTO() {
    }

    public HistorialIaDTO(
            Long id,
            TipoAccionIa tipoAccion,
            Long conversacionId,
            String vistaPreviaSolicitud,
            String vistaPreviaRespuesta,
            boolean exitoso,
            LocalDateTime fechaCreacion) {

        this.id = id;
        this.tipoAccion = tipoAccion;
        this.conversacionId = conversacionId;
        this.vistaPreviaSolicitud = vistaPreviaSolicitud;
        this.vistaPreviaRespuesta = vistaPreviaRespuesta;
        this.exitoso = exitoso;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getVistaPreviaSolicitud() {
        return vistaPreviaSolicitud;
    }

    public void setVistaPreviaSolicitud(String vistaPreviaSolicitud) {
        this.vistaPreviaSolicitud = vistaPreviaSolicitud;
    }

    public String getVistaPreviaRespuesta() {
        return vistaPreviaRespuesta;
    }

    public void setVistaPreviaRespuesta(String vistaPreviaRespuesta) {
        this.vistaPreviaRespuesta = vistaPreviaRespuesta;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
