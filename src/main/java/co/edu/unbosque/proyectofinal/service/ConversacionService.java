package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.ConversacionDTO;
import co.edu.unbosque.proyectofinal.dto.CrearConversacionDTO;
import co.edu.unbosque.proyectofinal.entity.Conversacion;
import co.edu.unbosque.proyectofinal.entity.ParticipanteConversacion;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.repository.ConversacionRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
public class ConversacionService {

    @Autowired
    private ConversacionRepository conversacionRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ModelMapper mapper;

    
    public int create(CrearConversacionDTO dto) {

        Conversacion conversacion = new Conversacion();
        conversacion.setTipoConversacion(dto.getTipoConversacion());
        conversacion.setFechaCreacion(LocalDateTime.now());
        conversacion.setEncripado("DEFAULT");
        conversacion.setFechaUltimoMensaje(null);

        List<ParticipanteConversacion> participantes = new ArrayList<>();

        for (Long userId : dto.getUsuariosId()) {

            Optional<Usuario> userOpt = usuarioRepo.findById(userId);

            if (!userOpt.isPresent()) {
                return 1; 
            }

            Usuario usuario = userOpt.get();

            ParticipanteConversacion p = new ParticipanteConversacion();
            p.setUsuario(usuario);
            p.setConversacion(conversacion);
            p.setFechaIngresoChat(LocalDateTime.now());
            p.setFechaUltimoLeido(LocalDateTime.now());

            participantes.add(p);
        }

        conversacion.setParticipante(participantes);

        conversacionRepo.save(conversacion);

        return 0;
    }

    
    public List<ConversacionDTO> getAll() {

        List<Conversacion> lista = conversacionRepo.findAll();
        List<ConversacionDTO> dtoList = new ArrayList<>();

        lista.forEach(c -> dtoList.add(mapper.map(c, ConversacionDTO.class)));

        return dtoList;
    }

    public ConversacionDTO getById(Long id) {

        Optional<Conversacion> conv = conversacionRepo.findById(id);

        if (conv.isPresent()) {
            return mapper.map(conv.get(), ConversacionDTO.class);
        }

        return null;
    }

    public int deleteById(Long id) {

        Optional<Conversacion> conv = conversacionRepo.findById(id);

        if (conv.isPresent()) {
            conversacionRepo.delete(conv.get());
            return 0;
        }

        return 1;
    }

    public int updateById(Long id, ConversacionDTO dto) {

        Optional<Conversacion> conv = conversacionRepo.findById(id);

        if (conv.isPresent()) {

            Conversacion temp = conv.get();
            temp.setTipoConversacion(dto.getTipoConversacion());

            conversacionRepo.save(temp);

            return 0;
        }

        return 1;
    }
}