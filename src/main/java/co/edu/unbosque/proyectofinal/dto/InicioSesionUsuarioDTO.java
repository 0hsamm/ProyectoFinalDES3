package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class InicioSesionUsuarioDTO {

	private Long id;
	private String token;
    private Long usuarioId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
    
    public InicioSesionUsuarioDTO() {
		// TODO Auto-generated constructor stub
	}

	public InicioSesionUsuarioDTO(Long id, String token, Long usuarioId, LocalDateTime fechaCreacion,
			LocalDateTime fechaExpiracion) {
		super();
		this.id = id;
		this.token = token;
		this.usuarioId = usuarioId;
		this.fechaCreacion = fechaCreacion;
		this.fechaExpiracion = fechaExpiracion;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public LocalDateTime getFechaExpiracion() {
		return fechaExpiracion;
	}

	public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
		this.fechaExpiracion = fechaExpiracion;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fechaCreacion, fechaExpiracion, id, token, usuarioId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InicioSesionUsuarioDTO other = (InicioSesionUsuarioDTO) obj;
		return Objects.equals(fechaCreacion, other.fechaCreacion)
				&& Objects.equals(fechaExpiracion, other.fechaExpiracion) && Objects.equals(id, other.id)
				&& Objects.equals(token, other.token) && Objects.equals(usuarioId, other.usuarioId);
	}

	@Override
	public String toString() {
		return "InicioSesionUsuarioDTO [id=" + id + ", token=" + token + ", usuarioId=" + usuarioId + ", fechaCreacion="
				+ fechaCreacion + ", fechaExpiracion=" + fechaExpiracion + "]";
	}
    
    
	
}
