package co.edu.unbosque.proyectofinal.dto;

import java.util.Objects;

import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.proyectofinal.enums.TipoMensaje;

public class CrearMensajeDTO {

	private String usuario;

	private Long conversacionId;

	private TipoMensaje tipoMensaje;

	private String contenido;
	
	private ArchivoAdjuntoDTO adjunto;
	
	private MultipartFile archivo;
	

	public CrearMensajeDTO() {
	}

	

	public CrearMensajeDTO(String usuario, Long conversacionId, TipoMensaje tipoMensaje, String contenido,
			ArchivoAdjuntoDTO adjunto, MultipartFile archivo) {
		super();
		this.usuario = usuario;
		this.conversacionId = conversacionId;
		this.tipoMensaje = tipoMensaje;
		this.contenido = contenido;
		this.adjunto = adjunto;
		this.archivo = archivo;
	}



	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Long getConversacionId() {
		return conversacionId;
	}

	public void setConversacionId(
			Long conversacionId) {

		this.conversacionId =
				conversacionId;
	}

	public TipoMensaje getTipoMensaje() {
		return tipoMensaje;
	}

	public void setTipoMensaje(
			TipoMensaje tipoMensaje) {

		this.tipoMensaje = tipoMensaje;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	
	public ArchivoAdjuntoDTO getAdjunto() {
		return adjunto;
	}

	public void setAdjunto(ArchivoAdjuntoDTO adjunto) {
		this.adjunto = adjunto;
	}
	
	

	public MultipartFile getArchivo() {
		return archivo;
	}



	public void setArchivo(MultipartFile archivo) {
		this.archivo = archivo;
	}



	



	@Override
	public int hashCode() {
		return Objects.hash(adjunto, archivo, contenido, conversacionId, tipoMensaje, usuario);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CrearMensajeDTO other = (CrearMensajeDTO) obj;
		return Objects.equals(adjunto, other.adjunto) && Objects.equals(archivo, other.archivo)
				&& Objects.equals(contenido, other.contenido)
				&& Objects.equals(conversacionId, other.conversacionId) && tipoMensaje == other.tipoMensaje
				&& Objects.equals(usuario, other.usuario);
	}



	@Override
	public String toString() {
		return "CrearMensajeDTO [usuario=" + usuario + ", conversacionId=" + conversacionId + ", tipoMensaje="
				+ tipoMensaje + ", contenido=" + contenido + ", adjunto=" + adjunto + ", archivo=" + archivo
				+  "]";
	}



	

	

}