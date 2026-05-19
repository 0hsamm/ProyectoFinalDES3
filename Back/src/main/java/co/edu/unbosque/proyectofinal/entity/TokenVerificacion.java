package co.edu.unbosque.proyectofinal.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class TokenVerificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private LocalDateTime fechaExpiracion;

    public TokenVerificacion() {
	// TODO Auto-generated constructor stub
    }

	public TokenVerificacion(String token, Usuario usuario, LocalDateTime fechaExpiracion) {
		super();
		this.token = token;
		this.usuario = usuario;
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

	public LocalDateTime getFechaExpiracion() {
		return fechaExpiracion;
	}

	public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
		this.fechaExpiracion = fechaExpiracion;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fechaExpiracion, id, token, usuario);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TokenVerificacion other = (TokenVerificacion) obj;
		return Objects.equals(fechaExpiracion, other.fechaExpiracion) && id == other.id
				&& Objects.equals(token, other.token) && Objects.equals(usuario, other.usuario);
	}

	@Override
	public String toString() {
		return "TokenVerificacion [id=" + id + ", token=" + token + ", usuario=" + usuario + ", fechaExpiracion="
				+ fechaExpiracion + "]";
	}

    

}