package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.proyectofinal.dto.EstadoDTO;
import co.edu.unbosque.proyectofinal.dto.EstadoInteraccionDTO;
import co.edu.unbosque.proyectofinal.entity.Estado;
import co.edu.unbosque.proyectofinal.entity.EstadoLike;
import co.edu.unbosque.proyectofinal.entity.EstadoVisto;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.TipoEstado;
import co.edu.unbosque.proyectofinal.repository.EstadoLikeRepository;
import co.edu.unbosque.proyectofinal.repository.EstadoRepository;
import co.edu.unbosque.proyectofinal.repository.EstadoVistoRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
public class EstadoService {

    private final EstadoRepository estadoRepo;
    private final EstadoVistoRepository estadoVistoRepo;
    private final EstadoLikeRepository estadoLikeRepo;
    private final UsuarioRepository usuarioRepo;
    private final CloudinaryService cloudinaryService;

    public EstadoService(
            EstadoRepository estadoRepo,
            EstadoVistoRepository estadoVistoRepo,
            EstadoLikeRepository estadoLikeRepo,
            UsuarioRepository usuarioRepo,
            CloudinaryService cloudinaryService) {

        this.estadoRepo = estadoRepo;
        this.estadoVistoRepo = estadoVistoRepo;
        this.estadoLikeRepo = estadoLikeRepo;
        this.usuarioRepo = usuarioRepo;
        this.cloudinaryService = cloudinaryService;
    }

    public EstadoDTO crearEstado(
            Long usuarioId,
            String texto,
            MultipartFile archivo,
            TipoEstado tipo) {

        Usuario usuario = obtenerUsuarioActivo(usuarioId);

        TipoEstado tipoFinal =
                resolverTipo(tipo, archivo);

        Estado estado = new Estado();
        estado.setTexto(texto == null ? null : texto.trim());
        estado.setTipo(tipoFinal);
        estado.setFechaCreacion(LocalDateTime.now());
        estado.setFechaExpiracion(LocalDateTime.now().plusHours(24));
        estado.setUsuario(usuario);

        if (tipoFinal == TipoEstado.TEXTO) {
            validarTexto(estado.getTexto());
        } else {
            validarArchivo(archivo, tipoFinal);

            String mediaUrl =
                    cloudinaryService.subirArchivo(archivo);

            estado.setMediaUrl(mediaUrl);
            estado.setThumbnailUrl(mediaUrl);
            estado.setMimeType(archivo.getContentType());
        }

        return convertirDTO(
                estadoRepo.save(estado),
                usuarioId);
    }

    public List<EstadoDTO> obtenerEstadosActivos(
            Long usuarioId) {

        return estadoRepo
                .findByFechaExpiracionAfterOrderByFechaCreacionDesc(
                        LocalDateTime.now())
                .stream()
                .map(estado -> convertirDTO(estado, usuarioId))
                .toList();
    }

    public List<EstadoDTO> obtenerEstadosUsuario(
            Long usuarioId,
            Long viewerId) {

        Usuario usuario = obtenerUsuarioActivo(usuarioId);

        return estadoRepo
                .findByUsuarioAndFechaExpiracionAfterOrderByFechaCreacionDesc(
                        usuario,
                        LocalDateTime.now())
                .stream()
                .map(estado -> convertirDTO(estado, viewerId))
                .toList();
    }

    @Transactional
    public void eliminarEstado(
            Long estadoId,
            Long usuarioId) {

        Estado estado =
                obtenerEstado(estadoId);

        validarPropietario(estado, usuarioId);

        estadoLikeRepo.deleteByEstado(estado);
        estadoRepo.delete(estado);
    }

    public void registrarVista(
            Long estadoId,
            Long usuarioId) {

        Estado estado =
                obtenerEstado(estadoId);

        Usuario usuario =
                obtenerUsuarioActivo(usuarioId);

        if (estado.getUsuario() != null
                && estado.getUsuario().getId().equals(usuario.getId())) {
            return;
        }

        boolean yaExiste =
                estadoVistoRepo.existsByEstadoAndUsuario(
                        estado,
                        usuario);

        if (!yaExiste) {
            EstadoVisto vista = new EstadoVisto();
            vista.setEstado(estado);
            vista.setUsuario(usuario);
            vista.setFechaVista(LocalDateTime.now());

            estadoVistoRepo.save(vista);
        }
    }

    public EstadoDTO darLike(
            Long estadoId,
            Long usuarioId) {

        Estado estado =
                obtenerEstado(estadoId);

        Usuario usuario =
                obtenerUsuarioActivo(usuarioId);

        boolean yaExiste =
                estadoLikeRepo.existsByEstadoAndUsuario(
                        estado,
                        usuario);

        if (!yaExiste) {
            EstadoLike like = new EstadoLike();
            like.setEstado(estado);
            like.setUsuario(usuario);
            like.setFechaLike(LocalDateTime.now());

            estadoLikeRepo.save(like);
        }

        return convertirDTO(estado, usuarioId);
    }

    @Transactional
    public EstadoDTO quitarLike(
            Long estadoId,
            Long usuarioId) {

        Estado estado =
                obtenerEstado(estadoId);

        Usuario usuario =
                obtenerUsuarioActivo(usuarioId);

        estadoLikeRepo
                .findByEstadoAndUsuario(estado, usuario)
                .ifPresent(estadoLikeRepo::delete);

        return convertirDTO(estado, usuarioId);
    }

    public int obtenerCantidadVistas(Long estadoId) {

        Estado estado =
                obtenerEstado(estadoId);

        return estadoVistoRepo.countByEstado(estado);
    }

    public List<EstadoInteraccionDTO> obtenerVistasDetalle(
            Long estadoId,
            Long usuarioId) {

        Estado estado =
                obtenerEstado(estadoId);

        validarPropietario(estado, usuarioId);

        return estadoVistoRepo
                .findByEstado(estado)
                .stream()
                .map(vista ->
                        convertirInteraccion(
                                vista.getUsuario(),
                                vista.getFechaVista()))
                .toList();
    }

    public List<EstadoInteraccionDTO> obtenerLikesDetalle(
            Long estadoId,
            Long usuarioId) {

        Estado estado =
                obtenerEstado(estadoId);

        validarPropietario(estado, usuarioId);

        return estadoLikeRepo
                .findByEstadoOrderByFechaLikeDesc(estado)
                .stream()
                .map(like ->
                        convertirInteraccion(
                                like.getUsuario(),
                                like.getFechaLike()))
                .toList();
    }

    public void eliminarEstadosExpirados() {

        List<Estado> expirados =
                estadoRepo.findByFechaExpiracionBefore(
                        LocalDateTime.now());

        estadoRepo.deleteAll(expirados);
    }

    private Estado obtenerEstado(Long estadoId) {

        return estadoRepo.findById(estadoId)
                .orElseThrow(() ->
                        new RuntimeException("Estado no encontrado"));
    }

    private Usuario obtenerUsuarioActivo(Long usuarioId) {

        return usuarioRepo.findById(usuarioId)
                .filter(Usuario::isHabilitado)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));
    }

    private void validarPropietario(
            Estado estado,
            Long usuarioId) {

        if (estado.getUsuario() == null
                || !estado.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException(
                    "Solo puedes administrar tus propios estados");
        }
    }

    private TipoEstado resolverTipo(
            TipoEstado tipo,
            MultipartFile archivo) {

        if (tipo != null) {
            return tipo;
        }

        if (archivo != null && !archivo.isEmpty()) {
            String contentType =
                    archivo.getContentType() == null
                            ? ""
                            : archivo.getContentType();

            return contentType.startsWith("video/")
                    ? TipoEstado.VIDEO
                    : TipoEstado.IMAGEN;
        }

        return TipoEstado.TEXTO;
    }

    private void validarTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            throw new RuntimeException(
                    "El texto no puede estar vacio");
        }
    }

    private void validarArchivo(
            MultipartFile archivo,
            TipoEstado tipo) {

        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException(
                    "Debe enviar un archivo");
        }

        String contentType =
                archivo.getContentType() == null
                        ? ""
                        : archivo.getContentType();

        if (tipo == TipoEstado.IMAGEN
                && !contentType.startsWith("image/")) {
            throw new RuntimeException(
                    "El archivo seleccionado no es una imagen");
        }

        if (tipo == TipoEstado.VIDEO
                && !contentType.startsWith("video/")) {
            throw new RuntimeException(
                    "El archivo seleccionado no es un video");
        }
    }

    private EstadoDTO convertirDTO(
            Estado estado,
            Long usuarioActualId) {

        EstadoDTO dto = new EstadoDTO();

        dto.setId(estado.getId());
        dto.setTexto(estado.getTexto());
        dto.setMediaUrl(estado.getMediaUrl());
        dto.setThumbnailUrl(estado.getThumbnailUrl());
        dto.setMimeType(estado.getMimeType());
        dto.setTipo(estado.getTipo());
        dto.setFechaCreacion(estado.getFechaCreacion());
        dto.setFechaExpiracion(estado.getFechaExpiracion());

        Usuario usuario = estado.getUsuario();

        if (usuario != null) {
            dto.setUsuarioId(usuario.getId());
            dto.setUsuarioNombre(obtenerNombreVisible(usuario));
            dto.setFotoPerfil(usuario.getFotoPerfil());
        }

        dto.setCantidadVistas(
                estadoVistoRepo.countByEstado(estado));
        dto.setCantidadLikes(
                estadoLikeRepo.countByEstado(estado));

        if (usuarioActualId != null) {
            Usuario usuarioActual =
                    usuarioRepo.findById(usuarioActualId)
                            .orElse(null);

            dto.setPropio(
                    usuario != null
                            && usuario.getId().equals(usuarioActualId));

            if (usuarioActual != null) {
                dto.setVisto(
                        estadoVistoRepo.existsByEstadoAndUsuario(
                                estado,
                                usuarioActual));
                dto.setMeGusta(
                        estadoLikeRepo.existsByEstadoAndUsuario(
                                estado,
                                usuarioActual));
            }
        }

        return dto;
    }

    private EstadoInteraccionDTO convertirInteraccion(
            Usuario usuario,
            LocalDateTime fecha) {

        return new EstadoInteraccionDTO(
                usuario.getId(),
                usuario.getUsuario(),
                obtenerNombreVisible(usuario),
                usuario.getFotoPerfil(),
                fecha);
    }

    private String obtenerNombreVisible(Usuario usuario) {

        return usuario.getNombrePersona() == null
                || usuario.getNombrePersona().isBlank()
                        ? usuario.getUsuario()
                        : usuario.getNombrePersona();
    }
}
