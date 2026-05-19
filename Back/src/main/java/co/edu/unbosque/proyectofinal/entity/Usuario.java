package co.edu.unbosque.proyectofinal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_usuarios")
public class Usuario implements UserDetails{

	private @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Column(nullable = false, unique = true, length = 60)
	private String usuario;

	@Column(nullable = false, unique = true, length = 120)
	private String correo;

	@Column(nullable = false, length = 120)
	private String nombrePersona;

	@Column(nullable = false, length = 180)
	private String contrasenaHash;

	@Column(length = 220)
	private String sobreMi;

	private LocalDate fechaNacimiento;

	@Column(nullable = false)
	private LocalDateTime fechaCreacionCuenta;

	private LocalDateTime ultimaVezEnLinea;

	@Column(nullable = false)
	private boolean enLinea;
	
	@Column(nullable = false)
	private boolean habilitado = false;

	@OneToMany(
			mappedBy = "usuario",
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			fetch = FetchType.LAZY)
	private List<InicioSesionUsuario>
	numeroSesion = new ArrayList<>();

	@OneToMany(
			mappedBy = "usuario",
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			fetch = FetchType.LAZY)
	private List<ParticipanteConversacion>
	numeroConversacion = new ArrayList<>();

	public Usuario() {
	}
	
	

	public Usuario(String usuario, String correo, String nombrePersona, String contrasenaHash, String sobreMi,
			LocalDate fechaNacimiento, LocalDateTime fechaCreacionCuenta, LocalDateTime ultimaVezEnLinea,
			boolean enLinea, boolean habilitado, List<InicioSesionUsuario> numeroSesion,
			List<ParticipanteConversacion> numeroConversacion) {
		super();
		this.usuario = usuario;
		this.correo = correo;
		this.nombrePersona = nombrePersona;
		this.contrasenaHash = contrasenaHash;
		this.sobreMi = sobreMi;
		this.fechaNacimiento = fechaNacimiento;
		this.fechaCreacionCuenta = fechaCreacionCuenta;
		this.ultimaVezEnLinea = ultimaVezEnLinea;
		this.enLinea = enLinea;
		this.habilitado = habilitado;
		this.numeroSesion = numeroSesion;
		this.numeroConversacion = numeroConversacion;
	}

	@Override
	public boolean isEnabled() {

	    return habilitado;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public String getContrasenaHash() {
		return contrasenaHash;
	}

	public void setContrasenaHash(
			String contrasenaHash) {

		this.contrasenaHash = contrasenaHash;
	}

	public String getSobreMi() {
		return sobreMi;
	}

	public void setSobreMi(String sobreMi) {
		this.sobreMi = sobreMi;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(
			LocalDate fechaNacimiento) {

		this.fechaNacimiento = fechaNacimiento;
	}

	public LocalDateTime getFechaCreacionCuenta() {
		return fechaCreacionCuenta;
	}

	public void setFechaCreacionCuenta(
			LocalDateTime fechaCreacionCuenta) {

		this.fechaCreacionCuenta =
				fechaCreacionCuenta;
	}

	public LocalDateTime getUltimaVezEnLinea() {
		return ultimaVezEnLinea;
	}

	public void setUltimaVezEnLinea(
			LocalDateTime ultimaVezEnLinea) {

		this.ultimaVezEnLinea =
				ultimaVezEnLinea;
	}

	public boolean isEnLinea() {
		return enLinea;
	}

	public void setEnLinea(boolean enLinea) {
		this.enLinea = enLinea;
	}

	public List<InicioSesionUsuario>
	getNumeroSesion() {

		return numeroSesion;
	}

	public void setNumeroSesion(
			List<InicioSesionUsuario>
			numeroSesion) {

		this.numeroSesion = numeroSesion;
	}

	public List<ParticipanteConversacion>
	getNumeroConversacion() {

		return numeroConversacion;
	}

	public void setNumeroConversacion(
			List<ParticipanteConversacion>
			numeroConversacion) {

		this.numeroConversacion =
				numeroConversacion;
	}

	public boolean isHabilitado() {
		return habilitado;
	}



	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}



	
	@Override
	public String toString() {
		return "Usuario [id=" + id + ", usuario=" + usuario + ", correo=" + correo + ", nombrePersona=" + nombrePersona
				+ ", contrasenaHash=" + contrasenaHash + ", sobreMi=" + sobreMi + ", fechaNacimiento=" + fechaNacimiento
				+ ", fechaCreacionCuenta=" + fechaCreacionCuenta + ", ultimaVezEnLinea=" + ultimaVezEnLinea
				+ ", enLinea=" + enLinea + ", habilitado=" + habilitado + ", numeroSesion=" + numeroSesion
				+ ", numeroConversacion=" + numeroConversacion + "]";
	}



	@Override
	public int hashCode() {
		return Objects.hash(contrasenaHash, correo, enLinea, fechaCreacionCuenta, fechaNacimiento, habilitado, id,
				nombrePersona, numeroConversacion, numeroSesion, sobreMi, ultimaVezEnLinea, usuario);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(contrasenaHash, other.contrasenaHash) && Objects.equals(correo, other.correo)
				&& enLinea == other.enLinea && Objects.equals(fechaCreacionCuenta, other.fechaCreacionCuenta)
				&& Objects.equals(fechaNacimiento, other.fechaNacimiento) && habilitado == other.habilitado
				&& id == other.id && Objects.equals(nombrePersona, other.nombrePersona)
				&& Objects.equals(numeroConversacion, other.numeroConversacion)
				&& Objects.equals(numeroSesion, other.numeroSesion) && Objects.equals(sobreMi, other.sobreMi)
				&& Objects.equals(ultimaVezEnLinea, other.ultimaVezEnLinea) && Objects.equals(usuario, other.usuario);
	}



	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public @Nullable String getPassword() {
		return contrasenaHash;
	}

	@Override
	public String getUsername() {
		return correo;
	}

}
