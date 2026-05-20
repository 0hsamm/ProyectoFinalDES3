package co.edu.unbosque.proyectofinal.dto;

import co.edu.unbosque.proyectofinal.enums.EstadoLlamada;
import co.edu.unbosque.proyectofinal.enums.TipoLlamada;


public class LlamadaRespuestaDTO {

	private long id;
	private String canalAgora;   
	private String tokenAgora;   
	private int uidAgora;        
	private TipoLlamada tipoLlamada;
	private EstadoLlamada estadoLlamada;
	private String appIdAgora;   
	private Long conversacionId;
	private Long usuarioLlamanteId;
	private Long usuarioReceptorId;

	public LlamadaRespuestaDTO() {
	}

	public LlamadaRespuestaDTO(long id, String canalAgora, String tokenAgora, int uidAgora,
			TipoLlamada tipoLlamada, EstadoLlamada estadoLlamada, String appIdAgora,
			Long conversacionId, Long usuarioLlamanteId, Long usuarioReceptorId) {
		this.id = id;
		this.canalAgora = canalAgora;
		this.tokenAgora = tokenAgora;
		this.uidAgora = uidAgora;
		this.tipoLlamada = tipoLlamada;
		this.estadoLlamada = estadoLlamada;
		this.appIdAgora = appIdAgora;
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

	public String getTokenAgora() {
		return tokenAgora;
	}

	public void setTokenAgora(String tokenAgora) {
		this.tokenAgora = tokenAgora;
	}

	public int getUidAgora() {
		return uidAgora;
	}

	public void setUidAgora(int uidAgora) {
		this.uidAgora = uidAgora;
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

	public String getAppIdAgora() {
		return appIdAgora;
	}

	public void setAppIdAgora(String appIdAgora) {
		this.appIdAgora = appIdAgora;
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
