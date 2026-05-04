package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.CrearUsuarioDTO;
import co.edu.unbosque.proyectofinal.dto.UsuarioDTO;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
public class UsuarioService{

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ModelMapper mapper;

 
    public int create(CrearUsuarioDTO data) {

        Usuario usuario = mapper.map(data, Usuario.class);

        usuario.setFechaCreacionCuenta(LocalDateTime.now());
        usuario.setEnLinea(false);

       
        usuario.setContrasenaHash(data.getContrasena()); 

        usuarioRepo.save(usuario);

        return 0; 
    }

    public List<UsuarioDTO> getAll() {

        List<Usuario> lista = usuarioRepo.findAll();
        List<UsuarioDTO> dtoList = new ArrayList<>();

        lista.forEach(u -> {
            dtoList.add(mapper.map(u, UsuarioDTO.class));
        });

        return dtoList;
    }

 
    public int deleteById(Long id) {

        Optional<Usuario> encontrado = usuarioRepo.findById(id);

        if (encontrado.isPresent()) {
            usuarioRepo.delete(encontrado.get());
            return 0;
        }

        return 1; 
    }

   
    public int updateById(Long id, UsuarioDTO data) {

        Optional<Usuario> encontrado = usuarioRepo.findById(id);

        if (encontrado.isPresent()) {

            Usuario temp = encontrado.get();

            temp.setNombrePersona(data.getNombrePersona());
            temp.setSobreMi(data.getSobreMi());
            temp.setUltimaVezEnLinea(data.getUltimaVezEnLinea());
            temp.setEnLinea(data.isEnLinea());
            usuarioRepo.save(temp);
            return 0;
        }

        return 1; 
    }

    public long count() {
        return usuarioRepo.count();
    }

    public boolean exist(Long id) {
        return usuarioRepo.existsById(id);
    }


    public UsuarioDTO getById(Long id) {
        Optional<Usuario> usuario = usuarioRepo.findById(id);

        if (usuario.isPresent()) {
            return mapper.map(usuario.get(), UsuarioDTO.class);
        }

        return null;
    }

    public UsuarioDTO getByUsername(String username) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByUsuario(username);

        if (usuarioOpt.isPresent()) {
            return mapper.map(usuarioOpt.get(), UsuarioDTO.class);
        }

        return null;
    }
}