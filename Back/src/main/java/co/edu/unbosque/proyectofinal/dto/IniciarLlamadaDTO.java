package co.edu.unbosque.proyectofinal.dto;

import co.edu.unbosque.proyectofinal.enums.TipoLlamada;

// DTO que manda el frontend para iniciar una llamada
public class IniciarLlamadaDTO {

	private Long conversacionId;
	private Long usuarioLlamanteId;
	private Long usuarioReceptorId;
	private TipoLlamada tipoLlamada; // VOZ o VIDEO

	public IniciarLlamadaDTO() {
	}

	public IniciarLlamadaDTO(Long conversacionId, Long usuarioLlamanteId,
			Long usuarioReceptorId, TipoLlamada tipoLlamada) {
		this.conversacionId = conversacionId;
		this.usuarioLlamanteId = usuarioLlamanteId;
		this.usuarioReceptorId = usuarioReceptorId;
		this.tipoLlamada = tipoLlamada;
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

	public TipoLlamada getTipoLlamada() {
		return tipoLlamada;
	}

	public void setTipoLlamada(TipoLlamada tipoLlamada) {
		this.tipoLlamada = tipoLlamada;
	}

}
