package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.proyectofinal.dto.ConversacionDTO;
import co.edu.unbosque.proyectofinal.dto.ParticipanteConversacionDTO;
import co.edu.unbosque.proyectofinal.entity.Conversacion;
import co.edu.unbosque.proyectofinal.entity.Mensaje;
import co.edu.unbosque.proyectofinal.entity.ParticipanteConversacion;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.RolParticipante;
import co.edu.unbosque.proyectofinal.enums.TipoConversacion;
import co.edu.unbosque.proyectofinal.repository.ConversacionRepository;
import co.edu.unbosque.proyectofinal.repository.MensajeRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
@Transactional
public class ConversacionService {

	@Autowired
	private ConversacionRepository conversacionRepo;

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private MensajeRepository mensajeRepo;

	@Autowired
	private CifradoService cifradoService;

	public int create(ConversacionDTO dto) {

		if (dto == null
				|| dto.getTipoConversacion() == null) {

			return 2;
		}

		List<Long> participantesIds =
				normalizarParticipantes(
						dto.getParticipantesIds());

		if (participantesIds.isEmpty()) {
			return 2;
		}

		if (!tieneTexto(dto.getFraseSecreta())) {
			return 5;
		}

		if (dto.getTipoConversacion()
				== TipoConversacion.PRIVADA
				&& participantesIds.size() != 2) {

			return 3;
		}

		if (dto.getTipoConversacion()
				== TipoConversacion.GRUPAL
				&& participantesIds.size() < 2) {

			return 3;
		}

		Conversacion conversacion =
				new Conversacion();

		conversacion.setTipoConversacion(
				dto.getTipoConversacion());

		LocalDateTime fechaCreacion =
				LocalDateTime.now();

		conversacion.setFechaCreacion(
				fechaCreacion);

		conversacion.setEncripado(cifradoService.generarHashFrase(dto.getFraseSecreta()));

		conversacion.setFechaUltimoMensaje(
				fechaCreacion);

		List<ParticipanteConversacion> participantes =
				new ArrayList<>();

		for (int i = 0; i < participantesIds.size(); i++) {

			Long usuarioId =
					participantesIds.get(i);

			Optional<Usuario> userOpt =
					usuarioRepo.findById(usuarioId);

			if (!userOpt.isPresent()) {
				return 1;
			}

			ParticipanteConversacion participante =
					new ParticipanteConversacion();

			participante.setUsuario(
					userOpt.get());

			participante.setConversacion(
					conversacion);

			participante.setFechaIngresoChat(
					LocalDateTime.now());

			participante.setFechaUltimoLeido(
					LocalDateTime.now());

			participante.setRol(
					calcularRolInicial(
							dto.getTipoConversacion(),
							i));

			participantes.add(participante);
		}

		conversacion.setParticipante(
				participantes);

		conversacionRepo.save(conversacion);

		return 0;
	}

	public List<ConversacionDTO> getAll() {

		List<Conversacion> lista =
				conversacionRepo.findAll();

		List<ConversacionDTO> dtoList =
				new ArrayList<>();

		lista.forEach(c ->
			dtoList.add(
					mapear(c))
		);

		return dtoList;
	}

	public ConversacionDTO getById(Long id) {

		Optional<Conversacion> conv =
				conversacionRepo.findById(id);

		if (conv.isPresent()) {
			return mapear(conv.get());
		}

		return null;
	}

	public int deleteById(Long id) {

		Optional<Conversacion> conv =
				conversacionRepo.findById(id);

		if (conv.isPresent()) {

			conversacionRepo.delete(conv.get());

			return 0;
		}

		return 1;
	}

	public int updateById(
			Long id,
			ConversacionDTO dto) {

		Optional<Conversacion> conv =
				conversacionRepo.findById(id);

		if (conv.isPresent()) {

			Conversacion temp =
					conv.get();

			if (dto.getTipoConversacion() != null) {
				temp.setTipoConversacion(
						dto.getTipoConversacion());
			}

			conversacionRepo.save(temp);

			return 0;
		}

		return 1;
	}

	public List<ConversacionDTO> getByUsuario(
			Long usuarioId) {

		List<Conversacion> lista =
				conversacionRepo
						.findByParticipante_Usuario_Id(
								usuarioId);

		List<ConversacionDTO> dtoList =
				new ArrayList<>();

		lista.forEach(c ->
			dtoList.add(
					mapear(c))
		);

		return dtoList;
	}

	public List<ConversacionDTO>
	getConUltimoMensaje(Long usuarioId) {

		List<Conversacion> lista =
				conversacionRepo
						.findByParticipante_Usuario_Id(
								usuarioId);

		List<ConversacionDTO> dtoList =
				new ArrayList<>();

		for (Conversacion c : lista) {

			ConversacionDTO dto =
					mapear(c);

			Optional<Mensaje> ultimo =
					mensajeRepo
							.findTop1ByConversacion_IdOrderByHoraEnvioDesc(
									c.getId());

			if (ultimo.isPresent()) {

				if (ultimo.get().getVi() == null) {
					dto.setUltimoMensaje(
							ultimo.get()
									.getContenidoCifrado());
				} else {
					dto.setUltimoMensaje(
							"Mensaje protegido");
				}

				dto.setFechaUltimoMensaje(
						ultimo.get()
								.getHoraEnvio());
			}

			dtoList.add(dto);
		}

		return dtoList;
	}

	public int agregarParticipante(
			ParticipanteConversacionDTO dto) {

		if (dto == null
				|| dto.getConversacionId() == null
				|| dto.getUsuarioId() == null) {

			return 4;
		}

		Optional<Conversacion> convOpt =
				conversacionRepo.findById(
						dto.getConversacionId());

		if (!convOpt.isPresent()) {
			return 1;
		}

		Optional<Usuario> userOpt =
				usuarioRepo.findById(
						dto.getUsuarioId());

		if (!userOpt.isPresent()) {
			return 2;
		}

		Conversacion conversacion =
				convOpt.get();

		Usuario usuario =
				userOpt.get();

		for (ParticipanteConversacion p :
				conversacion.getParticipante()) {

			if (p.getUsuario().getId()
					== dto.getUsuarioId()) {

				return 3;
			}
		}

		ParticipanteConversacion nuevo =
				new ParticipanteConversacion();

		nuevo.setConversacion(conversacion);

		nuevo.setUsuario(usuario);

		nuevo.setFechaIngresoChat(
				LocalDateTime.now());

		nuevo.setFechaUltimoLeido(
				LocalDateTime.now());

		nuevo.setRol(
				dto.getRol() == null
						? RolParticipante.MIEMBRO
						: dto.getRol());

		conversacion.getParticipante()
				.add(nuevo);

		conversacionRepo.save(conversacion);

		return 0;
	}

	public List<ParticipanteConversacionDTO>
	getParticipantes(Long conversacionId) {

		Optional<Conversacion> convOpt =
				conversacionRepo.findById(
						conversacionId);

		if (!convOpt.isPresent()) {
			return new ArrayList<>();
		}

		List<ParticipanteConversacionDTO> dtoList =
				new ArrayList<>();

		for (ParticipanteConversacion p :
				convOpt.get().getParticipante()) {

			dtoList.add(
					mapearParticipante(p));
		}

		return dtoList;
	}

	private List<Long> normalizarParticipantes(
			List<Long> participantesIds) {

		if (participantesIds == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(
				new LinkedHashSet<>(
						participantesIds));
	}

	private RolParticipante calcularRolInicial(
			TipoConversacion tipoConversacion,
			int posicion) {

		if (tipoConversacion == TipoConversacion.PRIVADA) {
			return RolParticipante.MIEMBRO;
		}

		return posicion == 0
				? RolParticipante.ADMIN
				: RolParticipante.MIEMBRO;
	}

	private ConversacionDTO mapear(
			Conversacion conversacion) {

		ConversacionDTO dto =
				new ConversacionDTO();

		dto.setId(conversacion.getId());
		dto.setTipoConversacion(
				conversacion.getTipoConversacion());
		dto.setFechaCreacion(
				conversacion.getFechaCreacion());
		dto.setFechaUltimoMensaje(
				conversacion.getFechaUltimoMensaje());
		dto.setParticipantesIds(
				obtenerParticipantesIds(conversacion));
		dto.setFraseSecretaConfigurada(
				cifradoService.esHashFraseConfigurado(
						conversacion.getEncripado()));

		return dto;
	}

	private List<Long> obtenerParticipantesIds(
			Conversacion conversacion) {

		List<Long> ids =
				new ArrayList<>();

		for (ParticipanteConversacion participante :
				conversacion.getParticipante()) {

			ids.add(
					participante
							.getUsuario()
							.getId());
		}

		return ids;
	}

	private ParticipanteConversacionDTO mapearParticipante(
			ParticipanteConversacion participante) {

		ParticipanteConversacionDTO dto =
				new ParticipanteConversacionDTO();

		dto.setId(participante.getId());
		dto.setConversacionId(
				participante
						.getConversacion()
						.getId());
		dto.setUsuarioId(
				participante
						.getUsuario()
						.getId());
		dto.setRol(
				participante.getRol());
		dto.setFechaIngresoChat(
				participante.getFechaIngresoChat());
		dto.setFechaUltimoLeido(
				participante.getFechaUltimoLeido());

		return dto;
	}

	private boolean tieneTexto(
			String valor) {

		return valor != null
				&& !valor.trim().isEmpty();
	}
}
