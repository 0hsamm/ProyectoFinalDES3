package co.edu.unbosque.proyectofinal.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.TipoAccionIa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Registro funcional de una interaccion entre un usuario y Gemini.
 * Guarda metadatos y vistas cortas para no almacenar informacion sensible.
 */
@Entity
@Table(name = "interaccion_ia")
public class InteraccionIa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoAccionIa tipoAccion;

    @Column(name = "id_conversacion")
    private Long conversacionId;

    @Column(nullable = false, length = 128)
    private String hashSolicitud;

    @Column(length = 160)
    private String vistaPreviaSolicitud;

    @Column(length = 220)
    private String vistaPreviaRespuesta;

    @Column(nullable = false)
    private boolean exitoso;

    @Column(length = 250)
    private String mensajeError;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    public InteraccionIa() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public String getHashSolicitud() {
        return hashSolicitud;
    }

    public void setHashSolicitud(String hashSolicitud) {
        this.hashSolicitud = hashSolicitud;
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

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
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
                exitoso,
                fechaCreacion,
                hashSolicitud,
                id,
                mensajeError,
                tipoAccion,
                vistaPreviaRespuesta,
                vistaPreviaSolicitud);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        InteraccionIa other =
                (InteraccionIa) obj;

        return exitoso == other.exitoso
                && Objects.equals(conversacionId, other.conversacionId)
                && Objects.equals(fechaCreacion, other.fechaCreacion)
                && Objects.equals(hashSolicitud, other.hashSolicitud)
                && Objects.equals(id, other.id)
                && Objects.equals(mensajeError, other.mensajeError)
                && tipoAccion == other.tipoAccion
                && Objects.equals(vistaPreviaRespuesta, other.vistaPreviaRespuesta)
                && Objects.equals(vistaPreviaSolicitud, other.vistaPreviaSolicitud);
    }
}
