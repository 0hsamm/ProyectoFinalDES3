package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.proyectofinal.dto.EstadoDTO;
import co.edu.unbosque.proyectofinal.entity.Estado;
import co.edu.unbosque.proyectofinal.entity.EstadoVisto;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.TipoEstado;
import co.edu.unbosque.proyectofinal.repository.EstadoRepository;
import co.edu.unbosque.proyectofinal.repository.EstadoVistoRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
public class EstadoService {

	
	private EstadoRepository estadoRepo;

    private EstadoVistoRepository estadoVistoRepo;

    private UsuarioRepository usuarioRepo;

    private CloudinaryService cloudinaryService;

    /**
     * Crear un estado.
     */
    public EstadoDTO crearEstado(
            Long usuarioId,
            String texto,
            MultipartFile archivo,
            TipoEstado tipo) {

        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        Estado estado = new Estado();

        estado.setTexto(texto);

        estado.setTipo(tipo);

        estado.setFechaCreacion(LocalDateTime.now());

        estado.setFechaExpiracion(LocalDateTime.now().plusHours(24));

        estado.setUsuario(usuario);

        /*
         * Si el estado es multimedia,
         * se sube a Cloudinary.
         */
        if (tipo == TipoEstado.IMAGEN ||
            tipo == TipoEstado.VIDEO) {

            if (archivo == null || archivo.isEmpty()) {
                throw new RuntimeException(
                        "Debe enviar un archivo");
            }

            String mediaUrl =
                    cloudinaryService.subirArchivo(archivo);

            estado.setMediaUrl(mediaUrl);

            estado.setMimeType(
                    archivo.getContentType());
        }

        /*
         * Si es solo texto.
         */
        if (tipo == TipoEstado.TEXTO) {

            if (texto == null || texto.isBlank()) {
                throw new RuntimeException(
                        "El texto no puede estar vacío");
            }
        }

        Estado guardado = estadoRepo.save(estado);

        return convertirDTO(guardado);
    }

    /**
     * Obtener estados activos.
     */
    public List<EstadoDTO> obtenerEstadosActivos() {

        List<Estado> estados =
                estadoRepo.findByFechaExpiracionAfter(
                        LocalDateTime.now());

        return estados.stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener estados de un usuario.
     */
    public List<EstadoDTO> obtenerEstadosUsuario(
            Long usuarioId) {

        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        List<Estado> estados =
                estadoRepo.findByUsuarioAndFechaExpiracionAfter(
                        usuario,
                        LocalDateTime.now());

        return estados.stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    /**
     * Registrar vista.
     */
    public void registrarVista(
            Long estadoId,
            Long usuarioId) {

        Estado estado = estadoRepo.findById(estadoId)
                .orElseThrow(() ->
                        new RuntimeException("Estado no encontrado"));

        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        /*
         * Evitar vistas repetidas.
         */
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

    /**
     * Obtener cantidad de vistas.
     */
    public int obtenerCantidadVistas(Long estadoId) {

        Estado estado = estadoRepo.findById(estadoId)
                .orElseThrow(() ->
                        new RuntimeException("Estado no encontrado"));

        return estadoVistoRepo.findByEstado(estado).size();
    }

    /**
     * Eliminar estados expirados.
     */
    public void eliminarEstadosExpirados() {

        List<Estado> expirados =
                estadoRepo.findByFechaExpiracionBefore(
                        LocalDateTime.now());

        estadoRepo.deleteAll(expirados);
    }

    /**
     * Convertir entidad a DTO.
     */
    private EstadoDTO convertirDTO(Estado estado) {

        EstadoDTO dto = new EstadoDTO();

        dto.setId(estado.getId());

        dto.setTexto(estado.getTexto());

        dto.setMediaUrl(estado.getMediaUrl());

        dto.setMimeType(estado.getMimeType());

        dto.setTipo(estado.getTipo());

        dto.setFechaCreacion(
                estado.getFechaCreacion());

        dto.setFechaExpiracion(
                estado.getFechaExpiracion());

        dto.setUsuarioNombre(
                estado.getUsuario().getUsuario());

        dto.setCantidadVistas(
                estado.getVistas().size());

        return dto;
    }
	
	
}
