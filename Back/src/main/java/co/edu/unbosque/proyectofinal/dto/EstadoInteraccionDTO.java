package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;

public class EstadoInteraccionDTO {

    private Long usuarioId;
    private String usuario;
    private String usuarioNombre;
    private String fotoPerfil;
    private LocalDateTime fecha;

    public EstadoInteraccionDTO() {
    }

    public EstadoInteraccionDTO(
            Long usuarioId,
            String usuario,
            String usuarioNombre,
            String fotoPerfil,
            LocalDateTime fecha) {

        this.usuarioId = usuarioId;
        this.usuario = usuario;
        this.usuarioNombre = usuarioNombre;
        this.fotoPerfil = fotoPerfil;
        this.fecha = fecha;
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

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
