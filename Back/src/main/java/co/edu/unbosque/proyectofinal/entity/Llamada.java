package co.edu.unbosque.proyectofinal.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.EstadoLlamada;
import co.edu.unbosque.proyectofinal.enums.TipoLlamada;
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

@Entity
@Table(name = "llamadas")
public class Llamada {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	// Nombre del canal en Agora (único por llamada)
	@Column(nullable = false, unique = true, length = 120)
	private String canalAgora;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoLlamada tipoLlamada; // VOZ o VIDEO

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoLlamada estadoLlamada; // INICIADA, ACTIVA, FINALIZADA, PERDIDA

	@Column(nullable = false)
	private LocalDateTime fechaInicio;

	private LocalDateTime fechaFin;

	private Long duracionSegundos;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_usuario_llamante", nullable = false)
	private Usuario usuarioLlamante;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_usuario_receptor", nullable = false)
	private Usuario usuarioReceptor;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_conversacion", nullable = false)
	private Conversacion conversacion;

	public Llamada() {
	}

	public Llamada(String canalAgora, TipoLlamada tipoLlamada, EstadoLlamada estadoLlamada,
			LocalDateTime fechaInicio, LocalDateTime fechaFin, Long duracionSegundos,
			Usuario usuarioLlamante, Usuario usuarioReceptor, Conversacion conversacion) {
		this.canalAgora = canalAgora;
		this.tipoLlamada = tipoLlamada;
		this.estadoLlamada = estadoLlamada;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.duracionSegundos = duracionSegundos;
		this.usuarioLlamante = usuarioLlamante;
		this.usuarioReceptor = usuarioReceptor;
		this.conversacion = conversacion;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCanalAgora() {
		return canalAgora;
	}

	public void setCanalAgora(String canalAgora) {
		this.canalAgora = canalAgora;
	}

	public TipoLlamada getTipoLlamada() {
		return tipoLlamada;
	}

	public void setTipoLlamada(TipoLlamada tipoLlamada) {
		this.tipoLlamada = tipoLlamada;
	}

	public EstadoLlamada getEstadoLlamada() {
		return estadoLlamada;
	}

	public void setEstadoLlamada(EstadoLlamada estadoLlamada) {
		this.estadoLlamada = estadoLlamada;
	}

	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDateTime fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDateTime fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Long getDuracionSegundos() {
		return duracionSegundos;
	}

	public void setDuracionSegundos(Long duracionSegundos) {
		this.duracionSegundos = duracionSegundos;
	}

	public Usuario getUsuarioLlamante() {
		return usuarioLlamante;
	}

	public void setUsuarioLlamante(Usuario usuarioLlamante) {
		this.usuarioLlamante = usuarioLlamante;
	}

	public Usuario getUsuarioReceptor() {
		return usuarioReceptor;
	}

	public void setUsuarioReceptor(Usuario usuarioReceptor) {
		this.usuarioReceptor = usuarioReceptor;
	}

	public Conversacion getConversacion() {
		return conversacion;
	}

	public void setConversacion(Conversacion conversacion) {
		this.conversacion = conversacion;
	}

	@Override
	public int hashCode() {
		return Objects.hash(canalAgora, conversacion, duracionSegundos, estadoLlamada,
				fechaFin, fechaInicio, id, tipoLlamada, usuarioLlamante, usuarioReceptor);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Llamada other = (Llamada) obj;
		return Objects.equals(canalAgora, other.canalAgora)
				&& Objects.equals(conversacion, other.conversacion)
				&& Objects.equals(duracionSegundos, other.duracionSegundos)
				&& estadoLlamada == other.estadoLlamada
				&& Objects.equals(fechaFin, other.fechaFin)
				&& Objects.equals(fechaInicio, other.fechaInicio)
				&& id == other.id
				&& tipoLlamada == other.tipoLlamada
				&& Objects.equals(usuarioLlamante, other.usuarioLlamante)
				&& Objects.equals(usuarioReceptor, other.usuarioReceptor);
	}

	@Override
	public String toString() {
		return "Llamada [id=" + id + ", canalAgora=" + canalAgora
				+ ", tipoLlamada=" + tipoLlamada + ", estadoLlamada=" + estadoLlamada
				+ ", fechaInicio=" + fechaInicio + ", fechaFin=" + fechaFin
				+ ", duracionSegundos=" + duracionSegundos + "]";
	}

}
