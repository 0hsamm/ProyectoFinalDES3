package co.edu.unbosque.proyectofinal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class Usuario {
	
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
	
	@Column(nullable = false, unique = true, length = 60)
	private String usuario;
	
	@Column(nullable = false, length = 120)
	private String nombrePersona;

	@Column(nullable = false, length = 180)
	private String contrasenaHash;

	@Column(length = 220)
	private String sobreMi;

	@Column(nullable = false)
	private LocalDateTime fechaCreacionCuenta;

	private LocalDateTime ultimaVezEnLinea;

	@Column(nullable = false)
	private boolean enLinea;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<InicioSesionUsuario> numeroSesion = new ArrayList<>(); //Cantidad de sesiones que tiene el usuario

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ParticipanteConversacion> numeroConversacion = new ArrayList<>(); //Cantidad de conversaciones que tiene el usuario
	
	
	public Usuario() {
		// TODO Auto-generated constructor stub
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


	public String getNombrePersona() {
		return nombrePersona;
	}


	public void setNombrePersona(String nombrePersona) {
		this.nombrePersona = nombrePersona;
	}


	public String getContrasenaHash() {
		return contrasenaHash;
	}


	public void setContrasenaHash(String contrasenaHash) {
		this.contrasenaHash = contrasenaHash;
	}


	public String getSobreMi() {
		return sobreMi;
	}


	public void setSobreMi(String sobreMi) {
		this.sobreMi = sobreMi;
	}


	public LocalDateTime getFechaCreacionCuenta() {
		return fechaCreacionCuenta;
	}


	public void setFechaCreacionCuenta(LocalDateTime fechaCreacionCuenta) {
		this.fechaCreacionCuenta = fechaCreacionCuenta;
	}


	public LocalDateTime getUltimaVezEnLinea() {
		return ultimaVezEnLinea;
	}


	public void setUltimaVezEnLinea(LocalDateTime ultimaVezEnLinea) {
		this.ultimaVezEnLinea = ultimaVezEnLinea;
	}


	public boolean isOnline() {
		return enLinea;
	}


	public void setOnline(boolean online) {
		this.enLinea = online;
	}







	@Override
	public int hashCode() {
		return Objects.hash(contrasenaHash, fechaCreacionCuenta, id, nombrePersona, numeroConversacion, numeroSesion,
				enLinea, sobreMi, ultimaVezEnLinea, usuario);
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
		return Objects.equals(contrasenaHash, other.contrasenaHash)
				&& Objects.equals(fechaCreacionCuenta, other.fechaCreacionCuenta) && id == other.id
				&& Objects.equals(nombrePersona, other.nombrePersona)
				&& Objects.equals(numeroConversacion, other.numeroConversacion)
				&& Objects.equals(numeroSesion, other.numeroSesion) && enLinea == other.enLinea
				&& Objects.equals(sobreMi, other.sobreMi) && Objects.equals(ultimaVezEnLinea, other.ultimaVezEnLinea)
				&& Objects.equals(usuario, other.usuario);
	}




	@Override
	public String toString() {
		return "Usuario [id=" + id + ", usuario=" + usuario + ", nombrePersona=" + nombrePersona + ", contrasenaHash="
				+ contrasenaHash + ", sobreMi=" + sobreMi + ", fechaCreacionCuenta=" + fechaCreacionCuenta
				+ ", ultimaVezEnLinea=" + ultimaVezEnLinea + ", online=" + enLinea + ", sessions=" + numeroSesion
				+ ", conversations=" + numeroConversacion + "]";
	}
	
	
	
	
}
