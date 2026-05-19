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
    private boolean visto;
	
    public EstadoDTO() {
		// TODO Auto-generated constructor stub
	}

	public EstadoDTO(String texto, String mediaUrl, String thumbnailUrl, String mimeType, TipoEstado tipo,
			LocalDateTime fechaCreacion, LocalDateTime fechaExpiracion, Long usuarioId, String usuarioNombre,
			String fotoPerfil, int cantidadVistas, boolean visto) {
		super();
		this.texto = texto;
		this.mediaUrl = mediaUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.mimeType = mimeType;
		this.tipo = tipo;
		this.fechaCreacion = fechaCreacion;
		this.fechaExpiracion = fechaExpiracion;
		this.usuarioId = usuarioId;
		this.usuarioNombre = usuarioNombre;
		this.fotoPerfil = fotoPerfil;
		this.cantidadVistas = cantidadVistas;
		this.visto = visto;
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

	public boolean isVisto() {
		return visto;
	}

	public void setVisto(boolean visto) {
		this.visto = visto;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cantidadVistas, fechaCreacion, fechaExpiracion, fotoPerfil, id, mediaUrl, mimeType, texto,
				thumbnailUrl, tipo, usuarioId, usuarioNombre, visto);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EstadoDTO other = (EstadoDTO) obj;
		return cantidadVistas == other.cantidadVistas && Objects.equals(fechaCreacion, other.fechaCreacion)
				&& Objects.equals(fechaExpiracion, other.fechaExpiracion)
				&& Objects.equals(fotoPerfil, other.fotoPerfil) && Objects.equals(id, other.id)
				&& Objects.equals(mediaUrl, other.mediaUrl) && Objects.equals(mimeType, other.mimeType)
				&& Objects.equals(texto, other.texto) && Objects.equals(thumbnailUrl, other.thumbnailUrl)
				&& tipo == other.tipo && Objects.equals(usuarioId, other.usuarioId)
				&& Objects.equals(usuarioNombre, other.usuarioNombre) && visto == other.visto;
	}

	@Override
	public String toString() {
		return "EstadoDTO [id=" + id + ", texto=" + texto + ", mediaUrl=" + mediaUrl + ", thumbnailUrl=" + thumbnailUrl
				+ ", mimeType=" + mimeType + ", tipo=" + tipo + ", fechaCreacion=" + fechaCreacion
				+ ", fechaExpiracion=" + fechaExpiracion + ", usuarioId=" + usuarioId + ", usuarioNombre="
				+ usuarioNombre + ", fotoPerfil=" + fotoPerfil + ", cantidadVistas=" + cantidadVistas + ", visto="
				+ visto + "]";
	}
    
    
    
}
