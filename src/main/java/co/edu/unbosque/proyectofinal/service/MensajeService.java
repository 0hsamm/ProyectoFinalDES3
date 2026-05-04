package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.CrearMensajeDTO;
import co.edu.unbosque.proyectofinal.dto.MensajeDTO;
import co.edu.unbosque.proyectofinal.entity.Conversacion;
import co.edu.unbosque.proyectofinal.entity.Mensaje;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.EstatusMensaje;
import co.edu.unbosque.proyectofinal.repository.ConversacionRepository;
import co.edu.unbosque.proyectofinal.repository.MensajeRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
public class MensajeService{

    @Autowired
    private MensajeRepository mensajeRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ConversacionRepository conversacionRepo;

    @Autowired
    private ModelMapper mapper;


    public int create(CrearMensajeDTO dto) {
    	if (dto.getConversacionId() == null) {
    	    return 3; // id de conversación nulo
    	}
        // 🔍 Buscar usuario por username
        Optional<Usuario> userOpt = usuarioRepo.findByUsuario(dto.getUsuario());

        if (!userOpt.isPresent()) {
            return 1; // usuario no existe
        }

        // 🔍 Buscar conversación
        Optional<Conversacion> convOpt = conversacionRepo.findById(dto.getConversacionId());

        if (!convOpt.isPresent()) {
            return 2; // conversación no existe
        }

        Usuario usuario = userOpt.get();
        Conversacion conversacion = convOpt.get();

        // 🧠 Crear mensaje
        Mensaje mensaje = new Mensaje();
        mensaje.setRemitente(usuario);
        mensaje.setConversacion(conversacion);
        mensaje.setTipoMensaje(dto.getTipoMensaje());
        mensaje.setContenidoCifrado(dto.getContenido());
        mensaje.setEstatusMensaje(EstatusMensaje.ENVIADO);
        mensaje.setHoraEnvio(LocalDateTime.now());

        // ⚠️ IMPORTANTE (por tu error anterior)
        mensaje.setHoraLlegada(null);
        mensaje.setHoraLeido(null);

        mensajeRepo.save(mensaje);

        return 0;
    }


 
    public List<MensajeDTO> getAll() {

        List<Mensaje> lista = mensajeRepo.findAll();
        List<MensajeDTO> dtoList = new ArrayList<>();

        lista.forEach(m -> {
            dtoList.add(mapper.map(m, MensajeDTO.class));
        });

        return dtoList;
    }


    public int deleteById(Long id) {

        Optional<Mensaje> encontrado = mensajeRepo.findById(id);

        if (encontrado.isPresent()) {
            mensajeRepo.delete(encontrado.get());
            return 0;
        }

        return 1;
    }

    public int updateById(Long id, MensajeDTO data) {

        Optional<Mensaje> encontrado = mensajeRepo.findById(id);

        if (encontrado.isPresent()) {

            Mensaje temp = encontrado.get();

            temp.setEstatusMensaje(data.getEstatusMensaje());
            temp.setHoraLeido(data.getHoraLeido());
            temp.setHoraLlegada(data.getHoraLlegada());

            mensajeRepo.save(temp);

            return 0;
        }

        return 1;
    }

    public long count() {
        return mensajeRepo.count();
    }

    public boolean exist(Long id) {
        return mensajeRepo.existsById(id);
    }

   
    public List<MensajeDTO> getByConversacion(Long conversacionId) {

    	List<Mensaje> lista = mensajeRepo.findByConversacion_Id(conversacionId);
        List<MensajeDTO> dtoList = new ArrayList<>();

        lista.forEach(m -> dtoList.add(mapper.map(m, MensajeDTO.class)));

        return dtoList;
    }
    
    
    public List<MensajeDTO> getMensajesPorConversacion(Long conversacionId) {

    	List<Mensaje> lista = mensajeRepo.findByConversacion_Id(conversacionId);
        List<MensajeDTO> dtoList = new ArrayList<>();

        lista.forEach(m -> dtoList.add(mapper.map(m, MensajeDTO.class)));

        return dtoList;
    }
    
    public List<MensajeDTO> getUltimosMensajes(Long conversacionId) {

        List<Mensaje> lista = mensajeRepo.findTop20ByConversacion_IdOrderByHoraEnvioDesc(conversacionId);

        List<MensajeDTO> dtoList = new ArrayList<>();

        lista.forEach(m -> dtoList.add(mapper.map(m, MensajeDTO.class)));

        return dtoList;
    }
}