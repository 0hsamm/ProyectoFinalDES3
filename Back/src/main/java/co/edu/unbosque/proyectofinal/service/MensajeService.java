package co.edu.unbosque.proyectofinal.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.proyectofinal.dto.CrearMensajeDTO;
import co.edu.unbosque.proyectofinal.dto.MensajeDTO;
import co.edu.unbosque.proyectofinal.dto.SubirAudioDTO;
import co.edu.unbosque.proyectofinal.entity.ArchivoAdjunto;
import co.edu.unbosque.proyectofinal.entity.Conversacion;
import co.edu.unbosque.proyectofinal.entity.Mensaje;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.EstatusMensaje;
import co.edu.unbosque.proyectofinal.enums.RolParticipante;
import co.edu.unbosque.proyectofinal.enums.TipoConversacion;
import co.edu.unbosque.proyectofinal.enums.TipoMensaje;
import co.edu.unbosque.proyectofinal.repository.ArchivoAdjuntoRepository;
import co.edu.unbosque.proyectofinal.repository.ConversacionRepository;
import co.edu.unbosque.proyectofinal.repository.MensajeRepository;
import co.edu.unbosque.proyectofinal.repository.ParticipanteConversacionRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
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
	private ArchivoAdjuntoRepository archivoAdjuntoRepo;
	
	@Autowired
	private CloudinaryService cloudinaryService;

	@Autowired
	private ModelMapper mapper;

	public int create(CrearMensajeDTO dto) {

	    try {

	        Optional<Usuario> remitente =
	                usuarioRepo.findByUsuario(
	                        dto.getUsuario());

	        if (!remitente.isPresent()) {
	            return 1;
	        }

	        Optional<Conversacion> conversacion =
	                conversacionRepo.findById(
	                        dto.getConversacionId());

	        if (!conversacion.isPresent()) {
	            return 2;
	        }

	        /**
	         * Validar permisos
	         * en canales.
	         */
	        if (conversacion.get().getTipoConversacion()
	                == TipoConversacion.CANAL) {

	            boolean esAdmin =
	                    participanteRepo
	                            .existsByConversacion_IdAndUsuario_UsuarioAndRol(

	                                    dto.getConversacionId(),

	                                    dto.getUsuario(),

	                                    RolParticipante.ADMIN);

	            if (!esAdmin) {
	                return 4;
	            }
	        }

	        /**
	         * Crear mensaje.
	         */
	        Mensaje mensaje =
	                new Mensaje();

	        mensaje.setRemitente(
	                remitente.get());

	        mensaje.setConversacion(
	                conversacion.get());

	        mensaje.setTipoMensaje(
	                dto.getTipoMensaje());

	        mensaje.setHoraEnvio(
	                LocalDateTime.now());

	        mensaje.setEstatusMensaje(
	                EstatusMensaje.ENVIADO);

	        /**
	         * Si es texto.
	         */
	        if (dto.getTipoMensaje()
	                == TipoMensaje.TEXTO) {

	            mensaje.setContenidoCifrado(
	                    dto.getContenido());
	        }

	        /**
	         * Guardar mensaje primero.
	         */
	        mensajeRepo.save(mensaje);

	        /**
	         * Si es audio.
	         */
	        if (dto.getTipoMensaje()
	                == TipoMensaje.AUDIO) {

	            MultipartFile archivo =
	                    dto.getArchivo();

	            /**
	             * Subir audio a Cloudinary.
	             */
	            Map<String, Object> resultado =
	                    cloudinaryService
	                            .subirAudio(archivo);

	            /**
	             * Crear adjunto.
	             */
	            ArchivoAdjunto adjunto =
	                    new ArchivoAdjunto();

	            adjunto.setMensaje(
	                    mensaje);

	            adjunto.setNombreOriginalArchivo(
	                    archivo.getOriginalFilename());

	            adjunto.setFormatoArchivo(
	                    archivo.getContentType());

	            adjunto.setTamanoArchivo(
	                    archivo.getSize());

	            adjunto.setUrl(
	                    resultado.get("secure_url")
	                            .toString());

	            adjunto.setPublicId(
	                    resultado.get("public_id")
	                            .toString());

	            archivoAdjuntoRepo.save(
	                    adjunto);
	        }

	        return 0;

	    } catch (Exception e) {

	        e.printStackTrace();

	        return 3;
	    }
	}

	public List<MensajeDTO> getAll() {

		List<Mensaje> lista =
				mensajeRepo.findAll();

		List<MensajeDTO> dtoList =
				new ArrayList<>();

		lista.forEach(m ->

			dtoList.add(
					mapper.map(
							m,
							MensajeDTO.class))
		);

		return dtoList;
	}

	public List<MensajeDTO>
	getMensajesPorConversacion(Long id) {

		List<Mensaje> lista =
				mensajeRepo
				.findByConversacion_Id(id);

		List<MensajeDTO> dtoList =
				new ArrayList<>();

		lista.forEach(m ->

			dtoList.add(
					mapper.map(
							m,
							MensajeDTO.class))
		);

		return dtoList;
	}

	public List<MensajeDTO>
	getUltimosMensajes(Long id) {

		List<Mensaje> lista =
				mensajeRepo
				.findTop20ByConversacion_IdOrderByHoraEnvioDesc(
						id);

		List<MensajeDTO> dtoList =
				new ArrayList<>();

		lista.forEach(m ->

			dtoList.add(
					mapper.map(
							m,
							MensajeDTO.class))
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

		return 1;
	}
	
	public Map<String, Object> subirAudio(MultipartFile archivo) throws IOException {

	    return cloudinaryService.subirAudio(archivo);
	}
	
	

}