package co.edu.unbosque.proyectofinal.dto;

import java.util.Objects;

public class CrearUsuarioDTO {

	
	private String usuario;
    private String nombrePersona;
    private String contrasena;
    private String sobreMi;
	
    public CrearUsuarioDTO() {
		// TODO Auto-generated constructor stub
	}

	public CrearUsuarioDTO(String usuario, String nombrePersona, String contrasena, String sobreMi) {
		super();
		this.usuario = usuario;
		this.nombrePersona = nombrePersona;
		this.contrasena = contrasena;
		this.sobreMi = sobreMi;
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

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getSobreMi() {
		return sobreMi;
	}

	public void setSobreMi(String sobreMi) {
		this.sobreMi = sobreMi;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contrasena, nombrePersona, sobreMi, usuario);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CrearUsuarioDTO other = (CrearUsuarioDTO) obj;
		return Objects.equals(contrasena, other.contrasena)
				&& Objects.equals(nombrePersona, other.nombrePersona) && Objects.equals(sobreMi, other.sobreMi)
				&& Objects.equals(usuario, other.usuario);
	}

	@Override
	public String toString() {
		return "CrearUsuarioDTO [ usuario=" + usuario + ", nombrePersona=" + nombrePersona
				+ ", contrasena=" + contrasena + ", sobreMi=" + sobreMi + "]";
	}

	

    
    
	
    
}
