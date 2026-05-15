package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.EstatusMensaje;
import co.edu.unbosque.proyectofinal.enums.TipoMensaje;

public class CrearMensajeDTO {

	private Long id;

    private Long conversacionId;
    private Long remitenteId;
    private TipoMensaje tipoMensaje;
    private String contenido; //Contenido no cifrado
    private EstatusMensaje estatusMensaje;
    private LocalDateTime horaEnvio;
    private LocalDateTime horaLlegada;
    private LocalDateTime horaLeido;
    private ArchivoAdjuntoDTO adjunto;
	
    
    public CrearMensajeDTO() {
		// TODO Auto-generated constructor stub
	}


	public CrearMensajeDTO(Long id, Long conversacionId, Long remitenteId, TipoMensaje tipoMensaje, String contenido,
			EstatusMensaje estatusMensaje, LocalDateTime horaEnvio, LocalDateTime horaLlegada, LocalDateTime horaLeido,
			ArchivoAdjuntoDTO adjunto) {
		super();
		this.id = id;
		this.conversacionId = conversacionId;
		this.remitenteId = remitenteId;
		this.tipoMensaje = tipoMensaje;
		this.contenido = contenido;
		this.estatusMensaje = estatusMensaje;
		this.horaEnvio = horaEnvio;
		this.horaLlegada = horaLlegada;
		this.horaLeido = horaLeido;
		this.adjunto = adjunto;
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


	public Long getRemitenteId() {
		return remitenteId;
	}


	public void setRemitenteId(Long remitenteId) {
		this.remitenteId = remitenteId;
	}


	public TipoMensaje getTipoMensaje() {
		return tipoMensaje;
	}


	public void setTipoMensaje(TipoMensaje tipoMensaje) {
		this.tipoMensaje = tipoMensaje;
	}


	public String getContenido() {
		return contenido;
	}


	public void setContenido(String contenido) {
		this.contenido = contenido;
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


	public ArchivoAdjuntoDTO getAdjunto() {
		return adjunto;
	}


	public void setAdjunto(ArchivoAdjuntoDTO adjunto) {
		this.adjunto = adjunto;
	}


	@Override
	public int hashCode() {
		return Objects.hash(adjunto, contenido, conversacionId, estatusMensaje, horaEnvio, horaLeido, horaLlegada, id,
				remitenteId, tipoMensaje);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CrearMensajeDTO other = (CrearMensajeDTO) obj;
		return Objects.equals(adjunto, other.adjunto) && Objects.equals(contenido, other.contenido)
				&& Objects.equals(conversacionId, other.conversacionId) && estatusMensaje == other.estatusMensaje
				&& Objects.equals(horaEnvio, other.horaEnvio) && Objects.equals(horaLeido, other.horaLeido)
				&& Objects.equals(horaLlegada, other.horaLlegada) && Objects.equals(id, other.id)
				&& Objects.equals(remitenteId, other.remitenteId) && tipoMensaje == other.tipoMensaje;
	}


	@Override
	public String toString() {
		return "CrearMensajeDTO [id=" + id + ", conversacionId=" + conversacionId + ", remitenteId=" + remitenteId
				+ ", tipoMensaje=" + tipoMensaje + ", contenido=" + contenido + ", estatusMensaje=" + estatusMensaje
				+ ", horaEnvio=" + horaEnvio + ", horaLlegada=" + horaLlegada + ", horaLeido=" + horaLeido
				+ ", adjunto=" + adjunto + "]";
	}
    
    
    
}
