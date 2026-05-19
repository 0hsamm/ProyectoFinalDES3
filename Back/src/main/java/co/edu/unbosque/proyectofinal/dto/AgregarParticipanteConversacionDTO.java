package co.edu.unbosque.proyectofinal.dto;

import java.util.Objects;

public class AgregarParticipanteConversacionDTO {

	
	private Long conversacionId;
    private Long usuarioId;
    
    public AgregarParticipanteConversacionDTO() {
		// TODO Auto-generated constructor stub
	}

	public AgregarParticipanteConversacionDTO(Long conversacionId, Long usuarioId) {
		super();
		this.conversacionId = conversacionId;
		this.usuarioId = usuarioId;
	}

	public Long getConversacionId() {
		return conversacionId;
	}

	public void setConversacionId(Long conversacionId) {
		this.conversacionId = conversacionId;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(conversacionId, usuarioId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgregarParticipanteConversacionDTO other = (AgregarParticipanteConversacionDTO) obj;
		return Objects.equals(conversacionId, other.conversacionId) && Objects.equals(usuarioId, other.usuarioId);
	}

	@Override
	public String toString() {
		return "AgregarParticipanteConversacionDTO [conversacionId=" + conversacionId + ", usuarioId=" + usuarioId
				+ "]";
	}
    
    
	
}
