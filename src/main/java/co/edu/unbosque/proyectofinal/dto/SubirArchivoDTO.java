package co.edu.unbosque.proyectofinal.dto;

import java.util.Objects;

public class SubirArchivoDTO {

	private Long mensajeId;
    private String nombreOriginalArchivo;
    private String formatoArchivo;
    
    public SubirArchivoDTO() {
		// TODO Auto-generated constructor stub
	}

	public SubirArchivoDTO(Long mensajeId, String nombreOriginalArchivo, String formatoArchivo) {
		super();
		this.mensajeId = mensajeId;
		this.nombreOriginalArchivo = nombreOriginalArchivo;
		this.formatoArchivo = formatoArchivo;
	}

	public Long getMensajeId() {
		return mensajeId;
	}

	public void setMensajeId(Long mensajeId) {
		this.mensajeId = mensajeId;
	}

	public String getNombreOriginalArchivo() {
		return nombreOriginalArchivo;
	}

	public void setNombreOriginalArchivo(String nombreOriginalArchivo) {
		this.nombreOriginalArchivo = nombreOriginalArchivo;
	}

	public String getFormatoArchivo() {
		return formatoArchivo;
	}

	public void setFormatoArchivo(String formatoArchivo) {
		this.formatoArchivo = formatoArchivo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(formatoArchivo, mensajeId, nombreOriginalArchivo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubirArchivoDTO other = (SubirArchivoDTO) obj;
		return Objects.equals(formatoArchivo, other.formatoArchivo) && Objects.equals(mensajeId, other.mensajeId)
				&& Objects.equals(nombreOriginalArchivo, other.nombreOriginalArchivo);
	}

	@Override
	public String toString() {
		return "SubirArchivoDTO [mensajeId=" + mensajeId + ", nombreOriginalArchivo=" + nombreOriginalArchivo
				+ ", formatoArchivo=" + formatoArchivo + "]";
	}
    
    
	
}
