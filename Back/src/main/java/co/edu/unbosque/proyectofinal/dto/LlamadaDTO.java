package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;

import co.edu.unbosque.proyectofinal.enums.EstadoLlamada;
import co.edu.unbosque.proyectofinal.enums.TipoLlamada;

// DTO general para consultar el historial de llamadas
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

	public LlamadaDTO() {
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

}
