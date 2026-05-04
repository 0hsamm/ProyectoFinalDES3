package co.edu.unbosque.proyectofinal.dto;

import java.util.List;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.TipoConversacion;

public class CrearConversacionDTO {

	private TipoConversacion tipoConversacion;
	private List<Long> usuariosId;
	
	public CrearConversacionDTO() {
		// TODO Auto-generated constructor stub
	}




	public TipoConversacion getTipoConversacion() {
		return tipoConversacion;
	}




	public void setTipoConversacion(TipoConversacion tipoConversacion) {
		this.tipoConversacion = tipoConversacion;
	}




	public List<Long> getUsuariosId() {
		return usuariosId;
	}




	public void setUsuariosId(List<Long> usuariosId) {
		this.usuariosId = usuariosId;
	}




	public CrearConversacionDTO(TipoConversacion tipoConversacion, List<Long> usuariosId) {
		super();
		this.tipoConversacion = tipoConversacion;
		this.usuariosId = usuariosId;
	}




	@Override
	public int hashCode() {
		return Objects.hash(tipoConversacion, usuariosId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CrearConversacionDTO other = (CrearConversacionDTO) obj;
		return tipoConversacion == other.tipoConversacion && Objects.equals(usuariosId, other.usuariosId);
	}

	@Override
	public String toString() {
		return "CrearConversacionDTO [tipoConversacion=" + tipoConversacion + ", usuariosId=" + usuariosId + "]";
	}
	
	
	
}
