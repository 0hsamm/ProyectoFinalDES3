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

	@Override
	public int hashCode() {
		return Objects.hash(adjuntoFormato, adjuntoNombreOriginal, adjuntoTamano, adjuntoUrl, contenido, contenidoProtegido,
				conversacionId, estatusMensaje, horaEnvio, horaLeido, horaLlegada, id, remitenteId,
				remitenteNombre, remitenteUsuario, tieneAdjunto, tipoMensaje);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MensajeDTO other = (MensajeDTO) obj;
		return Objects.equals(adjuntoFormato, other.adjuntoFormato)
				&& Objects.equals(adjuntoNombreOriginal, other.adjuntoNombreOriginal)
				&& Objects.equals(adjuntoTamano, other.adjuntoTamano)
				&& Objects.equals(adjuntoUrl, other.adjuntoUrl)
				&& Objects.equals(contenido, other.contenido)
				&& Objects.equals(contenidoProtegido, other.contenidoProtegido)
				&& Objects.equals(conversacionId, other.conversacionId)
				&& estatusMensaje == other.estatusMensaje && Objects.equals(horaEnvio, other.horaEnvio)
				&& Objects.equals(horaLeido, other.horaLeido) && Objects.equals(horaLlegada, other.horaLlegada)
				&& Objects.equals(id, other.id) && Objects.equals(remitenteId, other.remitenteId)
				&& Objects.equals(remitenteNombre, other.remitenteNombre)
				&& Objects.equals(remitenteUsuario, other.remitenteUsuario)
				&& Objects.equals(tieneAdjunto, other.tieneAdjunto) && tipoMensaje == other.tipoMensaje;
	}

	@Override
	public String toString() {
		return "MensajeDTO [id=" + id + ", conversacionId=" + conversacionId + ", remitenteId=" + remitenteId
				+ ", tipoMensaje=" + tipoMensaje + ", contenido=" + contenido + ", estatusMensaje=" + estatusMensaje
				+ ", horaEnvio=" + horaEnvio + ", horaLlegada=" + horaLlegada + ", horaLeido=" + horaLeido
				+ ", tieneAdjunto=" + tieneAdjunto + ", contenidoProtegido=" + contenidoProtegido
				+ ", remitenteUsuario=" + remitenteUsuario + ", remitenteNombre=" + remitenteNombre
				+ ", adjuntoUrl=" + adjuntoUrl + ", adjuntoNombreOriginal=" + adjuntoNombreOriginal
				+ ", adjuntoFormato=" + adjuntoFormato + ", adjuntoTamano=" + adjuntoTamano + "]";
	}
    
    
    
	
}
