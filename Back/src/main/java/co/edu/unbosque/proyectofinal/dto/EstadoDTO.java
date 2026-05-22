package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.TipoEstado;

public class EstadoDTO {

    private Long id;
    private String texto;
    private String mediaUrl;
    private String thumbnailUrl;
    private String mimeType;
    private TipoEstado tipo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
    private Long usuarioId;
    private String usuarioNombre;
    private String fotoPerfil;
    private int cantidadVistas;
    private int cantidadLikes;
    private boolean visto;
    private boolean meGusta;
    private boolean propio;

    public EstadoDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public TipoEstado getTipo() {
        return tipo;
    }

    public void setTipo(TipoEstado tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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

    public int getCantidadVistas() {
        return cantidadVistas;
    }

    public void setCantidadVistas(int cantidadVistas) {
        this.cantidadVistas = cantidadVistas;
    }

    public int getCantidadLikes() {
        return cantidadLikes;
    }

    public void setCantidadLikes(int cantidadLikes) {
        this.cantidadLikes = cantidadLikes;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }

    public boolean isMeGusta() {
        return meGusta;
    }

    public void setMeGusta(boolean meGusta) {
        this.meGusta = meGusta;
    }

    public boolean isPropio() {
        return propio;
    }

    public void setPropio(boolean propio) {
        this.propio = propio;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cantidadLikes, cantidadVistas, fechaCreacion,
                fechaExpiracion, fotoPerfil, id, meGusta, mediaUrl, mimeType,
                propio, texto, thumbnailUrl, tipo, usuarioId, usuarioNombre,
                visto);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EstadoDTO other = (EstadoDTO) obj;
        return cantidadLikes == other.cantidadLikes
                && cantidadVistas == other.cantidadVistas
                && meGusta == other.meGusta
                && propio == other.propio
                && visto == other.visto
                && Objects.equals(fechaCreacion, other.fechaCreacion)
                && Objects.equals(fechaExpiracion, other.fechaExpiracion)
                && Objects.equals(fotoPerfil, other.fotoPerfil)
                && Objects.equals(id, other.id)
                && Objects.equals(mediaUrl, other.mediaUrl)
                && Objects.equals(mimeType, other.mimeType)
                && Objects.equals(texto, other.texto)
                && Objects.equals(thumbnailUrl, other.thumbnailUrl)
                && tipo == other.tipo
                && Objects.equals(usuarioId, other.usuarioId)
                && Objects.equals(usuarioNombre, other.usuarioNombre);
    }
}
