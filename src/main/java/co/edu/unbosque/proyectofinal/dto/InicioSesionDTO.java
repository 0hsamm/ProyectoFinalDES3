package co.edu.unbosque.proyectofinal.dto;

import java.util.Objects;

public class InicioSesionDTO {

	
	 private String usuario;
	 private String contrasena;
	
	 
	 public InicioSesionDTO() {
		// TODO Auto-generated constructor stub
	}


	 public InicioSesionDTO(String usuario, String contrasena) {
		super();
		this.usuario = usuario;
		this.contrasena = contrasena;
	 }

	 
	 



	 public String getUsuario() {
		 return usuario;
	 }


	 public void setUsuario(String usuario) {
		 this.usuario = usuario;
	 }


	 public String getContrasena() {
		 return contrasena;
	 }


	 public void setContrasena(String contrasena) {
		 this.contrasena = contrasena;
	 }


	 @Override
	 public int hashCode() {
		return Objects.hash(contrasena, usuario);
	 }


	 @Override
	 public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InicioSesionDTO other = (InicioSesionDTO) obj;
		return Objects.equals(contrasena, other.contrasena)
				&& Objects.equals(usuario, other.usuario);
	 }


	 @Override
	 public String toString() {
		return "InicioSesionDTO [ usuario=" + usuario + ", contrasena=" + contrasena + "]";
	 }


	
	 
	 
	 
}
