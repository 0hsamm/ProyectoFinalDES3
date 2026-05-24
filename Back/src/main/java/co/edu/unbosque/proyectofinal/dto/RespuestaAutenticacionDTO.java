package co.edu.unbosque.proyectofinal.dto;

/**
 * Respuesta segura del login.
 * Incluye el JWT que Angular debe enviar como Bearer token.
 */
public class RespuestaAutenticacionDTO {

    private String token;

    private String tipoToken;

    private Long id;

    private String usuario;

    private String correo;

    private String nombrePersona;

    private String fotoPerfil;

    public RespuestaAutenticacionDTO() {
    }

    public RespuestaAutenticacionDTO(
            String token,
            String tipoToken,
            Long id,
            String usuario,
            String correo,
            String nombrePersona,
            String fotoPerfil) {

        this.token = token;
        this.tipoToken = tipoToken;
        this.id = id;
        this.usuario = usuario;
        this.correo = correo;
        this.nombrePersona = nombrePersona;
        this.fotoPerfil = fotoPerfil;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(String tipoToken) {
        this.tipoToken = tipoToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}
