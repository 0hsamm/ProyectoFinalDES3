package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class AmistadDTO {

    private Long solicitudId;
    private Long usuarioId;
    private String usuario;
    private String nombrePersona;
    private String fotoPerfil;
    private String sobreMi;
    private boolean enLinea;
    private LocalDateTime fechaAmistad;

    public AmistadDTO() {
    }

    public AmistadDTO(
            Long solicitudId,
            Long usuarioId,
            String usuario,
            String nombrePersona,
            String fotoPerfil,
            String sobreMi,
            boolean enLinea,
            LocalDateTime fechaAmistad) {
        super();
        this.solicitudId = solicitudId;
        this.usuarioId = usuarioId;
        this.usuario = usuario;
        this.nombrePersona = nombrePersona;
        this.fotoPerfil = fotoPerfil;
        this.sobreMi = sobreMi;
        this.enLinea = enLinea;
        this.fechaAmistad = fechaAmistad;
    }

    public Long getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(Long solicitudId) {
        this.solicitudId = solicitudId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getSobreMi() {
        return sobreMi;
    }

    public void setSobreMi(String sobreMi) {
        this.sobreMi = sobreMi;
    }

    public boolean isEnLinea() {
        return enLinea;
    }

    public void setEnLinea(boolean enLinea) {
        this.enLinea = enLinea;
    }

    public LocalDateTime getFechaAmistad() {
        return fechaAmistad;
    }

    public void setFechaAmistad(
            LocalDateTime fechaAmistad) {
        this.fechaAmistad = fechaAmistad;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                enLinea,
                fechaAmistad,
                fotoPerfil,
                nombrePersona,
                solicitudId,
                sobreMi,
                usuario,
                usuarioId);
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
        AmistadDTO other =
                (AmistadDTO) obj;
        return enLinea == other.enLinea
                && Objects.equals(
                        fechaAmistad,
                        other.fechaAmistad)
                && Objects.equals(
                        fotoPerfil,
                        other.fotoPerfil)
                && Objects.equals(
                        nombrePersona,
                        other.nombrePersona)
                && Objects.equals(
                        solicitudId,
                        other.solicitudId)
                && Objects.equals(
                        sobreMi,
                        other.sobreMi)
                && Objects.equals(
                        usuario,
                        other.usuario)
                && Objects.equals(
                        usuarioId,
                        other.usuarioId);
    }

    @Override
    public String toString() {
        return "AmistadDTO [solicitudId="
                + solicitudId
                + ", usuarioId="
                + usuarioId
                + ", usuario="
                + usuario
                + ", nombrePersona="
                + nombrePersona
                + ", fotoPerfil="
                + fotoPerfil
                + ", sobreMi="
                + sobreMi
                + ", enLinea="
                + enLinea
                + ", fechaAmistad="
                + fechaAmistad
                + "]";
    }
}
