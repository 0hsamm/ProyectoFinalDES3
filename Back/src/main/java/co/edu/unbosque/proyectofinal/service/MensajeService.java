package co.edu.unbosque.proyectofinal.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.proyectofinal.dto.MensajeDTO;
import co.edu.unbosque.proyectofinal.configuration.UploadPathConfig;
import co.edu.unbosque.proyectofinal.entity.ArchivoAdjunto;
import co.edu.unbosque.proyectofinal.entity.Conversacion;
import co.edu.unbosque.proyectofinal.entity.Mensaje;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.EstadoSolicitudAmistad;
import co.edu.unbosque.proyectofinal.enums.EstatusMensaje;
import co.edu.unbosque.proyectofinal.enums.RolParticipante;
import co.edu.unbosque.proyectofinal.enums.TipoConversacion;
import co.edu.unbosque.proyectofinal.enums.TipoMensaje;
import co.edu.unbosque.proyectofinal.exception.ConversacionNoEncontradaException;
import co.edu.unbosque.proyectofinal.exception.FraseSecretaInvalidaException;
import co.edu.unbosque.proyectofinal.exception.MensajeNoEncontradoException;
import co.edu.unbosque.proyectofinal.repository.ArchivoAdjuntoRepository;
import co.edu.unbosque.proyectofinal.repository.ConversacionRepository;
import co.edu.unbosque.proyectofinal.repository.MensajeRepository;
import co.edu.unbosque.proyectofinal.repository.ParticipanteConversacionRepository;
import co.edu.unbosque.proyectofinal.repository.SolicitudAmistadRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
@Transactional
public class MensajeService {

	private static final Logger LOGGER =
			LoggerFactory.getLogger(MensajeService.class);

	@Autowired
	private MensajeRepository mensajeRepo;

	@Autowired
	private ConversacionRepository conversacionRepo;

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private ParticipanteConversacionRepository participanteRepo;

	@Autowired
	private SolicitudAmistadRepository solicitudAmistadRepo;

	@Autowired
	private ArchivoAdjuntoRepository archivoAdjuntoRepo;

	@Autowired
	private CloudinaryService cloudinaryService;

	@Autowired
	private CifradoService cifradoService;

	public ResultadoCreacionMensaje create(MensajeDTO dto) {

	    int validacion = validarDtoBasico(dto);
	    if (validacion != 0) return ResultadoCreacionMensaje.conCodigo(validacion);

	    Optional<Usuario> remitente = usuarioRepo.findById(dto.getRemitenteId());
	    if (!remitente.isPresent()) return ResultadoCreacionMensaje.conCodigo(1);

	    Optional<Conversacion> conversacion = conversacionRepo.findById(dto.getConversacionId());
	    if (!conversacion.isPresent()) throw new ConversacionNoEncontradaException();

	    int permisoParticipante = validarPermisoParticipante(dto, conversacion.get());
	    if (permisoParticipante != 0) return ResultadoCreacionMensaje.conCodigo(permisoParticipante);

	    int validacionBloqueo = validarBloqueo(dto, conversacion.get());
	    if (validacionBloqueo != 0) return ResultadoCreacionMensaje.conCodigo(validacionBloqueo);

	    int validacionContenido = validarContenido(dto, conversacion.get());
	    if (validacionContenido != 0) return ResultadoCreacionMensaje.conCodigo(validacionContenido);

	    try {
	        Mensaje mensaje = construirMensaje(dto, remitente.get(), conversacion.get());
	        mensajeRepo.save(mensaje);
	        actualizarConversacion(conversacion.get(), mensaje);
	        return ResultadoCreacionMensaje.exitosa(mapear(mensaje, dto.getFraseSecreta()));
	    } catch (Exception e) {
	        LOGGER.error("Error creando mensaje", e);
	        return ResultadoCreacionMensaje.conCodigo(3);
	    }
	}

	private int validarDtoBasico(MensajeDTO dto) {
	    if (dto == null || dto.getRemitenteId() == null || dto.getConversacionId() == null) return 5;
	    return 0;
	}

	private int validarPermisoParticipante(MensajeDTO dto, Conversacion conversacion) {
	    boolean esParticipante = participanteRepo.existsByConversacion_IdAndUsuario_Id(
	            dto.getConversacionId(), dto.getRemitenteId());
	    if (!esParticipante) return 4;

	    if (conversacion.getTipoConversacion() == TipoConversacion.CANAL) {
	        boolean esAdmin = participanteRepo.existsByConversacion_IdAndUsuario_IdAndRol(
	                dto.getConversacionId(), dto.getRemitenteId(), RolParticipante.ADMIN);
	        if (!esAdmin) return 4;
	    }
	    return 0;
	}

	private int validarBloqueo(MensajeDTO dto, Conversacion conversacion) {
	    if (conversacion.getTipoConversacion() != TipoConversacion.PRIVADA) return 0;

	    Long usuarioDestino = obtenerOtroParticipanteId(conversacion, dto.getRemitenteId());
	    if (usuarioDestino == null) return 0;

	    boolean hayBloqueo =
	            solicitudAmistadRepo.existsBySolicitante_IdAndReceptor_IdAndEstado(
	                    dto.getRemitenteId(),
	                    usuarioDestino,
	                    EstadoSolicitudAmistad.BLOQUEADA)
	            || solicitudAmistadRepo.existsBySolicitante_IdAndReceptor_IdAndEstado(
	                    usuarioDestino,
	                    dto.getRemitenteId(),
	                    EstadoSolicitudAmistad.BLOQUEADA);

	    return hayBloqueo ? 6 : 0;
	}

	private Long obtenerOtroParticipanteId(Conversacion conversacion, Long usuarioIdActual) {
	    return conversacion.getParticipante()
	            .stream()
	            .map(participante -> participante.getUsuario().getId())
	            .filter(id -> !id.equals(usuarioIdActual))
	            .findFirst()
	            .orElse(null);
	}

	private int validarContenido(MensajeDTO dto, Conversacion conversacion) {
	    boolean tieneContenido = tieneTexto(dto.getContenido());
	    boolean tieneAdjunto = dto.isTieneAdjunto();

	    if (dto.getTipoMensaje() == null) {
	        dto.setTipoMensaje(tieneAdjunto ? TipoMensaje.ARCHIVO : TipoMensaje.TEXTO);
	    }

	    if (!tieneContenido && !tieneAdjunto) return 5;

	    if (!cifradoService.validarFrase(dto.getFraseSecreta(), conversacion.getEncripado())) {
	        throw new FraseSecretaInvalidaException();
	    }
	    return 0;
	}

	private Mensaje construirMensaje(MensajeDTO dto, Usuario remitente, Conversacion conversacion) {
	    Mensaje mensaje = new Mensaje();
	    mensaje.setRemitente(remitente);
	    mensaje.setConversacion(conversacion);
	    mensaje.setTipoMensaje(dto.getTipoMensaje());
	    mensaje.setHoraEnvio(LocalDateTime.now());
	    mensaje.setHoraLlegada(null);
	    mensaje.setHoraLeido(null);
	    mensaje.setEstatusMensaje(EstatusMensaje.ENVIADO);

	    if (tieneTexto(dto.getContenido())) {
	        CifradoService.ResultadoCifrado resultado = cifradoService.cifrar(
	                dto.getContenido(), dto.getFraseSecreta(), conversacion.getEncripado());
	        mensaje.setContenidoCifrado(resultado.getTextoCifrado());
	        mensaje.setVi(resultado.getIv());
	    }
	    return mensaje;
	}

	private void actualizarConversacion(Conversacion conversacion, Mensaje mensaje) {
	    conversacion.setFechaUltimoMensaje(mensaje.getHoraEnvio());
	    restaurarConversacionParaParticipantes(conversacion);
	    conversacionRepo.save(conversacion);
	}

	private void restaurarConversacionParaParticipantes(
			Conversacion conversacion) {

		conversacion.getParticipante()
				.forEach(participante -> {
					participante.setOculta(false);
					participante.setFechaOcultada(null);
				});
	}

	public List<MensajeDTO> getAll() {

		return getAll(null);
	}

	public List<MensajeDTO> getAll(
			String fraseSecreta) {

		List<Mensaje> lista =
				mensajeRepo.findAll();

		List<MensajeDTO> dtoList =
				new ArrayList<>();

		lista.forEach(m ->
			dtoList.add(
					mapear(m, fraseSecreta))
		);

		return dtoList;
	}

	public List<MensajeDTO>
	getMensajesPorConversacion(Long id) {

		return getMensajesPorConversacion(id, null);
	}

	public List<MensajeDTO>
	getMensajesPorConversacion(
			Long id,
			String fraseSecreta) {

		return getMensajesPorConversacion(
				id,
				null,
				fraseSecreta);
	}

	public List<MensajeDTO>
	getMensajesPorConversacion(
			Long id,
			Long usuarioId,
			String fraseSecreta) {

		if (usuarioId != null
				&& !participanteRepo.existsByConversacion_IdAndUsuario_Id(
						id,
						usuarioId)) {
			throw new AccessDeniedException(
					"No tienes acceso a esta conversacion");
		}

		List<Mensaje> lista =
				mensajeRepo
						.findByConversacion_IdOrderByHoraEnvioAsc(id);

		List<MensajeDTO> dtoList =
				new ArrayList<>();

		lista.forEach(m ->
			dtoList.add(
					mapear(m, fraseSecreta))
		);

		return dtoList;
	}

	public List<MensajeDTO>
	getUltimosMensajes(Long id) {

		return getUltimosMensajes(id, null);
	}

	public List<MensajeDTO>
	getUltimosMensajes(
			Long id,
			String fraseSecreta) {

		return getUltimosMensajes(
				id,
				null,
				fraseSecreta);
	}

	public List<MensajeDTO>
	getUltimosMensajes(
			Long id,
			Long usuarioId,
			String fraseSecreta) {

		if (usuarioId != null
				&& !participanteRepo.existsByConversacion_IdAndUsuario_Id(
						id,
						usuarioId)) {
			throw new AccessDeniedException(
					"No tienes acceso a esta conversacion");
		}

		List<Mensaje> lista =
				mensajeRepo
						.findTop20ByConversacion_IdOrderByHoraEnvioDesc(
								id);

		List<MensajeDTO> dtoList =
				new ArrayList<>();

		lista.forEach(m ->
			dtoList.add(
					mapear(m, fraseSecreta))
		);

		return dtoList;
	}

	public List<MensajeDTO> getMensajesFijados(
			Long conversacionId,
			Long usuarioId,
			String fraseSecreta) {

		if (usuarioId != null
				&& !participanteRepo.existsByConversacion_IdAndUsuario_Id(
						conversacionId,
						usuarioId)) {
			throw new AccessDeniedException(
					"No tienes acceso a esta conversacion");
		}

		List<MensajeDTO> dtoList =
				new ArrayList<>();

		mensajeRepo.findByConversacion_IdAndFijadoTrueOrderByFechaFijadoDesc(
				conversacionId)
				.forEach(mensaje ->
						dtoList.add(
								mapear(
										mensaje,
										fraseSecreta)));

		return dtoList;
	}

	public MensajeDTO actualizarFijado(
			Long mensajeId,
			Long usuarioId,
			boolean esAdmin,
			boolean fijado,
			String fraseSecreta) {

		Mensaje mensaje =
				mensajeRepo.findById(mensajeId)
						.orElseThrow(
								MensajeNoEncontradoException::new);

		if (!esAdmin
				&& !participanteRepo.existsByConversacion_IdAndUsuario_Id(
						mensaje.getConversacion().getId(),
						usuarioId)) {
			throw new AccessDeniedException(
					"No tienes permiso para fijar mensajes en esta conversacion");
		}

		mensaje.setFijado(fijado);
		mensaje.setFechaFijado(
				fijado
						? LocalDateTime.now()
						: null);

		mensajeRepo.save(mensaje);

		return mapear(
				mensaje,
				fraseSecreta);
	}

	public int deleteById(Long id) {

		return deleteById(id, null, false);
	}

	public int deleteById(Long id, Long usuarioId, boolean esAdmin) {

	    Optional<Mensaje> mensajeOpt = mensajeRepo.findById(id);
	    if (!mensajeOpt.isPresent()) throw new MensajeNoEncontradoException();

	    Mensaje mensaje = mensajeOpt.get();

	    if (usuarioId != null && !esAdmin 
	            && !mensaje.getRemitente().getId().equals(usuarioId)) {
	        throw new AccessDeniedException("No tienes permiso para eliminar este mensaje");
	    }

	    if (mensaje.getAdjunto() != null) {
	        archivoAdjuntoRepo.deleteById(mensaje.getAdjunto().getId());
	        archivoAdjuntoRepo.flush();
	        mensaje.setAdjunto(null);  // desasocia en memoria
	    }

	    mensajeRepo.delete(mensaje);
	    return 0;
	}	
	
	public Map<String, Object> subirAudio(
			MultipartFile archivo)
			throws IOException {

		return cloudinaryService.subirAudio(archivo);
	}

	public MensajeDTO subirAdjunto(
			Long mensajeId,
			MultipartFile archivo,
			Long usuarioId,
			boolean esAdmin,
			String fraseSecreta) {

		Mensaje mensaje =
				mensajeRepo.findById(mensajeId)
						.orElseThrow(
								MensajeNoEncontradoException::new);

		if (usuarioId != null
				&& !esAdmin
				&& !mensaje.getRemitente()
						.getId()
						.equals(usuarioId)) {
			throw new AccessDeniedException(
					"No tienes permiso para adjuntar archivos a este mensaje");
		}

		if (archivo == null
				|| archivo.isEmpty()) {
			throw new IllegalArgumentException(
					"Debes enviar un archivo");
		}

		if (mensaje.getAdjunto() != null) {
			throw new IllegalStateException(
					"El mensaje ya tiene un archivo adjunto");
		}

		if (cifradoService.esHashFraseConfigurado(
				mensaje.getConversacion().getEncripado())
				&& !cifradoService.validarFrase(
						fraseSecreta,
						mensaje.getConversacion().getEncripado())) {
			throw new FraseSecretaInvalidaException();
		}

		String url =
				cloudinaryService.subirArchivo(archivo);

		ArchivoAdjunto adjunto =
				new ArchivoAdjunto();

		adjunto.setMensaje(mensaje);
		adjunto.setRutaAlmacenamiento(url);
		adjunto.setNombreOriginalArchivo(
				archivo.getOriginalFilename());
		adjunto.setFormatoArchivo(
				archivo.getContentType());
		adjunto.setTamanoArchivo(
				archivo.getSize());
		adjunto.setVi("NO_CIFRADO");
		adjunto.setUrl(url);
		adjunto.setPublicId(url);

		archivoAdjuntoRepo.save(adjunto);

		mensaje.setAdjunto(adjunto);
		mensaje.setTipoMensaje(
				determinarTipoMensajeAdjunto(
						archivo.getContentType()));
		mensajeRepo.save(mensaje);

		return mapear(
				mensaje,
				fraseSecreta);
	}

	public ArchivoAdjuntoDescargable obtenerAdjunto(
			Long mensajeId) {

		Mensaje mensaje =
				mensajeRepo.findById(mensajeId)
						.orElseThrow(
								MensajeNoEncontradoException::new);

		ArchivoAdjunto adjunto =
				mensaje.getAdjunto();

		if (adjunto == null) {
			throw new IllegalStateException(
					"El mensaje no tiene un archivo adjunto");
		}

		return new ArchivoAdjuntoDescargable(
				adjunto.getNombreOriginalArchivo(),
				tieneTexto(adjunto.getFormatoArchivo())
						? adjunto.getFormatoArchivo()
						: "application/octet-stream",
				leerContenidoAdjunto(adjunto));
	}

	private MensajeDTO mapear(Mensaje mensaje) {

		return mapear(mensaje, null);
	}

	private MensajeDTO mapear(
			Mensaje mensaje,
			String fraseSecreta) {

		MensajeDTO dto =
				new MensajeDTO();

		dto.setId(mensaje.getId());
		dto.setConversacionId(
				mensaje.getConversacion().getId());
		dto.setRemitenteId(
				mensaje.getRemitente().getId());
		dto.setRemitenteUsuario(
				mensaje.getRemitente().getUsuario());
		dto.setRemitenteNombre(
				tieneTexto(
						mensaje.getRemitente().getNombrePersona())
								? mensaje.getRemitente().getNombrePersona()
								: mensaje.getRemitente().getUsuario());
		dto.setTipoMensaje(
				mensaje.getTipoMensaje());
		dto.setContenido(
				obtenerContenidoVisible(
						mensaje,
						fraseSecreta));
		boolean adjuntoVisible =
				tieneAdjuntoVisible(
						mensaje,
						fraseSecreta);
		dto.setContenidoProtegido(
				tieneContenidoProtegido(
						mensaje,
						dto.getContenido(),
						adjuntoVisible));
		dto.setEstatusMensaje(
				mensaje.getEstatusMensaje());
		dto.setHoraEnvio(
				mensaje.getHoraEnvio());
		dto.setHoraLlegada(
				mensaje.getHoraLlegada());
		dto.setHoraLeido(
				mensaje.getHoraLeido());
		dto.setFijado(
				mensaje.isFijado());
		dto.setFechaFijado(
				mensaje.getFechaFijado());
		dto.setTieneAdjunto(
				mensaje.getAdjunto() != null);
		if (adjuntoVisible) {
			dto.setAdjuntoUrl(
					mensaje.getAdjunto()
							.getUrl());
			dto.setAdjuntoNombreOriginal(
					mensaje.getAdjunto()
							.getNombreOriginalArchivo());
			dto.setAdjuntoFormato(
					mensaje.getAdjunto()
							.getFormatoArchivo());
			dto.setAdjuntoTamano(
					mensaje.getAdjunto()
							.getTamanoArchivo());
		}

		return dto;
	}

	private String obtenerContenidoVisible(
			Mensaje mensaje,
			String fraseSecreta) {

		if (mensaje.getVi() == null) {
			return mensaje.getContenidoCifrado();
		}

		if (fraseSecreta == null
				|| fraseSecreta.trim().isEmpty()) {

			return null;
		}

		try {
			return cifradoService.descifrar(
					mensaje.getContenidoCifrado(),
					mensaje.getVi(),
					fraseSecreta,
					mensaje.getConversacion().getEncripado());

		} catch (RuntimeException e) {
			return null;
		}
	}

	private boolean tieneTexto(
			String valor) {

		return valor != null
				&& !valor.trim().isEmpty();
	}

	private boolean tieneAdjuntoVisible(
			Mensaje mensaje,
			String fraseSecreta) {

		if (mensaje.getAdjunto() == null) {
			return false;
		}

		if (!cifradoService.esHashFraseConfigurado(
				mensaje.getConversacion().getEncripado())) {
			return true;
		}

		return cifradoService.validarFrase(
				fraseSecreta,
				mensaje.getConversacion().getEncripado());
	}

	private boolean tieneContenidoProtegido(
			Mensaje mensaje,
			String contenidoVisible,
			boolean adjuntoVisible) {

		if (mensaje.getVi() != null
				&& contenidoVisible == null) {
			return true;
		}

		return mensaje.getAdjunto() != null
				&& !adjuntoVisible
				&& cifradoService.esHashFraseConfigurado(
						mensaje.getConversacion().getEncripado());
	}

	private TipoMensaje determinarTipoMensajeAdjunto(
			String contentType) {

		if (!tieneTexto(contentType)) {
			return TipoMensaje.ARCHIVO;
		}

		String mime =
				contentType.toLowerCase();

		if (mime.startsWith("image/")) {
			return TipoMensaje.IMAGEN;
		}

		if (mime.startsWith("audio/")) {
			return TipoMensaje.AUDIO;
		}

		if (mime.startsWith("video/")) {
			return TipoMensaje.VIDEO;
		}

		return TipoMensaje.ARCHIVO;
	}

	private byte[] leerContenidoAdjunto(
			ArchivoAdjunto adjunto) {

		Path archivoLocal =
				resolverArchivoLocal(adjunto);

		if (archivoLocal != null
				&& Files.exists(archivoLocal)) {
			try {
				return Files.readAllBytes(
						archivoLocal);
			} catch (IOException e) {
				throw new RuntimeException(
						"No se pudo leer el archivo adjunto guardado localmente",
						e);
			}
		}

		String url =
				tieneTexto(adjunto.getUrl())
						? adjunto.getUrl()
						: adjunto.getRutaAlmacenamiento();

		if (!tieneTexto(url)) {
			throw new RuntimeException(
					"El archivo adjunto no tiene una ruta válida");
		}

		try (InputStream flujo =
				new URL(url).openStream()) {
			return flujo.readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException(
					"No se pudo recuperar el archivo adjunto",
					e);
		}
	}

	private Path resolverArchivoLocal(
			ArchivoAdjunto adjunto) {

		String ruta =
				tieneTexto(adjunto.getRutaAlmacenamiento())
						? adjunto.getRutaAlmacenamiento()
						: adjunto.getUrl();

		if (!tieneTexto(ruta)) {
			return null;
		}

		String nombreArchivo =
				extraerNombreArchivo(ruta);

		if (!tieneTexto(nombreArchivo)) {
			return null;
		}

		return UploadPathConfig
				.obtenerCarpetaUploads()
				.resolve(nombreArchivo)
				.normalize();
	}

	private String extraerNombreArchivo(
			String ruta) {

		try {
			URI uri =
					URI.create(ruta);
			String path =
					uri.getPath();
			if (tieneTexto(path)) {
				return Path.of(path)
						.getFileName()
						.toString();
			}
		} catch (IllegalArgumentException e) {
			// Ignora rutas que no son URI.
		}

		try {
			return Path.of(ruta)
					.getFileName()
					.toString();
		} catch (RuntimeException e) {
			return null;
		}
	}

	public static class ArchivoAdjuntoDescargable {

		private final String nombreArchivo;

		private final String contentType;


	    @SuppressWarnings("java:S2384")
		private final byte[] contenido;

		public ArchivoAdjuntoDescargable(
				String nombreArchivo,
				String contentType,
				byte[] contenido) {

			this.nombreArchivo =
					nombreArchivo;
			this.contentType =
					contentType;
			this.contenido =
					contenido == null
							? null
							: contenido.clone();
		}

		public String getNombreArchivo() {
			return nombreArchivo;
		}

		public String getContentType() {
			return contentType;
		}

		public byte[] getContenido() {
		    return contenido == null
		        ? null
		        : contenido.clone();
		}
	}

	public static class ResultadoCreacionMensaje {

		private final int codigo;

		private final MensajeDTO mensaje;

		private ResultadoCreacionMensaje(
				int codigo,
				MensajeDTO mensaje) {

			this.codigo = codigo;
			this.mensaje = mensaje;
		}

		public static ResultadoCreacionMensaje exitosa(
				MensajeDTO mensaje) {

			return new ResultadoCreacionMensaje(
					0,
					mensaje);
		}

		public static ResultadoCreacionMensaje conCodigo(
				int codigo) {

			return new ResultadoCreacionMensaje(
					codigo,
					null);
		}

		public int getCodigo() {
			return codigo;
		}

		public MensajeDTO getMensaje() {
			return mensaje;
		}
	}
}
