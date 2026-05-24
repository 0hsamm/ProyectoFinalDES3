package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class RegistroAuditoriaDTO {

    private Long id;
    private String usuarioCorreo;
    private String usuarioNombre;
    private String accion;
    private String modulo;
    private String descripcion;
    private LocalDateTime fechaAccion;
    private String ip;
    private String navegador;
    private Double latitud;
    private Double longitud;
    private String ubicacion;
    private Long conversacionId;
    private boolean exitoso;

    public RegistroAuditoriaDTO() {
    }

	public RegistroAuditoriaDTO(String usuarioCorreo, String usuarioNombre, String accion, String modulo,
			String descripcion, LocalDateTime fechaAccion, String ip, String navegador, Double latitud, Double longitud,
			String ubicacion, Long conversacionId, boolean exitoso) {
		super();
		this.usuarioCorreo = usuarioCorreo;
		this.usuarioNombre = usuarioNombre;
		this.accion = accion;
		this.modulo = modulo;
		this.descripcion = descripcion;
		this.fechaAccion = fechaAccion;
		this.ip = ip;
		this.navegador = navegador;
		this.latitud = latitud;
		this.longitud = longitud;
		this.ubicacion = ubicacion;
		this.conversacionId = conversacionId;
		this.exitoso = exitoso;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsuarioCorreo() {
		return usuarioCorreo;
	}

	public void setUsuarioCorreo(String usuarioCorreo) {
		this.usuarioCorreo = usuarioCorreo;
	}

	public String getUsuarioNombre() {
		return usuarioNombre;
	}

	public void setUsuarioNombre(String usuarioNombre) {
		this.usuarioNombre = usuarioNombre;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public LocalDateTime getFechaAccion() {
		return fechaAccion;
	}

	public void setFechaAccion(LocalDateTime fechaAccion) {
		this.fechaAccion = fechaAccion;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getNavegador() {
		return navegador;
	}

	public void setNavegador(String navegador) {
		this.navegador = navegador;
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

	public Long getConversacionId() {
		return conversacionId;
	}

	public void setConversacionId(Long conversacionId) {
		this.conversacionId = conversacionId;
	}

	public boolean isExitoso() {
		return exitoso;
	}

	public void setExitoso(boolean exitoso) {
		this.exitoso = exitoso;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accion, conversacionId, descripcion, exitoso, fechaAccion, id, ip, latitud, longitud,
				modulo, navegador, ubicacion, usuarioCorreo, usuarioNombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof RegistroAuditoriaDTO other)) {
			return false;
		}

		return Objects.equals(
				java.util.List.of(
						accion,
						conversacionId,
						descripcion,
						exitoso,
						fechaAccion,
						id,
						ip,
						latitud,
						longitud,
						modulo,
						navegador,
						ubicacion,
						usuarioCorreo,
						usuarioNombre
				),
				java.util.List.of(
						other.accion,
						other.conversacionId,
						other.descripcion,
						other.exitoso,
						other.fechaAccion,
						other.id,
						other.ip,
						other.latitud,
						other.longitud,
						other.modulo,
						other.navegador,
						other.ubicacion,
						other.usuarioCorreo,
						other.usuarioNombre
				)
		);
	}

	@Override
	public String toString() {
		return "RegistroAuditoriaDTO [id=" + id + ", usuarioCorreo=" + usuarioCorreo + ", usuarioNombre="
				+ usuarioNombre + ", accion=" + accion + ", modulo=" + modulo + ", descripcion=" + descripcion
				+ ", fechaAccion=" + fechaAccion + ", ip=" + ip + ", navegador=" + navegador + ", latitud=" + latitud
				+ ", longitud=" + longitud + ", ubicacion=" + ubicacion + ", conversacionId=" + conversacionId
				+ ", exitoso=" + exitoso + "]";
	}

   
}