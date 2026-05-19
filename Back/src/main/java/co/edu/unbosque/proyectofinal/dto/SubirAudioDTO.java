package co.edu.unbosque.proyectofinal.dto;

import java.util.Objects;

public class SubirAudioDTO {

	
	private Long conversacionId;
	private String usuario;
	
	
	public SubirAudioDTO() {
		// TODO Auto-generated constructor stub
	}


	public SubirAudioDTO(Long conversacionId, String usuario) {
		super();
		this.conversacionId = conversacionId;
		this.usuario = usuario;
	}


	public Long getConversacionId() {
		return conversacionId;
	}


	public void setConversacionId(Long conversacionId) {
		this.conversacionId = conversacionId;
	}


	public String getUsuario() {
		return usuario;
	}


	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}


	@Override
	public int hashCode() {
		return Objects.hash(conversacionId, usuario);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubirAudioDTO other = (SubirAudioDTO) obj;
		return Objects.equals(conversacionId, other.conversacionId) && Objects.equals(usuario, other.usuario);
	}


	@Override
	public String toString() {
		return "SubirAudioDTO [conversacionId=" + conversacionId + ", usuario=" + usuario + "]";
	}
	
	
	
}
