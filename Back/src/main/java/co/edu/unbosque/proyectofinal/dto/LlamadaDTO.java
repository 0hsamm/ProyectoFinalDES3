package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.EstadoLlamada;
import co.edu.unbosque.proyectofinal.enums.TipoLlamada;

public class LlamadaDTO {

	private long id;
	private String canalAgora;
	private TipoLlamada tipoLlamada;
	private EstadoLlamada estadoLlamada;
	private LocalDateTime fechaInicio;
	private LocalDateTime fechaFin;
	private Long duracionSegundos;
	private Long conversacionId;
	private Long usuarioLlamanteId;
	private Long usuarioReceptorId;
	private String usuarioLlamanteNombre;
	private String usuarioReceptorNombre;

	public LlamadaDTO() {
	}
	
	public LlamadaDTO(String canalAgora, TipoLlamada tipoLlamada, EstadoLlamada estadoLlamada,
			LocalDateTime fechaInicio, LocalDateTime fechaFin, Long duracionSegundos, Long conversacionId,
			Long usuarioLlamanteId, Long usuarioReceptorId) {
		super();
		this.canalAgora = canalAgora;
		this.tipoLlamada = tipoLlamada;
		this.estadoLlamada = estadoLlamada;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.duracionSegundos = duracionSegundos;
		this.conversacionId = conversacionId;
		this.usuarioLlamanteId = usuarioLlamanteId;
		this.usuarioReceptorId = usuarioReceptorId;
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

	public Long getConversacionId() {
		return conversacionId;
	}

	public void setConversacionId(Long conversacionId) {
		this.conversacionId = conversacionId;
	}

	public Long getUsuarioLlamanteId() {
		return usuarioLlamanteId;
	}

	public void setUsuarioLlamanteId(Long usuarioLlamanteId) {
		this.usuarioLlamanteId = usuarioLlamanteId;
	}

	public Long getUsuarioReceptorId() {
		return usuarioReceptorId;
	}

	public void setUsuarioReceptorId(Long usuarioReceptorId) {
		this.usuarioReceptorId = usuarioReceptorId;
	}

	public String getUsuarioLlamanteNombre() {
		return usuarioLlamanteNombre;
	}

	public void setUsuarioLlamanteNombre(String usuarioLlamanteNombre) {
		this.usuarioLlamanteNombre = usuarioLlamanteNombre;
	}

	public String getUsuarioReceptorNombre() {
		return usuarioReceptorNombre;
	}

	public void setUsuarioReceptorNombre(String usuarioReceptorNombre) {
		this.usuarioReceptorNombre = usuarioReceptorNombre;
	}

	@Override
	public int hashCode() {
		return Objects.hash(canalAgora, conversacionId, duracionSegundos, estadoLlamada, fechaFin, fechaInicio, id,
				tipoLlamada, usuarioLlamanteId, usuarioReceptorId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LlamadaDTO other = (LlamadaDTO) obj;
		return Objects.equals(canalAgora, other.canalAgora) && Objects.equals(conversacionId, other.conversacionId)
				&& Objects.equals(duracionSegundos, other.duracionSegundos) && estadoLlamada == other.estadoLlamada
				&& Objects.equals(fechaFin, other.fechaFin) && Objects.equals(fechaInicio, other.fechaInicio)
				&& id == other.id && tipoLlamada == other.tipoLlamada
				&& Objects.equals(usuarioLlamanteId, other.usuarioLlamanteId)
				&& Objects.equals(usuarioReceptorId, other.usuarioReceptorId);
	}

	@Override
	public String toString() {
		return "LlamadaDTO [id=" + id + ", canalAgora=" + canalAgora + ", tipoLlamada=" + tipoLlamada
				+ ", estadoLlamada=" + estadoLlamada + ", fechaInicio=" + fechaInicio + ", fechaFin=" + fechaFin
				+ ", duracionSegundos=" + duracionSegundos + ", conversacionId=" + conversacionId
				+ ", usuarioLlamanteId=" + usuarioLlamanteId + ", usuarioReceptorId=" + usuarioReceptorId + "]";
	}

}
