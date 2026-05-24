package co.edu.unbosque.proyectofinal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.edu.unbosque.proyectofinal.enums.TipoEstado;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "estados")
public class Estado {

	
	 @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

	 @Column(length = 500)
	    private String texto;

	    /*
	     * URL del archivo multimedia
	     * (imagen o video)
	     */
	    private String mediaUrl;

	    /*
	     * URL de miniatura/preview
	     * usada para carga rápida
	     */
	    private String thumbnailUrl;

	    /*
	     * Tipo MIME del archivo
	     * Ej:
	     * image/png
	     * image/jpeg
	     * video/mp4
	     */
	    private String mimeType;

	    /*
	     * Tipo del estado
	     * TEXTO
	     * IMAGEN
	     * VIDEO
	     */
	    @Enumerated(EnumType.STRING)
	    private TipoEstado tipo;

	    /*
	     * Fecha de creación
	     */
	    private LocalDateTime fechaCreacion;

	    /*
	     * Fecha de expiración
	     * normalmente +24 horas
	     */
	    private LocalDateTime fechaExpiracion;

	    /*
	     * Usuario que publicó el estado
	     */
	    @ManyToOne
	    @JoinColumn(name = "usuario_id")
	    private Usuario usuario;

	    /*
	     * Personas que vieron el estado
	     */
	    @OneToMany(
	        mappedBy = "estado",
	        cascade = CascadeType.ALL,
	        orphanRemoval = true
	    )
	    private List<EstadoVisto> vistas =
	            new ArrayList<>();
	
	
	    
	    public Estado() {
			// TODO Auto-generated constructor stub
		}



		



		public Estado(String texto, String mediaUrl, String thumbnailUrl, String mimeType, TipoEstado tipo,
				LocalDateTime fechaCreacion, LocalDateTime fechaExpiracion, Usuario usuario, List<EstadoVisto> vistas) {
			super();
			this.texto = texto;
			this.mediaUrl = mediaUrl;
			this.thumbnailUrl = thumbnailUrl;
			this.mimeType = mimeType;
			this.tipo = tipo;
			this.fechaCreacion = fechaCreacion;
			this.fechaExpiracion = fechaExpiracion;
			this.usuario = usuario;
			this.vistas = vistas;
		}







		public long getId() {
			return id;
		}



		public void setId(long id) {
			this.id = id;
		}



		



		public String getTexto() {
			return texto;
		}



		public void setTexto(String texto) {
			this.texto = texto;
		}



		public String getMediaUrl() {
			return mediaUrl;
		}



		public void setMediaUrl(String mediaUrl) {
			this.mediaUrl = mediaUrl;
		}



		public TipoEstado getTipo() {
			return tipo;
		}



		public void setTipo(TipoEstado tipo) {
			this.tipo = tipo;
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



		public Usuario getUsuario() {
			return usuario;
		}



		public void setUsuario(Usuario usuario) {
			this.usuario = usuario;
		}







		public String getThumbnailUrl() {
			return thumbnailUrl;
		}







		public void setThumbnailUrl(String thumbnailUrl) {
			this.thumbnailUrl = thumbnailUrl;
		}







		public String getMimeType() {
			return mimeType;
		}







		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}







		public List<EstadoVisto> getVistas() {
			return vistas;
		}







		public void setVistas(List<EstadoVisto> vistas) {
			this.vistas = vistas == null
					? new ArrayList<>()
					: new ArrayList<>(vistas);
		}







		@Override
		public int hashCode() {
			return Objects.hash(fechaCreacion, fechaExpiracion, id, mediaUrl, mimeType, texto, thumbnailUrl, tipo,
					usuario, vistas);
		}







		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Estado other = (Estado) obj;
			return Objects.equals(fechaCreacion, other.fechaCreacion)
					&& Objects.equals(fechaExpiracion, other.fechaExpiracion) && id == other.id
					&& Objects.equals(mediaUrl, other.mediaUrl) && Objects.equals(mimeType, other.mimeType)
					&& Objects.equals(texto, other.texto) && Objects.equals(thumbnailUrl, other.thumbnailUrl)
					&& tipo == other.tipo && Objects.equals(usuario, other.usuario)
					&& Objects.equals(vistas, other.vistas);
		}







		@Override
		public String toString() {
			return "Estado [id=" + id + ", texto=" + texto + ", mediaUrl=" + mediaUrl + ", thumbnailUrl=" + thumbnailUrl
					+ ", mimeType=" + mimeType + ", tipo=" + tipo + ", fechaCreacion=" + fechaCreacion
					+ ", fechaExpiracion=" + fechaExpiracion + ", usuario=" + usuario + ", vistas=" + vistas + "]";
		}



		
	    
	    
}
