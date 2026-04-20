package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class ParticipanteConversacionDTO {

	private Long id;
    private Long conversacionId;
    private Long usuarioId;
    private LocalDateTime fechaIngresoChat;
    private LocalDateTime fechaUltimoLeido;
	
    
    public ParticipanteConversacionDTO() {
		// TODO Auto-generated constructor stub
	}


	public ParticipanteConversacionDTO(Long id, Long conversacionId, Long usuarioId, LocalDateTime fechaIngresoChat,
			LocalDateTime fechaUltimoLeido) {
		super();
		this.id = id;
		this.conversacionId = conversacionId;
		this.usuarioId = usuarioId;
		this.fechaIngresoChat = fechaIngresoChat;
		this.fechaUltimoLeido = fechaUltimoLeido;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
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


	public LocalDateTime getFechaIngresoChat() {
		return fechaIngresoChat;
	}


	public void setFechaIngresoChat(LocalDateTime fechaIngresoChat) {
		this.fechaIngresoChat = fechaIngresoChat;
	}


	public LocalDateTime getFechaUltimoLeido() {
		return fechaUltimoLeido;
	}


	public void setFechaUltimoLeido(LocalDateTime fechaUltimoLeido) {
		this.fechaUltimoLeido = fechaUltimoLeido;
	}


	@Override
	public int hashCode() {
		return Objects.hash(conversacionId, fechaIngresoChat, fechaUltimoLeido, id, usuarioId);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParticipanteConversacionDTO other = (ParticipanteConversacionDTO) obj;
		return Objects.equals(conversacionId, other.conversacionId)
				&& Objects.equals(fechaIngresoChat, other.fechaIngresoChat)
				&& Objects.equals(fechaUltimoLeido, other.fechaUltimoLeido) && Objects.equals(id, other.id)
				&& Objects.equals(usuarioId, other.usuarioId);
	}


	@Override
	public String toString() {
		return "ParticipanteConversacionDTO [id=" + id + ", conversacionId=" + conversacionId + ", usuarioId="
				+ usuarioId + ", fechaIngresoChat=" + fechaIngresoChat + ", fechaUltimoLeido=" + fechaUltimoLeido + "]";
	}
    
    
    
	
}
