package co.edu.unbosque.proyectofinal.dto;

import java.util.Objects;

public class ArchivoAdjuntoDTO {

	
	private Long id;
    private Long mensajeId;
    private String nombreOriginalArchivo;
    private String formatoArchivo;
    private long tamanoArchivo;
    private String url;
    private String publicId; //Id de cloudinary
    private Integer duracion; //Duración de los audios
	
    
    public ArchivoAdjuntoDTO() {
		// TODO Auto-generated constructor stub
	}


	public ArchivoAdjuntoDTO(Long id, Long mensajeId, String nombreOriginalArchivo, String formatoArchivo,
			long tamanoArchivo, String url, String publicId, Integer duracion) {
		super();
		this.id = id;
		this.mensajeId = mensajeId;
		this.nombreOriginalArchivo = nombreOriginalArchivo;
		this.formatoArchivo = formatoArchivo;
		this.tamanoArchivo = tamanoArchivo;
		this.url = url;
		this.publicId = publicId;
		this.duracion = duracion;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getMensajeId() {
		return mensajeId;
	}


	public void setMensajeId(Long mensajeId) {
		this.mensajeId = mensajeId;
	}


	public String getNombreOriginalArchivo() {
		return nombreOriginalArchivo;
	}


	public void setNombreOriginalArchivo(String nombreOriginalArchivo) {
		this.nombreOriginalArchivo = nombreOriginalArchivo;
	}


	public String getFormatoArchivo() {
		return formatoArchivo;
	}


	public void setFormatoArchivo(String formatoArchivo) {
		this.formatoArchivo = formatoArchivo;
	}


	public long getTamanoArchivo() {
		return tamanoArchivo;
	}


	public void setTamanoArchivo(long tamanoArchivo) {
		this.tamanoArchivo = tamanoArchivo;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}
	


	public String getPublicId() {
		return publicId;
	}


	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}


	public Integer getDuracion() {
		return duracion;
	}


	public void setDuracion(Integer duracion) {
		this.duracion = duracion;
	}


	@Override
	public int hashCode() {
		return Objects.hash(duracion, formatoArchivo, id, mensajeId, nombreOriginalArchivo, publicId, tamanoArchivo,
				url);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArchivoAdjuntoDTO other = (ArchivoAdjuntoDTO) obj;
		return Objects.equals(duracion, other.duracion) && Objects.equals(formatoArchivo, other.formatoArchivo)
				&& Objects.equals(id, other.id) && Objects.equals(mensajeId, other.mensajeId)
				&& Objects.equals(nombreOriginalArchivo, other.nombreOriginalArchivo)
				&& Objects.equals(publicId, other.publicId) && tamanoArchivo == other.tamanoArchivo
				&& Objects.equals(url, other.url);
	}


	@Override
	public String toString() {
		return "ArchivoAdjuntoDTO [id=" + id + ", mensajeId=" + mensajeId + ", nombreOriginalArchivo="
				+ nombreOriginalArchivo + ", formatoArchivo=" + formatoArchivo + ", tamanoArchivo=" + tamanoArchivo
				+ ", url=" + url + ", publicId=" + publicId + ", duracion=" + duracion + "]";
	}


    
    
    
}
