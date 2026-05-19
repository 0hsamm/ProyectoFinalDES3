package co.edu.unbosque.proyectofinal.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * Datos enviados desde Angular al backend para solicitar una respuesta de IA.
 * La API key de Gemini nunca viaja en este DTO.
 */
public class SolicitudGeminiDTO {

    @JsonAlias("message")
    private String mensaje;

    @JsonAlias("conversationId")
    private Long conversacionId;

    @JsonAlias("context")
    private String contexto;

    @JsonAlias("targetLanguage")
    private String idiomaDestino;

    @JsonAlias("latitude")
    private Double latitud;

    @JsonAlias("longitude")
    private Double longitud;

    @JsonAlias("location")
    private String ubicacion;

    public SolicitudGeminiDTO() {
    }

    public SolicitudGeminiDTO(
            String mensaje,
            Long conversacionId,
            String contexto,
            String idiomaDestino,
            Double latitud,
            Double longitud,
            String ubicacion) {

        this.mensaje = mensaje;
        this.conversacionId = conversacionId;
        this.contexto = contexto;
        this.idiomaDestino = idiomaDestino;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ubicacion = ubicacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Long getConversacionId() {
        return conversacionId;
    }

    public void setConversacionId(Long conversacionId) {
        this.conversacionId = conversacionId;
    }

    public String getContexto() {
        return contexto;
    }

    public void setContexto(String contexto) {
        this.contexto = contexto;
    }

    public String getIdiomaDestino() {
        return idiomaDestino;
    }

    public void setIdiomaDestino(String idiomaDestino) {
        this.idiomaDestino = idiomaDestino;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                contexto,
                conversacionId,
                idiomaDestino,
                latitud,
                longitud,
                mensaje,
                ubicacion);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        SolicitudGeminiDTO other =
                (SolicitudGeminiDTO) obj;

        return Objects.equals(contexto, other.contexto)
                && Objects.equals(conversacionId, other.conversacionId)
                && Objects.equals(idiomaDestino, other.idiomaDestino)
                && Objects.equals(latitud, other.latitud)
                && Objects.equals(longitud, other.longitud)
                && Objects.equals(mensaje, other.mensaje)
                && Objects.equals(ubicacion, other.ubicacion);
    }
}
