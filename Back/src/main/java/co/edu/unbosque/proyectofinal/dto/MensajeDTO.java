package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import co.edu.unbosque.proyectofinal.enums.EstatusMensaje;
import co.edu.unbosque.proyectofinal.enums.TipoMensaje;

public class MensajeDTO {

	private Long id;
	private Long conversacionId;
	private Long remitenteId;
	private TipoMensaje tipoMensaje;
	private String contenido;
	private EstatusMensaje estatusMensaje;
	private LocalDateTime horaEnvio;
    private LocalDateTime horaLlegada;
    private LocalDateTime horaLeido;
    private Boolean tieneAdjunto = false;
    private Boolean contenidoProtegido = false;
    private String remitenteUsuario;
    private String remitenteNombre;
    private String adjuntoUrl;
    private String adjuntoNombreOriginal;
    private String adjuntoFormato;
    private Long adjuntoTamano;
    private Boolean fijado = false;
    private LocalDateTime fechaFijado;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String fraseSecreta;
    
    public MensajeDTO() {
		// TODO Auto-generated constructor stub
	}

	

	public MensajeDTO(Long id, Long conversacionId, Long remitenteId, TipoMensaje tipoMensaje, String contenido,
			EstatusMensaje estatusMensaje, LocalDateTime horaEnvio, LocalDateTime horaLlegada, LocalDateTime horaLeido,
			Boolean tieneAdjunto) {
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
		this.tieneAdjunto = tieneAdjunto;
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

	public boolean isTieneAdjunto() {
		return Boolean.TRUE.equals(tieneAdjunto);
	}

	public void setTieneAdjunto(Boolean tieneAdjunto) {
		this.tieneAdjunto = tieneAdjunto;
	}

	public boolean isContenidoProtegido() {
		return Boolean.TRUE.equals(contenidoProtegido);
	}

	public void setContenidoProtegido(Boolean contenidoProtegido) {
		this.contenidoProtegido = contenidoProtegido;
	}

	public String getFraseSecreta() {
		return fraseSecreta;
	}

	public void setFraseSecreta(String fraseSecreta) {
		this.fraseSecreta = fraseSecreta;
	}

	public String getRemitenteUsuario() {
		return remitenteUsuario;
	}

	public void setRemitenteUsuario(String remitenteUsuario) {
		this.remitenteUsuario = remitenteUsuario;
	}

	public String getRemitenteNombre() {
		return remitenteNombre;
	}

	public void setRemitenteNombre(String remitenteNombre) {
		this.remitenteNombre = remitenteNombre;
	}

	public String getAdjuntoUrl() {
		return adjuntoUrl;
	}

	public void setAdjuntoUrl(String adjuntoUrl) {
		this.adjuntoUrl = adjuntoUrl;
	}

	public String getAdjuntoNombreOriginal() {
		return adjuntoNombreOriginal;
	}

	public void setAdjuntoNombreOriginal(String adjuntoNombreOriginal) {
		this.adjuntoNombreOriginal = adjuntoNombreOriginal;
	}

	public String getAdjuntoFormato() {
		return adjuntoFormato;
	}

	public void setAdjuntoFormato(String adjuntoFormato) {
		this.adjuntoFormato = adjuntoFormato;
	}

	public Long getAdjuntoTamano() {
		return adjuntoTamano;
	}

	public void setAdjuntoTamano(Long adjuntoTamano) {
		this.adjuntoTamano = adjuntoTamano;
	}

	public boolean isFijado() {
		return Boolean.TRUE.equals(fijado);
	}

	public void setFijado(Boolean fijado) {
		this.fijado = fijado;
	}

	public LocalDateTime getFechaFijado() {
		return fechaFijado;
	}

	public void setFechaFijado(LocalDateTime fechaFijado) {
		this.fechaFijado = fechaFijado;
	}

	@Override
	public int hashCode() {
		return Objects.hash(adjuntoFormato, adjuntoNombreOriginal, adjuntoTamano, adjuntoUrl, contenido, contenidoProtegido,
				conversacionId, estatusMensaje, fechaFijado, fijado, horaEnvio, horaLeido, horaLlegada, id, remitenteId,
				remitenteNombre, remitenteUsuario, tieneAdjunto, tipoMensaje);
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (!(obj instanceof MensajeDTO)) return false;
	    MensajeDTO other = (MensajeDTO) obj;
	    return Objects.equals(id, other.id)
	        && Objects.equals(conversacionId, other.conversacionId)
	        && Objects.equals(remitenteId, other.remitenteId)
	        && Objects.equals(fijado, other.fijado)
	        && Objects.equals(fechaFijado, other.fechaFijado)
	        && tipoMensaje == other.tipoMensaje
	        && estatusMensaje == other.estatusMensaje
	        && Objects.equals(contenido, other.contenido)
	        && Objects.equals(horaEnvio, other.horaEnvio)
	        && Objects.equals(tieneAdjunto, other.tieneAdjunto)
	        && Objects.equals(adjuntoUrl, other.adjuntoUrl);
	}

	@Override
	public String toString() {
		return "MensajeDTO [id=" + id + ", conversacionId=" + conversacionId + ", remitenteId=" + remitenteId
				+ ", tipoMensaje=" + tipoMensaje + ", contenido=" + contenido + ", estatusMensaje=" + estatusMensaje
				+ ", horaEnvio=" + horaEnvio + ", horaLlegada=" + horaLlegada + ", horaLeido=" + horaLeido
				+ ", tieneAdjunto=" + tieneAdjunto + ", contenidoProtegido=" + contenidoProtegido
				+ ", remitenteUsuario=" + remitenteUsuario + ", remitenteNombre=" + remitenteNombre
				+ ", adjuntoUrl=" + adjuntoUrl + ", adjuntoNombreOriginal=" + adjuntoNombreOriginal
				+ ", adjuntoFormato=" + adjuntoFormato + ", adjuntoTamano=" + adjuntoTamano
				+ ", fijado=" + fijado + ", fechaFijado=" + fechaFijado + "]";
	}
    
    
    
	
}
