package co.edu.unbosque.proyectofinal.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.RolParticipante;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "participante_conversacion", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "id_conversacion", "id_usuario" }) })
public class ParticipanteConversacion {

	
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_conversacion", nullable = false)
	private Conversacion conversacion;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario usuario;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RolParticipante rol;
	
	@Column(nullable = false)
	private LocalDateTime fechaIngresoChat; //la fecha en la que se unio al chat

	@Column(nullable = false)
	private LocalDateTime fechaUltimoLeido; //la fecha en la que se leyó el último mensaje
	

	public ParticipanteConversacion() {
		// TODO Auto-generated constructor stub
	}


	


	public ParticipanteConversacion(Conversacion conversacion, Usuario usuario, RolParticipante rol,
			LocalDateTime fechaIngresoChat, LocalDateTime fechaUltimoLeido) {
		super();
		this.conversacion = conversacion;
		this.usuario = usuario;
		this.rol = rol;
		this.fechaIngresoChat = fechaIngresoChat;
		this.fechaUltimoLeido = fechaUltimoLeido;
	}





	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public Conversacion getConversacion() {
		return conversacion;
	}


	public void setConversacion(Conversacion conversacion) {
		this.conversacion = conversacion;
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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





	public RolParticipante getRol() {
		return rol;
	}





	public void setRol(RolParticipante rol) {
		this.rol = rol;
	}





	@Override
	public int hashCode() {
		return Objects.hash(conversacion, fechaIngresoChat, fechaUltimoLeido, id, rol, usuario);
	}





	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParticipanteConversacion other = (ParticipanteConversacion) obj;
		return Objects.equals(conversacion, other.conversacion)
				&& Objects.equals(fechaIngresoChat, other.fechaIngresoChat)
				&& Objects.equals(fechaUltimoLeido, other.fechaUltimoLeido) && id == other.id && rol == other.rol
				&& Objects.equals(usuario, other.usuario);
	}





	@Override
	public String toString() {
		return "ParticipanteConversacion [id=" + id + ", conversacion=" + conversacion + ", usuario=" + usuario
				+ ", rol=" + rol + ", fechaIngresoChat=" + fechaIngresoChat + ", fechaUltimoLeido=" + fechaUltimoLeido
				+ "]";
	}
	
	


	




	
	
}
