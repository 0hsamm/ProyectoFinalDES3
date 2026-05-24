package co.edu.unbosque.proyectofinal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.TipoConversacion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversaciones")
public class Conversacion {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoConversacion tipoConversacion;

	@Column(nullable = false, length = 120)
	private String encripado;

	@Column(nullable = false)
	private LocalDateTime fechaCreacion; //fecha de creacion del chat

	private LocalDateTime fechaUltimoMensaje; //fecha en la que se mandó el ultimo mensaje

	@OneToMany(mappedBy = "conversacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ParticipanteConversacion> participante = new ArrayList<>();

	@OneToMany(mappedBy = "conversacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Mensaje> mensaje = new ArrayList<>();
	
	
	public Conversacion() {
		// TODO Auto-generated constructor stub
	}


	public Conversacion(TipoConversacion tipoConversacion, String encripado, LocalDateTime fechaCreacion,
			LocalDateTime fechaUltimoMensaje, List<ParticipanteConversacion> participante, List<Mensaje> mensaje) {
		super();
		this.tipoConversacion = tipoConversacion;
		this.encripado = encripado;
		this.fechaCreacion = fechaCreacion;
		this.fechaUltimoMensaje = fechaUltimoMensaje;
		this.participante = participante == null
				? new ArrayList<>()
				: new ArrayList<>(participante);

		this.mensaje = mensaje == null
				? new ArrayList<>()
				: new ArrayList<>(mensaje);
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public TipoConversacion getTipoConversacion() {
		return tipoConversacion;
	}


	public void setTipoConversacion(TipoConversacion tipoConversacion) {
		this.tipoConversacion = tipoConversacion;
	}


	public String getEncripado() {
		return encripado;
	}


	public void setEncripado(String encripado) {
		this.encripado = encripado;
	}


	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}


	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}


	public LocalDateTime getFechaUltimoMensaje() {
		return fechaUltimoMensaje;
	}


	public void setFechaUltimoMensaje(LocalDateTime fechaUltimoMensaje) {
		this.fechaUltimoMensaje = fechaUltimoMensaje;
	}


	public List<ParticipanteConversacion> getParticipante() {
		return participante;
	}


	public void setParticipante(List<ParticipanteConversacion> participante) {
		this.participante = participante == null
				? new ArrayList<>()
				: new ArrayList<>(participante);
	}

	public List<Mensaje> getMensaje() {
		return mensaje;
	}


	public void setMensaje(List<Mensaje> mensaje) {
		this.mensaje = mensaje == null
				? new ArrayList<>()
				: new ArrayList<>(mensaje);
	}


	@Override
	public int hashCode() {
		return Objects.hash(encripado, fechaCreacion, fechaUltimoMensaje, id, mensaje, participante, tipoConversacion);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Conversacion other = (Conversacion) obj;
		return Objects.equals(encripado, other.encripado) && Objects.equals(fechaCreacion, other.fechaCreacion)
				&& Objects.equals(fechaUltimoMensaje, other.fechaUltimoMensaje) && id == other.id
				&& Objects.equals(mensaje, other.mensaje) && Objects.equals(participante, other.participante)
				&& tipoConversacion == other.tipoConversacion;
	}


	@Override
	public String toString() {
		return "Conversacion [id=" + id + ", tipoConversacion=" + tipoConversacion + ", encripado=" + encripado
				+ ", fechaCreacion=" + fechaCreacion + ", fechaUltimoMensaje=" + fechaUltimoMensaje + ", participante="
				+ participante + ", mensaje=" + mensaje + "]";
	}
	
	
	
}
