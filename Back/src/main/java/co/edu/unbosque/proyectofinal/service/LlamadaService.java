package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.LlamadaDTO;
import co.edu.unbosque.proyectofinal.dto.LlamadaRespuestaDTO;
import co.edu.unbosque.proyectofinal.entity.Conversacion;
import co.edu.unbosque.proyectofinal.entity.Llamada;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.EstadoLlamada;
import co.edu.unbosque.proyectofinal.enums.TipoLlamada;
import co.edu.unbosque.proyectofinal.repository.ConversacionRepository;
import co.edu.unbosque.proyectofinal.repository.LlamadaRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
public class LlamadaService {

	@Autowired
	private LlamadaRepository llamadaRepo;

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private ConversacionRepository conversacionRepo;

	@Autowired
	private AgoraTokenService agoraTokenService;

	/**
	 * Inicia una nueva llamada.
	 * Devuelve el token de Agora para que el llamante se una al canal.
	 *
	 * Códigos de retorno:
	 *  null   → error (usuario o conversación no encontrados)
	 *  objeto → éxito, con token y datos del canal
	 */
	public LlamadaRespuestaDTO iniciarLlamada(LlamadaDTO dto) {

		if (dto == null
				|| dto.getUsuarioLlamanteId() == null
				|| dto.getUsuarioReceptorId() == null
				|| dto.getConversacionId() == null) {

			return null;
		}

		Optional<Usuario> llamanteOpt =
				usuarioRepo.findById(dto.getUsuarioLlamanteId());

		if (!llamanteOpt.isPresent()) {
			return null;
		}

		Optional<Usuario> receptorOpt =
				usuarioRepo.findById(dto.getUsuarioReceptorId());

		if (!receptorOpt.isPresent()) {
			return null;
		}

		Optional<Conversacion> convOpt =
				conversacionRepo.findById(dto.getConversacionId());

		if (!convOpt.isPresent()) {
			return null;
		}

		Usuario llamante = llamanteOpt.get();
		Usuario receptor = receptorOpt.get();
		Conversacion conversacion = convOpt.get();

		// Nombre único del canal: conv_{id}_{timestamp}
		String canal = "conv_" + dto.getConversacionId()
				+ "_" + System.currentTimeMillis();

		// UID de Agora: usamos el id del usuario como entero
		int uidLlamante = (int) (llamante.getId() % Integer.MAX_VALUE);

		// Generar token para el llamante
		String token = agoraTokenService.generarToken(canal, uidLlamante);

		// Persistir la llamada en la base de datos
		Llamada llamada = new Llamada();
		llamada.setCanalAgora(canal);
		llamada.setTipoLlamada(
				dto.getTipoLlamada() == null
						? TipoLlamada.VOZ
						: dto.getTipoLlamada());
		llamada.setEstadoLlamada(EstadoLlamada.INICIADA);
		llamada.setFechaInicio(LocalDateTime.now());
		llamada.setUsuarioLlamante(llamante);
		llamada.setUsuarioReceptor(receptor);
		llamada.setConversacion(conversacion);

		llamadaRepo.save(llamada);

		// Armar respuesta
		LlamadaRespuestaDTO respuesta = new LlamadaRespuestaDTO();
		respuesta.setId(llamada.getId());
		respuesta.setCanalAgora(canal);
		respuesta.setTokenAgora(token);
		respuesta.setUidAgora(uidLlamante);
		respuesta.setTipoLlamada(llamada.getTipoLlamada());
		respuesta.setEstadoLlamada(llamada.getEstadoLlamada());
		respuesta.setAppIdAgora(agoraTokenService.getAppId());
		respuesta.setConversacionId(dto.getConversacionId());
		respuesta.setUsuarioLlamanteId(dto.getUsuarioLlamanteId());
		respuesta.setUsuarioReceptorId(dto.getUsuarioReceptorId());

		return respuesta;
	}

	/**
	 * El receptor acepta la llamada.
	 * Devuelve su propio token para unirse al mismo canal.
	 *
	 * Códigos:
	 *  null → llamada no encontrada
	 *  obj  → token del receptor
	 */
	public LlamadaRespuestaDTO aceptarLlamada(Long llamadaId, Long usuarioReceptorId) {

		Optional<Llamada> llamadaOpt =
				llamadaRepo.findById(llamadaId);

		if (!llamadaOpt.isPresent()) {
			return null;
		}

		Llamada llamada = llamadaOpt.get();

		// Validar que el receptor es quien debe contestar
		if (llamada.getUsuarioReceptor().getId() != usuarioReceptorId) {
			return null;
		}

		// Cambiar estado a ACTIVA
		llamada.setEstadoLlamada(EstadoLlamada.ACTIVA);
		llamadaRepo.save(llamada);

		// Generar token para el receptor
		int uidReceptor = (int) (usuarioReceptorId % Integer.MAX_VALUE);
		String token = agoraTokenService.generarToken(
				llamada.getCanalAgora(), uidReceptor);

		LlamadaRespuestaDTO respuesta = new LlamadaRespuestaDTO();
		respuesta.setId(llamada.getId());
		respuesta.setCanalAgora(llamada.getCanalAgora());
		respuesta.setTokenAgora(token);
		respuesta.setUidAgora(uidReceptor);
		respuesta.setTipoLlamada(llamada.getTipoLlamada());
		respuesta.setEstadoLlamada(llamada.getEstadoLlamada());
		respuesta.setAppIdAgora(agoraTokenService.getAppId());
		respuesta.setConversacionId(llamada.getConversacion().getId());
		respuesta.setUsuarioLlamanteId(llamada.getUsuarioLlamante().getId());
		respuesta.setUsuarioReceptorId(llamada.getUsuarioReceptor().getId());

		return respuesta;
	}

	/**
	 * Finaliza una llamada (cualquiera de los dos participantes puede hacerlo).
	 *
	 * Códigos:
	 *  0 → éxito
	 *  1 → llamada no encontrada
	 */
	public int finalizarLlamada(Long llamadaId) {

		Optional<Llamada> llamadaOpt =
				llamadaRepo.findById(llamadaId);

		if (!llamadaOpt.isPresent()) {
			return 1;
		}

		Llamada llamada = llamadaOpt.get();

		llamada.setEstadoLlamada(EstadoLlamada.FINALIZADA);
		llamada.setFechaFin(LocalDateTime.now());

		// Calcular duración en segundos
		long duracion = ChronoUnit.SECONDS.between(
				llamada.getFechaInicio(),
				llamada.getFechaFin());

		llamada.setDuracionSegundos(duracion);

		llamadaRepo.save(llamada);

		return 0;
	}

	/**
	 * Marca una llamada como PERDIDA (receptor rechaza o no contesta).
	 *
	 * Códigos:
	 *  0 → éxito
	 *  1 → llamada no encontrada
	 */
	public int rechazarLlamada(Long llamadaId) {

		Optional<Llamada> llamadaOpt =
				llamadaRepo.findById(llamadaId);

		if (!llamadaOpt.isPresent()) {
			return 1;
		}

		Llamada llamada = llamadaOpt.get();

		llamada.setEstadoLlamada(EstadoLlamada.PERDIDA);
		llamada.setFechaFin(LocalDateTime.now());
		llamada.setDuracionSegundos(0L);

		llamadaRepo.save(llamada);

		return 0;
	}

	/**
	 * Obtiene el historial de llamadas de una conversación.
	 */
	public List<LlamadaDTO> getByConversacion(Long conversacionId) {

		List<Llamada> lista =
				llamadaRepo.findByConversacion_Id(conversacionId);

		return mapearLista(lista);
	}

	/**
	 * Obtiene el historial completo de llamadas de un usuario
	 * (tanto las que hizo como las que recibió).
	 */
	public List<LlamadaDTO> getHistorialUsuario(Long usuarioId) {

		List<Llamada> comoLlamante =
				llamadaRepo.findByUsuarioLlamante_Id(usuarioId);

		List<Llamada> comoReceptor =
				llamadaRepo.findByUsuarioReceptor_Id(usuarioId);

		List<Llamada> todas = new ArrayList<>();
		todas.addAll(comoLlamante);
		todas.addAll(comoReceptor);

		// Ordenar por fecha de inicio descendente
		todas.sort((a, b) -> b.getFechaInicio()
				.compareTo(a.getFechaInicio()));

		return mapearLista(todas);
	}

	/**
	 * Obtiene los datos de una llamada por su ID.
	 */
	public LlamadaDTO getById(Long id) {

		Optional<Llamada> opt = llamadaRepo.findById(id);

		if (!opt.isPresent()) {
			return null;
		}

		return mapear(opt.get());
	}

	// ─── Helpers de mapeo ─────────────────────────────────────────────────────

	private List<LlamadaDTO> mapearLista(List<Llamada> lista) {

		List<LlamadaDTO> dtos = new ArrayList<>();

		lista.forEach(l -> dtos.add(mapear(l)));

		return dtos;
	}

	private LlamadaDTO mapear(Llamada l) {

		LlamadaDTO dto = new LlamadaDTO();
		dto.setId(l.getId());
		dto.setCanalAgora(l.getCanalAgora());
		dto.setTipoLlamada(l.getTipoLlamada());
		dto.setEstadoLlamada(l.getEstadoLlamada());
		dto.setFechaInicio(l.getFechaInicio());
		dto.setFechaFin(l.getFechaFin());
		dto.setDuracionSegundos(l.getDuracionSegundos());
		dto.setConversacionId(l.getConversacion().getId());
		dto.setUsuarioLlamanteId(l.getUsuarioLlamante().getId());
		dto.setUsuarioReceptorId(l.getUsuarioReceptor().getId());

		return dto;
	}

}
