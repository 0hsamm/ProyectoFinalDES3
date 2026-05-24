package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.RolUsuario;

public class UsuarioDTO {

	private Long id;

	private String usuario;

	private String correo;

	private String nombrePersona;

	private String sobreMi;

	private String fotoPerfil;

	private boolean enLinea;

	private Boolean mostrarEnLinea;

	private LocalDate fechaNacimiento;

	private LocalDateTime ultimaVezEnLinea;
	
	private String contrasena;
	
	private RolUsuario rol;

	public UsuarioDTO() {
	}

	public UsuarioDTO(Long id, String usuario, String correo, String nombrePersona, String sobreMi, String fotoPerfil,
			boolean enLinea, LocalDate fechaNacimiento, LocalDateTime ultimaVezEnLinea, String contrasena,
			RolUsuario rol) {
		super();
		this.id = id;
		this.usuario = usuario;
		this.correo = correo;
		this.nombrePersona = nombrePersona;
		this.sobreMi = sobreMi;
		this.fotoPerfil = fotoPerfil;
		this.enLinea = enLinea;
		this.fechaNacimiento = fechaNacimiento;
		this.ultimaVezEnLinea = ultimaVezEnLinea;
		this.contrasena = contrasena;
		this.rol = rol;
	}

	public RolUsuario getRol() {
	    return rol;
	}

	public void setRol(RolUsuario rol) {
	    this.rol = rol;
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

	public String getSobreMi() {
		return sobreMi;
	}

	public void setSobreMi(String sobreMi) {
		this.sobreMi = sobreMi;
	}

	public String getFotoPerfil() {
		return fotoPerfil;
	}

	public void setFotoPerfil(String fotoPerfil) {
		this.fotoPerfil = fotoPerfil;
	}

	public boolean isEnLinea() {
		return enLinea;
	}

	public void setEnLinea(boolean enLinea) {
		this.enLinea = enLinea;
	}

	public Boolean getMostrarEnLinea() {
		return mostrarEnLinea;
	}

	public void setMostrarEnLinea(Boolean mostrarEnLinea) {
		this.mostrarEnLinea = mostrarEnLinea;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(
			LocalDate fechaNacimiento) {

		this.fechaNacimiento = fechaNacimiento;
	}

	public LocalDateTime getUltimaVezEnLinea() {
		return ultimaVezEnLinea;
	}

	public void setUltimaVezEnLinea(
			LocalDateTime ultimaVezEnLinea) {

		this.ultimaVezEnLinea =
				ultimaVezEnLinea;
	}
	
	
	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contrasena, correo, enLinea, fechaNacimiento, fotoPerfil, id, mostrarEnLinea, nombrePersona, rol, sobreMi,
				ultimaVezEnLinea, usuario);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuarioDTO other = (UsuarioDTO) obj;
		return Objects.equals(contrasena, other.contrasena) && Objects.equals(correo, other.correo)
				&& enLinea == other.enLinea && Objects.equals(fechaNacimiento, other.fechaNacimiento)
				&& Objects.equals(fotoPerfil, other.fotoPerfil) && Objects.equals(id, other.id)
				&& Objects.equals(mostrarEnLinea, other.mostrarEnLinea)
				&& Objects.equals(nombrePersona, other.nombrePersona) && rol == other.rol
				&& Objects.equals(sobreMi, other.sobreMi)
				&& Objects.equals(ultimaVezEnLinea, other.ultimaVezEnLinea) && Objects.equals(usuario, other.usuario);
	}

	@Override
	public String toString() {
		return "UsuarioDTO [id=" + id + ", usuario=" + usuario + ", correo=" + correo + ", nombrePersona="
				+ nombrePersona + ", sobreMi=" + sobreMi + ", fotoPerfil=" + fotoPerfil + ", enLinea=" + enLinea
				+ ", mostrarEnLinea=" + mostrarEnLinea
				+ ", fechaNacimiento=" + fechaNacimiento + ", ultimaVezEnLinea=" + ultimaVezEnLinea
				+ ", contrasena=" + contrasena + ", rol=" + rol + "]";
	}


	

}
