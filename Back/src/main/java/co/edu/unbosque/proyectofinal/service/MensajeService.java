package co.edu.unbosque.proyectofinal.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.proyectofinal.dto.MensajeDTO;
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
import co.edu.unbosque.proyectofinal.repository.ConversacionRepository;
import co.edu.unbosque.proyectofinal.repository.MensajeRepository;
import co.edu.unbosque.proyectofinal.repository.ParticipanteConversacionRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
@Transactional
public class MensajeService {

	@Autowired
	private MensajeRepository mensajeRepo;

	@Autowired
	private ConversacionRepository conversacionRepo;

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private ParticipanteConversacionRepository participanteRepo;

	@Autowired
	private CloudinaryService cloudinaryService;

	@Autowired
	private CifradoService cifradoService;

	public int create(MensajeDTO dto) {

	    if (dto == null
	            || dto.getRemitenteId() == null
	            || dto.getConversacionId() == null) {
	        return 5;
	    }

	    Optional<Usuario> remitente =
	            usuarioRepo.findById(dto.getRemitenteId());

	    if (!remitente.isPresent()) {
	        return 1;
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
	        return 4;
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
	            return 4;
	        }
	    }

	    if (dto.getTipoMensaje() == null) {
	        dto.setTipoMensaje(TipoMensaje.TEXTO);
	    }

	    if ((dto.getTipoMensaje() == TipoMensaje.TEXTO
	            || dto.getTipoMensaje() == TipoMensaje.IA)
	            && (dto.getContenido() == null
	            || dto.getContenido().trim().isEmpty())) {
	        return 5;
	    }

	    if ((dto.getTipoMensaje() == TipoMensaje.TEXTO
	            || dto.getTipoMensaje() == TipoMensaje.IA)
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

	        if (dto.getTipoMensaje() == TipoMensaje.TEXTO
	                || dto.getTipoMensaje() == TipoMensaje.IA) {

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
	        conversacionRepo.save(conversacionActual);

	        return 0;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return 3;
	    }
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

		List<Mensaje> lista =
				mensajeRepo
						.findByConversacion_Id(id);

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

	    Optional<Mensaje> mensaje =
	            mensajeRepo.findById(id);

	    if (mensaje.isPresent()) {
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
		dto.setTipoMensaje(
				mensaje.getTipoMensaje());
		dto.setContenido(
				obtenerContenidoVisible(
						mensaje,
						fraseSecreta));
		dto.setContenidoProtegido(
				mensaje.getVi() != null
						&& dto.getContenido() == null);
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
}
