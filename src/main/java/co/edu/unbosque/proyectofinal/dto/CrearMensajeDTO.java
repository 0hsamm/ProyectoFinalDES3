package co.edu.unbosque.proyectofinal.dto;


import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.TipoMensaje;

public class CrearMensajeDTO {

	
	private String usuario;
	private Long conversacionId;
	private TipoMensaje tipoMensaje;
	private String contenido; 
	    
   
	 public CrearMensajeDTO() {
		// TODO Auto-generated constructor stub
	}


	 public CrearMensajeDTO(String usuario, Long conversacionId, TipoMensaje tipoMensaje, String contenido) {
		super();
		this.usuario = usuario;
		this.conversacionId = conversacionId;
		this.tipoMensaje = tipoMensaje;
		this.contenido = contenido;
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


	 public void setConversacionId(Long conversacionId) {
		 this.conversacionId = conversacionId;
	 }





	 public TipoMensaje getTipoMensaje() {
		 return tipoMensaje;
	 }


	 public void setTipoMensaje(TipoMensaje tipoMensaje) {
		 this.tipoMensaje = tipoMensaje;
	 }


	 public String getContenido() {
		 return contenido;
	 }


	 public void setContenido(String contenido) {
		 this.contenido = contenido;
	 }


	 @Override
	 public int hashCode() {
		return Objects.hash(contenido, conversacionId, tipoMensaje, usuario);
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
		return Objects.equals(contenido, other.contenido) && Objects.equals(conversacionId, other.conversacionId)
				&& tipoMensaje == other.tipoMensaje && Objects.equals(usuario, other.usuario);
	 }


	 @Override
	 public String toString() {
		return "CrearMensajeDTO [usuario=" + usuario + ", conversacionId=" + conversacionId + ", tipoMensaje="
				+ tipoMensaje + ", contenido=" + contenido + "]";
	 }


	

	 
    
    
}
