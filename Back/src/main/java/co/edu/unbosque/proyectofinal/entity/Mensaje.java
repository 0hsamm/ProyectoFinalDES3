package co.edu.unbosque.proyectofinal.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.EstatusMensaje;
import co.edu.unbosque.proyectofinal.enums.TipoMensaje;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "mensajes")
public class Mensaje {

	
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_conversacion", nullable = false)
	private Conversacion conversacion;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_remitente", nullable = false)
	private Usuario remitente;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoMensaje tipoMensaje;

	@Lob
	private String contenidoCifrado;

	@Column(length = 120)
	private String vi; //vector inicializador para el cifrado de mensajes

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstatusMensaje estatusMensaje;

	@Column(nullable = false)
	private LocalDateTime horaEnvio;//Cuando el remitente mandó el mensaje

	@Column(nullable = true)
	private LocalDateTime horaLlegada;//Cuando el destinatario recibio el mensaje

	@Column(nullable = true)
	private LocalDateTime horaLeido; //Cuando el destinatario leyó el mensaje

	@Column(nullable = false)
	private boolean fijado = false;

	@Column(nullable = true)
	private LocalDateTime fechaFijado;
	
	@OneToOne(
		    mappedBy = "mensaje",
		    fetch = FetchType.LAZY,
		    cascade = CascadeType.ALL,
		    orphanRemoval = true
		)
		private ArchivoAdjunto adjunto;
	
	public Mensaje() {
		// TODO Auto-generated constructor stub
	}

	public Mensaje(Conversacion conversacion, Usuario remitente, TipoMensaje tipoMensaje, String contenidoCifrado,
			String vi, EstatusMensaje estatusMensaje, LocalDateTime horaEnvio, LocalDateTime horaLlegada,
			LocalDateTime horaLeido, boolean fijado, LocalDateTime fechaFijado, ArchivoAdjunto adjunto) {
		super();
		this.conversacion = conversacion;
		this.remitente = remitente;
		this.tipoMensaje = tipoMensaje;
		this.contenidoCifrado = contenidoCifrado;
		this.vi = vi;
		this.estatusMensaje = estatusMensaje;
		this.horaEnvio = horaEnvio;
		this.horaLlegada = horaLlegada;
		this.horaLeido = horaLeido;
		this.fijado = fijado;
		this.fechaFijado = fechaFijado;
		this.adjunto = adjunto;
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

	public Usuario getRemitente() {
		return remitente;
	}

	public void setRemitente(Usuario remitente) {
		this.remitente = remitente;
	}

	public TipoMensaje getTipoMensaje() {
		return tipoMensaje;
	}

	public void setTipoMensaje(TipoMensaje tipoMensaje) {
		this.tipoMensaje = tipoMensaje;
	}

	public String getContenidoCifrado() {
		return contenidoCifrado;
	}

	public void setContenidoCifrado(String contenidoCifrado) {
		this.contenidoCifrado = contenidoCifrado;
	}

	public String getVi() {
		return vi;
	}

	public void setVi(String vi) {
		this.vi = vi;
	}

	public EstatusMensaje getEstatusMensaje() {
		return estatusMensaje;
	}

	public void setEstatusMensaje(EstatusMensaje estatusMensaje) {
		this.estatusMensaje = estatusMensaje;
	}

	public LocalDateTime getHoraEnvio() {
		return horaEnvio;
	}

	public void setHoraEnvio(LocalDateTime horaEnvio) {
		this.horaEnvio = horaEnvio;
	}

	public LocalDateTime getHoraLlegada() {
		return horaLlegada;
	}

	public void setHoraLlegada(LocalDateTime horaLlegada) {
		this.horaLlegada = horaLlegada;
	}

	public LocalDateTime getHoraLeido() {
		return horaLeido;
	}

	public void setHoraLeido(LocalDateTime horaLeido) {
		this.horaLeido = horaLeido;
	}

	public boolean isFijado() {
		return fijado;
	}

	public void setFijado(boolean fijado) {
		this.fijado = fijado;
	}

	public LocalDateTime getFechaFijado() {
		return fechaFijado;
	}

	public void setFechaFijado(LocalDateTime fechaFijado) {
		this.fechaFijado = fechaFijado;
	}

	public ArchivoAdjunto getAdjunto() {
		return adjunto;
	}

	public void setAdjunto(ArchivoAdjunto adjunto) {
		this.adjunto = adjunto;
	}

	@Override
	public int hashCode() {
		return Objects.hash(adjunto, contenidoCifrado, conversacion, estatusMensaje, fechaFijado, fijado, horaEnvio,
				horaLeido, horaLlegada, id, remitente, tipoMensaje, vi);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mensaje other = (Mensaje) obj;
		return Objects.equals(adjunto, other.adjunto) && Objects.equals(contenidoCifrado, other.contenidoCifrado)
				&& Objects.equals(conversacion, other.conversacion) && estatusMensaje == other.estatusMensaje
				&& Objects.equals(fechaFijado, other.fechaFijado) && fijado == other.fijado
				&& Objects.equals(horaEnvio, other.horaEnvio) && Objects.equals(horaLeido, other.horaLeido)
				&& Objects.equals(horaLlegada, other.horaLlegada) && id == other.id
				&& Objects.equals(remitente, other.remitente) && tipoMensaje == other.tipoMensaje
				&& Objects.equals(vi, other.vi);
	}

	@Override
	public String toString() {
		return "Mensaje [id=" + id + ", conversacion=" + conversacion + ", remitente=" + remitente + ", tipoMensaje="
				+ tipoMensaje + ", contenidoCifrado=" + contenidoCifrado + ", vi=" + vi + ", estatusMensaje="
				+ estatusMensaje + ", horaEnvio=" + horaEnvio + ", horaLlegada=" + horaLlegada + ", horaLeido="
				+ horaLeido + ", fijado=" + fijado + ", fechaFijado=" + fechaFijado + ", adjunto=" + adjunto + "]";
	}
	
	
	
}
