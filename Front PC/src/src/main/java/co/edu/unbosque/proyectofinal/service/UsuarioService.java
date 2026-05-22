package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.UsuarioDTO;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.RolUsuario;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public int create(UsuarioDTO dto) {

        try {
            if (dto == null
                    || dto.getUsuario() == null
                    || dto.getCorreo() == null
                    || dto.getNombrePersona() == null
                    || dto.getContrasena() == null
                    || dto.getContrasena().isBlank()) {

                return 1;
            }

            Usuario usuario = new Usuario();
            usuario.setUsuario(dto.getUsuario());
            usuario.setCorreo(dto.getCorreo());
            usuario.setNombrePersona(dto.getNombrePersona());
            usuario.setContrasenaHash(
                    passwordEncoder.encode(dto.getContrasena()));
            usuario.setSobreMi(dto.getSobreMi());

            if (usuario.getSobreMi() == null
                    || usuario.getSobreMi().isBlank()) {
                usuario.setSobreMi("");
            }

            usuario.setFechaNacimiento(dto.getFechaNacimiento());
            usuario.setFechaCreacionCuenta(LocalDateTime.now());
            usuario.setEnLinea(false);
            usuario.setUltimaVezEnLinea(null);
            usuario.setHabilitado(true);
            usuario.setRol(
                    dto.getRol() != null
                            ? dto.getRol()
                            : RolUsuario.ROLE_USER);

            usuarioRepository.save(usuario);

            return 0;

        } catch (Exception e) {
            return 1;
        }
    }

    public List<UsuarioDTO> getAll() {

        return usuarioRepository.findAll()
                .stream()
                .filter(Usuario::isHabilitado)
                .map(usuario ->
                        modelMapper.map(
                                usuario,
                                UsuarioDTO.class))
                .toList();
    }

    public UsuarioDTO getById(Long id) {

        return usuarioRepository.findById(id)
                .filter(Usuario::isHabilitado)
                .map(usuario ->
                        modelMapper.map(
                                usuario,
                                UsuarioDTO.class))
                .orElse(null);
    }

    public String deleteById(Long id) {

        try {
            Usuario usuario =
                    usuarioRepository
                            .findById(id)
                            .orElse(null);

            if (usuario == null) {
                return "Usuario no encontrado";
            }

            String sufijo =
                    "_eliminado_" + usuario.getId();

            usuario.setHabilitado(false);
            usuario.setEnLinea(false);
            usuario.setUltimaVezEnLinea(LocalDateTime.now());
            usuario.setUsuario(
                    limpiarValorUnico(usuario.getUsuario(), sufijo));
            usuario.setCorreo(
                    "usuario" + usuario.getId()
                            + "@eliminado.local");
            usuario.setNombrePersona("Usuario eliminado");
            usuario.setSobreMi("");

            usuarioRepository.save(usuario);

            return "Usuario eliminado";

        } catch (Exception e) {
            return "Error al eliminar usuario";
        }
    }

    public int updateById(
            Long id,
            UsuarioDTO dto) {

        try {
            Usuario usuario =
                    usuarioRepository
                            .findById(id)
                            .orElse(null);

            if (usuario == null || !usuario.isHabilitado()) {
                return 1;
            }

            if (dto.getUsuario() != null) {
                usuario.setUsuario(dto.getUsuario());
            }

            if (dto.getNombrePersona() != null) {
                usuario.setNombrePersona(dto.getNombrePersona());
            }

            if (dto.getCorreo() != null) {
                usuario.setCorreo(dto.getCorreo());
            }

            if (dto.getSobreMi() != null) {
                usuario.setSobreMi(dto.getSobreMi());
            }

            if (dto.getFechaNacimiento() != null) {
                usuario.setFechaNacimiento(dto.getFechaNacimiento());
            }

            if (dto.getContrasena() != null
                    && !dto.getContrasena().isBlank()) {

                usuario.setContrasenaHash(
                        passwordEncoder.encode(
                                dto.getContrasena()));
            }

            usuarioRepository.save(usuario);

            return 0;

        } catch (Exception e) {
            return 1;
        }
    }

    public UsuarioDTO getByUsername(
            String username) {

        return usuarioRepository
                .findByUsuario(username)
                .filter(Usuario::isHabilitado)
                .map(usuario ->
                        modelMapper.map(
                                usuario,
                                UsuarioDTO.class))
                .orElse(null);
    }

    private String limpiarValorUnico(
            String valor,
            String sufijo) {

        String base =
                valor == null || valor.isBlank()
                        ? "usuario"
                        : valor;

        int maximoBase =
                Math.max(1, 120 - sufijo.length());

        if (base.length() > maximoBase) {
            base = base.substring(0, maximoBase);
        }

        return base + sufijo;
    }
}
