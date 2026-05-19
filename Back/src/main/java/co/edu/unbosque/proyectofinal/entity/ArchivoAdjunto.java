package co.edu.unbosque.proyectofinal.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "archivo_adjunto")
public class ArchivoAdjunto {

	
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_mensaje", nullable = false, unique = true)
	private Mensaje mensaje;

	@Column(nullable = false, length = 255)
	private String rutaAlmacenamiento;

	@Column(nullable = false, length = 120)
	private String nombreOriginalArchivo; //El nombre que tenia el archivo al subirlo

	@Column(nullable = false, length = 120)
	private String formatoArchivo; //si el archivo es jpg, png, etc

	@Column(nullable = false)
	private long tamanoArchivo;

	@Column(nullable = false, length = 120)
	private String vi; //El cifrado del archivo
	
	@Column(nullable = false, length = 500)
	private String url;

	@Column(nullable = false, length = 500)
	private String publicId;
	
	
	public ArchivoAdjunto() {
		// TODO Auto-generated constructor stub
	}


	


	public ArchivoAdjunto(Mensaje mensaje, String rutaAlmacenamiento, String nombreOriginalArchivo,
			String formatoArchivo, long tamanoArchivo, String vi, String url, String publicId) {
		super();
		this.mensaje = mensaje;
		this.rutaAlmacenamiento = rutaAlmacenamiento;
		this.nombreOriginalArchivo = nombreOriginalArchivo;
		this.formatoArchivo = formatoArchivo;
		this.tamanoArchivo = tamanoArchivo;
		this.vi = vi;
		this.url = url;
		this.publicId = publicId;
	}





	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public Mensaje getMensaje() {
		return mensaje;
	}


	public void setMensaje(Mensaje mensaje) {
		this.mensaje = mensaje;
	}


	public String getRutaAlmacenamiento() {
		return rutaAlmacenamiento;
	}


	public void setRutaAlmacenamiento(String rutaAlmacenamiento) {
		this.rutaAlmacenamiento = rutaAlmacenamiento;
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


	public long getTamanoArchivo() {
		return tamanoArchivo;
	}


	public void setTamanoArchivo(long tamanoArchivo) {
		this.tamanoArchivo = tamanoArchivo;
	}


	public String getVi() {
		return vi;
	}


	public void setVi(String vi) {
		this.vi = vi;
	}


	public String getUrl() {
		return url;
	}





	public void setUrl(String url) {
		this.url = url;
	}





	public String getPublicId() {
		return publicId;
	}





	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}





	@Override
	public int hashCode() {
		return Objects.hash(formatoArchivo, id, mensaje, nombreOriginalArchivo, publicId, rutaAlmacenamiento,
				tamanoArchivo, url, vi);
	}





	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArchivoAdjunto other = (ArchivoAdjunto) obj;
		return Objects.equals(formatoArchivo, other.formatoArchivo) && id == other.id
				&& Objects.equals(mensaje, other.mensaje)
				&& Objects.equals(nombreOriginalArchivo, other.nombreOriginalArchivo)
				&& Objects.equals(publicId, other.publicId)
				&& Objects.equals(rutaAlmacenamiento, other.rutaAlmacenamiento) && tamanoArchivo == other.tamanoArchivo
				&& Objects.equals(url, other.url) && Objects.equals(vi, other.vi);
	}





	@Override
	public String toString() {
		return "ArchivoAdjunto [id=" + id + ", mensaje=" + mensaje + ", rutaAlmacenamiento=" + rutaAlmacenamiento
				+ ", nombreOriginalArchivo=" + nombreOriginalArchivo + ", formatoArchivo=" + formatoArchivo
				+ ", tamanoArchivo=" + tamanoArchivo + ", vi=" + vi + ", url=" + url + ", publicId=" + publicId + "]";
	}





	
	
	
	
}
