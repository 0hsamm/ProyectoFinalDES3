package co.edu.unbosque.proyectofinal.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class UsuarioDTO {

	private Long id;
    private String usuario;
    private String nombrePersona;
    private String sobreMi;
    private boolean enLinea;
    private LocalDateTime ultimaVezEnLinea;
    
    public UsuarioDTO() {
		// TODO Auto-generated constructor stub
	}

	public UsuarioDTO(Long id, String usuario, String nombrePersona, String sobreMi, boolean enLinea,
			LocalDateTime ultimaVezEnLinea) {
		super();
		this.id = id;
		this.usuario = usuario;
		this.nombrePersona = nombrePersona;
		this.sobreMi = sobreMi;
		this.enLinea = enLinea;
		this.ultimaVezEnLinea = ultimaVezEnLinea;
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

	public boolean isEnLinea() {
		return enLinea;
	}

	public void setEnLinea(boolean enLinea) {
		this.enLinea = enLinea;
	}

	public LocalDateTime getUltimaVezEnLinea() {
		return ultimaVezEnLinea;
	}

	public void setUltimaVezEnLinea(LocalDateTime ultimaVezEnLinea) {
		this.ultimaVezEnLinea = ultimaVezEnLinea;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nombrePersona, enLinea, sobreMi, ultimaVezEnLinea, usuario);
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
		return Objects.equals(id, other.id) && Objects.equals(nombrePersona, other.nombrePersona)
				&& enLinea == other.enLinea && Objects.equals(sobreMi, other.sobreMi)
				&& Objects.equals(ultimaVezEnLinea, other.ultimaVezEnLinea) && Objects.equals(usuario, other.usuario);
	}

	@Override
	public String toString() {
		return "UsuarioDTO [id=" + id + ", usuario=" + usuario + ", nombrePersona=" + nombrePersona + ", sobreMi="
				+ sobreMi + ", online=" + enLinea + ", ultimaVezEnLinea=" + ultimaVezEnLinea + "]";
	}
    
    
	
}
