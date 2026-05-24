package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;
// skipcq: JAVA-E1086
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;

import co.edu.unbosque.proyectofinal.enums.TipoConversacion;

public class ConversacionDTO {

	private Long id;
    private TipoConversacion tipoConversacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimoMensaje;
    private List<Long> participantesIds;
    private String ultimoMensaje;
    private boolean fraseSecretaConfigurada;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String fraseSecreta;
    
    
    public ConversacionDTO() {
		// TODO Auto-generated constructor stub
	}


    public ConversacionDTO(Long id, TipoConversacion tipoConversacion, LocalDateTime fechaCreacion,
    		LocalDateTime fechaUltimoMensaje, List<Long> participantesIds, String ultimoMensaje) {
    	this.id = id;
    	this.tipoConversacion = tipoConversacion;
    	this.fechaCreacion = fechaCreacion;
    	this.fechaUltimoMensaje = fechaUltimoMensaje;
    	this.participantesIds = participantesIds == null
    			? null
    			: new ArrayList<>(participantesIds);
    	this.ultimoMensaje = ultimoMensaje;
    }


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public TipoConversacion getTipoConversacion() {
		return tipoConversacion;
	}


	public void setTipoConversacion(TipoConversacion tipoConversacion) {
		this.tipoConversacion = tipoConversacion;
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


	public List<Long> getParticipantesIds() {
	    return participantesIds == null ? null : new ArrayList<>(participantesIds);
	}


	public void setParticipantesIds(List<Long> participantesIds) {
		this.participantesIds = participantesIds == null
				? null
				: new ArrayList<>(participantesIds);
	}


	public String getUltimoMensaje() {
		return ultimoMensaje;
	}


	public void setUltimoMensaje(String ultimoMensaje) {
		this.ultimoMensaje = ultimoMensaje;
	}

	public boolean isFraseSecretaConfigurada() {
		return fraseSecretaConfigurada;
	}

	public void setFraseSecretaConfigurada(boolean fraseSecretaConfigurada) {
		this.fraseSecretaConfigurada = fraseSecretaConfigurada;
	}

	public String getFraseSecreta() {
		return fraseSecreta;
	}

	public void setFraseSecreta(String fraseSecreta) {
		this.fraseSecreta = fraseSecreta;
	}


	@Override
	public int hashCode() {
		return Objects.hash(fechaCreacion, fechaUltimoMensaje, fraseSecretaConfigurada, id, participantesIds,
				tipoConversacion, ultimoMensaje);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConversacionDTO other = (ConversacionDTO) obj;
		return Objects.equals(fechaCreacion, other.fechaCreacion)
				&& Objects.equals(fechaUltimoMensaje, other.fechaUltimoMensaje)
				&& fraseSecretaConfigurada == other.fraseSecretaConfigurada && Objects.equals(id, other.id)
				&& Objects.equals(participantesIds, other.participantesIds)
				&& tipoConversacion == other.tipoConversacion && Objects.equals(ultimoMensaje, other.ultimoMensaje);
	}


	@Override
	public String toString() {
		return "ConversacionDTO [id=" + id + ", tipoConversacion=" + tipoConversacion + ", fechaCreacion="
				+ fechaCreacion + ", fechaUltimoMensaje=" + fechaUltimoMensaje + ", participantesIds="
				+ participantesIds + ", ultimoMensaje=" + ultimoMensaje + ", fraseSecretaConfigurada="
				+ fraseSecretaConfigurada + "]";
	}
    
    
	
}
