package co.edu.unbosque.proyectofinal.entity;


import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sesion_usuarios")
public class InicioSesionUsuario {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
	
	@Column(nullable = false, unique = true, length = 120)
	private String token;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario usuario;
	
	@Column(nullable = false)
	private LocalDateTime fechaCreacion;

	@Column(nullable = false)
	private LocalDateTime fechaExpiracion; //Cuando se cierra la sesion
	
	
	public InicioSesionUsuario() {
		// TODO Auto-generated constructor stub
	}


	


	public InicioSesionUsuario(String token, Usuario usuario, LocalDateTime fechaCreacion,
			LocalDateTime fechaExpiracion) {
		super();
		this.token = token;
		this.usuario = usuario;
		this.fechaCreacion = fechaCreacion;
		this.fechaExpiracion = fechaExpiracion;
	}





	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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
		return Objects.hash(fechaCreacion, fechaExpiracion, id, token, usuario);
	}





	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InicioSesionUsuario other = (InicioSesionUsuario) obj;
		return Objects.equals(fechaCreacion, other.fechaCreacion)
				&& Objects.equals(fechaExpiracion, other.fechaExpiracion) && Objects.equals(id, other.id)
				&& Objects.equals(token, other.token) && Objects.equals(usuario, other.usuario);
	}





	@Override
	public String toString() {
		return "InicioSesionUsuario [id=" + id + ", token=" + token + ", usuario=" + usuario + ", fechaCreacion="
				+ fechaCreacion + ", fechaExpiracion=" + fechaExpiracion + "]";
	}
	
	
	
	
	
}
