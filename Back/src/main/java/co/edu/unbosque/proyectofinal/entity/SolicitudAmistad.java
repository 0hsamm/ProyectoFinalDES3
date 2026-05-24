package co.edu.unbosque.proyectofinal.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.EstadoSolicitudAmistad;
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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "solicitud_amistad",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "id_solicitante",
                                "id_receptor"
                        })
        })
public class SolicitudAmistad {

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_solicitante", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_receptor", nullable = false)
    private Usuario receptor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitudAmistad estado;

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud;

    private LocalDateTime fechaRespuesta;

    public SolicitudAmistad() {
    }

    public SolicitudAmistad(
            Usuario solicitante,
            Usuario receptor,
            EstadoSolicitudAmistad estado,
            LocalDateTime fechaSolicitud,
            LocalDateTime fechaRespuesta) {
        super();
        this.solicitante = solicitante;
        this.receptor = receptor;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaRespuesta = fechaRespuesta;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public Usuario getReceptor() {
        return receptor;
    }

    public void setReceptor(Usuario receptor) {
        this.receptor = receptor;
    }

    public EstadoSolicitudAmistad getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitudAmistad estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                estado,
                fechaRespuesta,
                fechaSolicitud,
                id,
                receptor,
                solicitante);
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
        SolicitudAmistad other =
                (SolicitudAmistad) obj;
        return estado == other.estado
                && Objects.equals(
                        fechaRespuesta,
                        other.fechaRespuesta)
                && Objects.equals(
                        fechaSolicitud,
                        other.fechaSolicitud)
                && id == other.id
                && Objects.equals(
                        receptor,
                        other.receptor)
                && Objects.equals(
                        solicitante,
                        other.solicitante);
    }

    @Override
    public String toString() {
        return "SolicitudAmistad [id="
                + id
                + ", solicitante="
                + solicitante
                + ", receptor="
                + receptor
                + ", estado="
                + estado
                + ", fechaSolicitud="
                + fechaSolicitud
                + ", fechaRespuesta="
                + fechaRespuesta
                + "]";
    }
}
