package co.edu.unbosque.proyectofinal.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.proyectofinal.dto.MensajeDTO;
import co.edu.unbosque.proyectofinal.entity.ArchivoAdjunto;
import co.edu.unbosque.proyectofinal.entity.Conversacion;
import co.edu.unbosque.proyectofinal.entity.Mensaje;
import co.edu.unbosque.proyectofinal.entity.Usuario;
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
	private ArchivoAdjuntoRepository archivoAdjuntoRepo;

	@Autowired
	private CloudinaryService cloudinaryService;

	@Autowired
	private CifradoService cifradoService;

	public ResultadoCreacionMensaje create(MensajeDTO dto) {

	    if (dto == null
	            || dto.getRemitenteId() == null
	            || dto.getConversacionId() == null) {
	        return ResultadoCreacionMensaje.conCodigo(5);
	    }

	    Optional<Usuario> remitente =
	            usuarioRepo.findById(dto.getRemitenteId());

	    if (!remitente.isPresent()) {
	        return ResultadoCreacionMensaje.conCodigo(1);
	    }

	    Optional<Conversacion> conversacion =
	            conversacionRepo.findById(dto.getConversacionId());

	    if (!conversacion.isPresent()) {
	        throw new ConversacionNoEncontradaException();
	    }

	    boolean esParticipante =
	            participanteRepo
	                    .existsByConversacion_IdAndUsuario_Id(
	                            dto.getConversacionId(),
	                            dto.getRemitenteId());

	    if (!esParticipante) {
	        return ResultadoCreacionMensaje.conCodigo(4);
	    }

	    if (conversacion.get().getTipoConversacion()
	            == TipoConversacion.CANAL) {

	        boolean esAdmin =
	                participanteRepo
	                        .existsByConversacion_IdAndUsuario_IdAndRol(
	                                dto.getConversacionId(),
	                                dto.getRemitenteId(),
	                                RolParticipante.ADMIN);

	        if (!esAdmin) {
	            return ResultadoCreacionMensaje.conCodigo(4);
	        }
	    }

	    boolean tieneContenido =
	            tieneTexto(dto.getContenido());

	    boolean tieneAdjunto =
	            dto.isTieneAdjunto();

	    if (dto.getTipoMensaje() == null) {
	        dto.setTipoMensaje(
	                tieneAdjunto
	                        ? TipoMensaje.ARCHIVO
	                        : TipoMensaje.TEXTO);
	    }

	    if (!tieneContenido
	            && !tieneAdjunto) {
	        return ResultadoCreacionMensaje.conCodigo(5);
	    }

	    if ((tieneContenido
	            || tieneAdjunto)
	            && !cifradoService.validarFrase(
	                    dto.getFraseSecreta(),
	                    conversacion.get().getEncripado())) {
	        throw new FraseSecretaInvalidaException();
	    }

	    try {
	        Mensaje mensaje = new Mensaje();
	        mensaje.setRemitente(remitente.get());
	        mensaje.setConversacion(conversacion.get());
	        mensaje.setTipoMensaje(dto.getTipoMensaje());
	        mensaje.setHoraEnvio(LocalDateTime.now());
	        mensaje.setHoraLlegada(null);
	        mensaje.setHoraLeido(null);
	        mensaje.setEstatusMensaje(EstatusMensaje.ENVIADO);

	        if (tieneContenido) {

	            CifradoService.ResultadoCifrado resultadoCifrado =
	                    cifradoService.cifrar(
	                            dto.getContenido(),
	                            dto.getFraseSecreta(),
	                            conversacion.get().getEncripado());

	            mensaje.setContenidoCifrado(
	                    resultadoCifrado.getTextoCifrado());
	            mensaje.setVi(resultadoCifrado.getIv());
	        }

	        mensajeRepo.save(mensaje);

	        Conversacion conversacionActual = conversacion.get();
	        conversacionActual.setFechaUltimoMensaje(mensaje.getHoraEnvio());
	        restaurarConversacionParaParticipantes(conversacionActual);
	        conversacionRepo.save(conversacionActual);

	        return ResultadoCreacionMensaje.exitosa(
	                mapear(
	                        mensaje,
	                        dto.getFraseSecreta()));

	    } catch (Exception e) {
	        LOGGER.error("Error creando mensaje", e);
	        return ResultadoCreacionMensaje.conCodigo(3);
	    }
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

	public int deleteById(Long id) {

		return deleteById(id, null, false);
	}

	public int deleteById(
			Long id,
			Long usuarioId,
			boolean esAdmin) {

	    Optional<Mensaje> mensaje =
	            mensajeRepo.findById(id);

	    if (mensaje.isPresent()) {

	        if (usuarioId != null
	                && !esAdmin
	                && !mensaje.get().getRemitente().getId().equals(usuarioId)) {
	            throw new AccessDeniedException(
	                    "No tienes permiso para eliminar este mensaje");
	        }

	        mensajeRepo.delete(mensaje.get());
	        return 0;
	    }

	    throw new MensajeNoEncontradoException();
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
