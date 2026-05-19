package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.AgregarParticipanteDTO;
import co.edu.unbosque.proyectofinal.dto.ConversacionDTO;
import co.edu.unbosque.proyectofinal.dto.CrearConversacionDTO;
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
public class ConversacionService {

	@Autowired
	private ConversacionRepository conversacionRepo;

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private MensajeRepository mensajeRepo;

	public int create(CrearConversacionDTO dto) {

	    // VALIDACIONES
	    if (dto.getTipoConversacion()
	            != TipoConversacion.CANAL

	            && (dto.getUsuariosId() == null
	            || dto.getUsuariosId().isEmpty())) {

	        return 2;
	    }

	    if (dto.getTipoConversacion()
	            == TipoConversacion.PRIVADA

	            && dto.getUsuariosId().size() != 2) {

	        return 3;
	    }

	    Conversacion conversacion =
	            new Conversacion();

	    conversacion.setTipoConversacion(
	            dto.getTipoConversacion());

	    conversacion.setFechaCreacion(
	            LocalDateTime.now());

	    conversacion.setEncripado("DEFAULT");

	    conversacion.setFechaUltimoMensaje(
	            null);

	    List<ParticipanteConversacion>
	    participantes = new ArrayList<>();

	    // =====================================
	    // SI ES CANAL
	    // =====================================

	    if (dto.getTipoConversacion()
	            == TipoConversacion.CANAL) {

	    	Optional<Usuario> adminOpt =
	    	        usuarioRepo.findByUsuario(
	    	                dto.getCreador());

	    	if (!adminOpt.isPresent()) {
	    	    return 1;
	    	}

	    	Usuario admin = adminOpt.get();

	        if (admin == null) {
	            return 1;
	        }

	        ParticipanteConversacion p =
	                new ParticipanteConversacion();

	        p.setUsuario(admin);

	        p.setConversacion(conversacion);

	        p.setFechaIngresoChat(
	                LocalDateTime.now());

	        p.setFechaUltimoLeido(
	                LocalDateTime.now());

	        p.setRol(
	                RolParticipante.ADMIN);

	        participantes.add(p);

	    } else {

	        // =====================================
	        // PRIVADOS Y GRUPOS
	        // =====================================

	        for (Long userId : dto.getUsuariosId()) {

	            Optional<Usuario> userOpt =
	                    usuarioRepo.findById(userId);

	            if (!userOpt.isPresent()) {
	                return 1;
	            }

	            Usuario usuario = userOpt.get();

	            ParticipanteConversacion p =
	                    new ParticipanteConversacion();

	            p.setUsuario(usuario);

	            p.setConversacion(conversacion);

	            p.setFechaIngresoChat(
	                    LocalDateTime.now());

	            p.setFechaUltimoLeido(
	                    LocalDateTime.now());

	            p.setRol(
	                    RolParticipante.ADMIN);

	            participantes.add(p);
	        }
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
					mapper.map(
							c,
							ConversacionDTO.class))
		);

		return dtoList;
	}

	public ConversacionDTO getById(Long id) {

		Optional<Conversacion> conv =
				conversacionRepo.findById(id);

		if (conv.isPresent()) {

			return mapper.map(
					conv.get(),
					ConversacionDTO.class);
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

			Conversacion temp = conv.get();

			temp.setTipoConversacion(
					dto.getTipoConversacion());

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
					mapper.map(
							c,
							ConversacionDTO.class))
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
					mapper.map(
							c,
							ConversacionDTO.class);

			Optional<Mensaje> ultimo =
					mensajeRepo
					.findTop1ByConversacion_IdOrderByHoraEnvioDesc(
							c.getId());

			if (ultimo.isPresent()) {

				dto.setUltimoMensaje(
						ultimo.get()
						.getContenidoCifrado());

				dto.setFechaUltimoMensaje(
						ultimo.get()
						.getHoraEnvio());
			}

			dtoList.add(dto);
		}

		return dtoList;
	}
	
	public int agregarParticipante(
	        AgregarParticipanteDTO dto) {

	    Optional<Conversacion> convOpt =
	            conversacionRepo.findById(
	                    dto.getConversacionId());

	    if (!convOpt.isPresent()) {
	        return 1;
	    }

	    Optional<Usuario> userOpt =
	            usuarioRepo.findByUsuario(
	                    dto.getUsuario());

	    if (!userOpt.isPresent()) {
	        return 2;
	    }

	    Conversacion conversacion =
	            convOpt.get();

	    Usuario usuario =
	            userOpt.get();

	    // VALIDAR SI YA EXISTE
	    for (ParticipanteConversacion p :
	            conversacion.getParticipante()) {

	        if (p.getUsuario().getUsuario()
	                .equals(dto.getUsuario())) {

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
	            RolParticipante.MIEMBRO);

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

	    List<ParticipanteConversacionDTO>
	    dtoList = new ArrayList<>();

	    for (ParticipanteConversacion p :
	            convOpt.get().getParticipante()) {

	        ParticipanteConversacionDTO dto =
	                mapper.map(
	                        p,
	                        ParticipanteConversacionDTO.class);

	        dtoList.add(dto);
	    }

	    return dtoList;
	}
	

}