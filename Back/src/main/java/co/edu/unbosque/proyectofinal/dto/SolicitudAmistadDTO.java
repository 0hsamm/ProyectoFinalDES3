package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.EstadoSolicitudAmistad;

public class SolicitudAmistadDTO {

    private Long id;
    private Long solicitanteId;
    private String solicitanteUsuario;
    private String solicitanteNombre;
    private Long receptorId;
    private String receptorUsuario;
    private String receptorNombre;
    private EstadoSolicitudAmistad estado;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRespuesta;

    public SolicitudAmistadDTO() {
    }

    public SolicitudAmistadDTO(
            Long id,
            Long solicitanteId,
            String solicitanteUsuario,
            String solicitanteNombre,
            Long receptorId,
            String receptorUsuario,
            String receptorNombre,
            EstadoSolicitudAmistad estado,
            LocalDateTime fechaSolicitud,
            LocalDateTime fechaRespuesta) {
        super();
        this.id = id;
        this.solicitanteId = solicitanteId;
        this.solicitanteUsuario = solicitanteUsuario;
        this.solicitanteNombre = solicitanteNombre;
        this.receptorId = receptorId;
        this.receptorUsuario = receptorUsuario;
        this.receptorNombre = receptorNombre;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaRespuesta = fechaRespuesta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSolicitanteId() {
        return solicitanteId;
    }

    public void setSolicitanteId(Long solicitanteId) {
        this.solicitanteId = solicitanteId;
    }

    public String getSolicitanteUsuario() {
        return solicitanteUsuario;
    }

    public void setSolicitanteUsuario(
            String solicitanteUsuario) {
        this.solicitanteUsuario = solicitanteUsuario;
    }

    public String getSolicitanteNombre() {
        return solicitanteNombre;
    }

    public void setSolicitanteNombre(
            String solicitanteNombre) {
        this.solicitanteNombre = solicitanteNombre;
    }

    public Long getReceptorId() {
        return receptorId;
    }

    public void setReceptorId(Long receptorId) {
        this.receptorId = receptorId;
    }

    public String getReceptorUsuario() {
        return receptorUsuario;
    }

    public void setReceptorUsuario(
            String receptorUsuario) {
        this.receptorUsuario = receptorUsuario;
    }

    public String getReceptorNombre() {
        return receptorNombre;
    }

    public void setReceptorNombre(
            String receptorNombre) {
        this.receptorNombre = receptorNombre;
    }

    public EstadoSolicitudAmistad getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoSolicitudAmistad estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(
            LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(
            LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                estado,
                fechaRespuesta,
                fechaSolicitud,
                id,
                receptorId,
                receptorNombre,
                receptorUsuario,
                solicitanteId,
                solicitanteNombre,
                solicitanteUsuario);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SolicitudAmistadDTO other =
                (SolicitudAmistadDTO) obj;
        return estado == other.estado
                && Objects.equals(
                        fechaRespuesta,
                        other.fechaRespuesta)
                && Objects.equals(
                        fechaSolicitud,
                        other.fechaSolicitud)
                && Objects.equals(id, other.id)
                && Objects.equals(
                        receptorId,
                        other.receptorId)
                && Objects.equals(
                        receptorNombre,
                        other.receptorNombre)
                && Objects.equals(
                        receptorUsuario,
                        other.receptorUsuario)
                && Objects.equals(
                        solicitanteId,
                        other.solicitanteId)
                && Objects.equals(
                        solicitanteNombre,
                        other.solicitanteNombre)
                && Objects.equals(
                        solicitanteUsuario,
                        other.solicitanteUsuario);
    }

    @Override
    public String toString() {
        return "SolicitudAmistadDTO [id="
                + id
                + ", solicitanteId="
                + solicitanteId
                + ", solicitanteUsuario="
                + solicitanteUsuario
                + ", solicitanteNombre="
                + solicitanteNombre
                + ", receptorId="
                + receptorId
                + ", receptorUsuario="
                + receptorUsuario
                + ", receptorNombre="
                + receptorNombre
                + ", estado="
                + estado
                + ", fechaSolicitud="
                + fechaSolicitud
                + ", fechaRespuesta="
                + fechaRespuesta
                + "]";
    }
}
