package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.proyectofinal.dto.UsuarioDTO;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.RolUsuario;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CloudinaryService cloudinaryService;

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

            if (usuarioRepository.findByUsuario(dto.getUsuario().trim()).isPresent()
                    || usuarioRepository.findByCorreo(dto.getCorreo().trim()).isPresent()) {
                return 1;
            }

            Usuario usuario = new Usuario();
            usuario.setUsuario(dto.getUsuario().trim());
            usuario.setCorreo(dto.getCorreo().trim());
            usuario.setNombrePersona(dto.getNombrePersona().trim());
            usuario.setContrasenaHash(
                    passwordEncoder.encode(dto.getContrasena()));
            usuario.setSobreMi(dto.getSobreMi());
            usuario.setFotoPerfil(dto.getFotoPerfil());

            if (usuario.getSobreMi() == null
                    || usuario.getSobreMi().isBlank()) {
                usuario.setSobreMi("");
            }

            usuario.setFechaNacimiento(dto.getFechaNacimiento());
            usuario.setFechaCreacionCuenta(LocalDateTime.now());
            usuario.setEnLinea(false);
            usuario.setUltimaVezEnLinea(null);
            usuario.setHabilitado(true);
            usuario.setRol(RolUsuario.ROLE_USER);

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
                .map(this::mapearPublico)
                .toList();
    }

    public UsuarioDTO getById(Long id) {

        return usuarioRepository.findById(id)
                .filter(Usuario::isHabilitado)
                .map(this::mapearPublico)
                .orElse(null);
    }

    public UsuarioDTO getPerfilById(Long id) {

        return usuarioRepository.findById(id)
                .filter(Usuario::isHabilitado)
                .map(this::mapearPrivado)
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
                String nuevoUsuario =
                        dto.getUsuario().trim();

                if (nuevoUsuario.isBlank()) {
                    return 1;
                }

                Optional<Usuario> usuarioDuplicado =
                        usuarioRepository.findByUsuario(nuevoUsuario);

                if (usuarioDuplicado.isPresent()
                        && !usuarioDuplicado.get().getId().equals(id)) {
                    return 1;
                }

                usuario.setUsuario(nuevoUsuario);
            }

            if (dto.getNombrePersona() != null) {
                String nuevoNombre =
                        dto.getNombrePersona().trim();

                if (nuevoNombre.isBlank()) {
                    return 1;
                }

                usuario.setNombrePersona(nuevoNombre);
            }

            if (dto.getCorreo() != null) {
                String nuevoCorreo =
                        dto.getCorreo().trim();

                if (nuevoCorreo.isBlank()) {
                    return 1;
                }

                Optional<Usuario> correoDuplicado =
                        usuarioRepository.findByCorreo(nuevoCorreo);

                if (correoDuplicado.isPresent()
                        && !correoDuplicado.get().getId().equals(id)) {
                    return 1;
                }

                usuario.setCorreo(nuevoCorreo);
            }

            if (dto.getSobreMi() != null) {
                usuario.setSobreMi(dto.getSobreMi());
            }

            if (dto.getFotoPerfil() != null) {
                usuario.setFotoPerfil(dto.getFotoPerfil());
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
                .map(this::mapearPublico)
                .orElse(null);
    }

    public UsuarioDTO actualizarFotoPerfil(
            Long id,
            MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException("Debes seleccionar una imagen");
        }

        String contentType =
                archivo.getContentType() == null
                        ? ""
                        : archivo.getContentType();

        if (!contentType.startsWith("image/")) {
            throw new RuntimeException("La foto de perfil debe ser una imagen");
        }

        Usuario usuario =
                usuarioRepository
                        .findById(id)
                        .filter(Usuario::isHabilitado)
                        .orElseThrow(() ->
                                new RuntimeException("Usuario no encontrado"));

        String url =
                cloudinaryService.subirArchivo(archivo);

        usuario.setFotoPerfil(url);

        return mapearPrivado(
                usuarioRepository.save(usuario));
    }

    public Optional<Usuario> buscarEntidadPorCorreo(
            String correo) {

        return usuarioRepository.findByCorreo(correo);
    }

    public Optional<Usuario> buscarEntidadPorId(
            Long id) {

        return usuarioRepository.findById(id);
    }

    public boolean esAdmin(
            Usuario usuario) {

        return usuario != null
                && usuario.getRol() == RolUsuario.ROLE_ADMIN;
    }

    private UsuarioDTO mapearPrivado(
            Usuario usuario) {

        UsuarioDTO dto =
                mapearPublico(usuario);

        dto.setCorreo(usuario.getCorreo());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setRol(usuario.getRol());
        dto.setContrasena(null);

        return dto;
    }

    private UsuarioDTO mapearPublico(
            Usuario usuario) {

        UsuarioDTO dto =
                new UsuarioDTO();

        dto.setId(usuario.getId());
        dto.setUsuario(usuario.getUsuario());
        dto.setNombrePersona(usuario.getNombrePersona());
        dto.setSobreMi(usuario.getSobreMi());
        dto.setFotoPerfil(usuario.getFotoPerfil());
        dto.setEnLinea(usuario.isEnLinea());
        dto.setUltimaVezEnLinea(usuario.getUltimaVezEnLinea());
        dto.setCorreo(null);
        dto.setFechaNacimiento(null);
        dto.setContrasena(null);
        dto.setRol(null);

        return dto;
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
