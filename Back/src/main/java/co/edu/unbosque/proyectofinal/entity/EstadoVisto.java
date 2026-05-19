package co.edu.unbosque.proyectofinal.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "estado_visto")
public class EstadoVisto {

	 @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

	    // Estado que fue visto
	    @ManyToOne
	    @JoinColumn(name = "estado_id", nullable = false)
	    private Estado estado;

	    // Usuario que vio el estado
	    @ManyToOne
	    @JoinColumn(name = "usuario_id", nullable = false)
	    private Usuario usuario;

	    // Momento exacto de visualización
	    private LocalDateTime fechaVista;
	
	    public EstadoVisto() {
			// TODO Auto-generated constructor stub
		}

		public EstadoVisto(Estado estado, Usuario usuario, LocalDateTime fechaVista) {
			super();
			this.estado = estado;
			this.usuario = usuario;
			this.fechaVista = fechaVista;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public Estado getEstado() {
			return estado;
		}

		public void setEstado(Estado estado) {
			this.estado = estado;
		}

		public Usuario getUsuario() {
			return usuario;
		}

		public void setUsuario(Usuario usuario) {
			this.usuario = usuario;
		}

		public LocalDateTime getFechaVista() {
			return fechaVista;
		}

		public void setFechaVista(LocalDateTime fechaVista) {
			this.fechaVista = fechaVista;
		}

		@Override
		public int hashCode() {
			return Objects.hash(estado, fechaVista, id, usuario);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EstadoVisto other = (EstadoVisto) obj;
			return Objects.equals(estado, other.estado) && Objects.equals(fechaVista, other.fechaVista)
					&& id == other.id && Objects.equals(usuario, other.usuario);
		}

		@Override
		public String toString() {
			return "EstadoVisto [id=" + id + ", estado=" + estado + ", usuario=" + usuario + ", fechaVista="
					+ fechaVista + "]";
		}
	    
	    
	
}

