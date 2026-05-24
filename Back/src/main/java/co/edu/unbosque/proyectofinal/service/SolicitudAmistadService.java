package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.proyectofinal.dto.AmistadDTO;
import co.edu.unbosque.proyectofinal.dto.CrearSolicitudAmistadDTO;
import co.edu.unbosque.proyectofinal.dto.SolicitudAmistadDTO;
import co.edu.unbosque.proyectofinal.entity.SolicitudAmistad;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.EstadoSolicitudAmistad;
import co.edu.unbosque.proyectofinal.repository.SolicitudAmistadRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
@Transactional
public class SolicitudAmistadService {

    private final SolicitudAmistadRepository solicitudAmistadRepository;

    private final UsuarioRepository usuarioRepository;

    public SolicitudAmistadService(
            SolicitudAmistadRepository solicitudAmistadRepository,
            UsuarioRepository usuarioRepository) {

        this.solicitudAmistadRepository =
                solicitudAmistadRepository;
        this.usuarioRepository =
                usuarioRepository;
    }

    public int enviarSolicitud(
            String correoSolicitante,
            CrearSolicitudAmistadDTO dto) {

        if (correoSolicitante == null || correoSolicitante.isBlank()) return 2;
        if (dto == null || dto.getUsernameDestino() == null || dto.getUsernameDestino().isBlank()) return 1;

        Optional<Usuario> solicitanteOpt = usuarioRepository.findByCorreo(correoSolicitante);
        if (!solicitanteOpt.isPresent() || !solicitanteOpt.get().isHabilitado()) return 2;

        Optional<Usuario> receptorOpt = usuarioRepository.findByUsuario(dto.getUsernameDestino().trim());
        if (!receptorOpt.isPresent() || !receptorOpt.get().isHabilitado()) return 3;

        Usuario solicitante = solicitanteOpt.get();
        Usuario receptor = receptorOpt.get();

        if (solicitante.getId().equals(receptor.getId())) return 4;

        Optional<Integer> resultadoDirecta = verificarRelacionDirecta(solicitante.getId(), receptor.getId());
        if (resultadoDirecta.isPresent()) return resultadoDirecta.get();

        int resultadoInversa = verificarRelacionInversa(receptor.getId(), solicitante.getId());
        if (resultadoInversa != 0) return resultadoInversa;

        crearNuevaSolicitud(solicitante, receptor);
        return 0;
    }

    private Optional<Integer> verificarRelacionDirecta(Long solicitanteId, Long receptorId) {
        Optional<SolicitudAmistad> directa = solicitudAmistadRepository
                .findBySolicitante_IdAndReceptor_Id(solicitanteId, receptorId);
        if (directa.isPresent()) {
            return Optional.of(manejarRelacionDirectaExistente(directa.get()));
        }
        return Optional.empty();
    }

    private int verificarRelacionInversa(Long receptorId, Long solicitanteId) {
        Optional<SolicitudAmistad> inversa = solicitudAmistadRepository
                .findBySolicitante_IdAndReceptor_Id(receptorId, solicitanteId);
        if (!inversa.isPresent()) return 0;

        EstadoSolicitudAmistad estado = inversa.get().getEstado();
        if (estado == EstadoSolicitudAmistad.PENDIENTE) return 6;
        if (estado == EstadoSolicitudAmistad.ACEPTADA) return 7;
        if (estado == EstadoSolicitudAmistad.BLOQUEADA) return 8;
        return 0;
    }

    private void crearNuevaSolicitud(Usuario solicitante, Usuario receptor) {
        SolicitudAmistad solicitud = new SolicitudAmistad();
        solicitud.setSolicitante(solicitante);
        solicitud.setReceptor(receptor);
        solicitud.setEstado(EstadoSolicitudAmistad.PENDIENTE);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setFechaRespuesta(null);
        solicitudAmistadRepository.save(solicitud);
    }

    public List<SolicitudAmistadDTO>
    obtenerSolicitudesRecibidas(
            String correoReceptor) {

        Usuario receptor =
                buscarUsuarioPorCorreo(correoReceptor);

        if (receptor == null) {
            return new ArrayList<>();
        }

        return solicitudAmistadRepository
                .findByReceptor_IdAndEstado(
                        receptor.getId(),
                        EstadoSolicitudAmistad.PENDIENTE)
                .stream()
                .sorted(
                        Comparator.comparing(
                                SolicitudAmistad::getFechaSolicitud,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()))
                                .reversed())
                .map(this::mapearSolicitud)
                .toList();
    }

    public List<SolicitudAmistadDTO>
    obtenerSolicitudesEnviadas(
            String correoSolicitante) {

        Usuario solicitante =
                buscarUsuarioPorCorreo(correoSolicitante);

        if (solicitante == null) {
            return new ArrayList<>();
        }

        return solicitudAmistadRepository
                .findBySolicitante_IdAndEstado(
                        solicitante.getId(),
                        EstadoSolicitudAmistad.PENDIENTE)
                .stream()
                .sorted(
                        Comparator.comparing(
                                SolicitudAmistad::getFechaSolicitud,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()))
                                .reversed())
                .map(this::mapearSolicitud)
                .toList();
    }

    public int cancelarSolicitud(
            String correoSolicitante,
            Long solicitudId) {

        Usuario solicitante =
                buscarUsuarioPorCorreo(correoSolicitante);

        if (solicitante == null) {
            return 1;
        }

        Optional<SolicitudAmistad> solicitudOpt =
                solicitudAmistadRepository.findById(
                        solicitudId);

        if (!solicitudOpt.isPresent()) {
            return 2;
        }

        SolicitudAmistad solicitud =
                solicitudOpt.get();

        if (!solicitud.getSolicitante()
                .getId()
                .equals(solicitante.getId())) {
            return 3;
        }

        if (solicitud.getEstado()
                != EstadoSolicitudAmistad.PENDIENTE) {
            return 4;
        }

        solicitudAmistadRepository.delete(solicitud);

        return 0;
    }

    public int aceptarSolicitud(
            String correoReceptor,
            Long solicitudId) {

        Usuario receptor =
                buscarUsuarioPorCorreo(correoReceptor);

        if (receptor == null) {
            return 1;
        }

        Optional<SolicitudAmistad> solicitudOpt =
                solicitudAmistadRepository.findById(
                        solicitudId);

        if (!solicitudOpt.isPresent()) {
            return 2;
        }

        SolicitudAmistad solicitud =
                solicitudOpt.get();

        if (!solicitud.getReceptor()
                .getId()
                .equals(receptor.getId())) {
            return 3;
        }

        if (solicitud.getEstado()
                != EstadoSolicitudAmistad.PENDIENTE) {
            return 4;
        }

        solicitud.setEstado(EstadoSolicitudAmistad.ACEPTADA);
        solicitud.setFechaRespuesta(LocalDateTime.now());

        solicitudAmistadRepository.save(solicitud);

        return 0;
    }

    public int rechazarSolicitud(
            String correoReceptor,
            Long solicitudId) {

        Usuario receptor =
                buscarUsuarioPorCorreo(correoReceptor);

        if (receptor == null) {
            return 1;
        }

        Optional<SolicitudAmistad> solicitudOpt =
                solicitudAmistadRepository.findById(
                        solicitudId);

        if (!solicitudOpt.isPresent()) {
            return 2;
        }

        SolicitudAmistad solicitud =
                solicitudOpt.get();

        if (!solicitud.getReceptor()
                .getId()
                .equals(receptor.getId())) {
            return 3;
        }

        if (solicitud.getEstado()
                != EstadoSolicitudAmistad.PENDIENTE) {
            return 4;
        }

        solicitud.setEstado(EstadoSolicitudAmistad.RECHAZADA);
        solicitud.setFechaRespuesta(LocalDateTime.now());

        solicitudAmistadRepository.save(solicitud);

        return 0;
    }

    public List<AmistadDTO> obtenerAmigos(
            String correoUsuario) {

        Usuario usuario =
                buscarUsuarioPorCorreo(correoUsuario);

        if (usuario == null) {
            return new ArrayList<>();
        }

        List<SolicitudAmistad> relaciones =
                new ArrayList<>();

        relaciones.addAll(
                solicitudAmistadRepository
                        .findBySolicitante_IdAndEstado(
                                usuario.getId(),
                                EstadoSolicitudAmistad.ACEPTADA));

        relaciones.addAll(
                solicitudAmistadRepository
                        .findByReceptor_IdAndEstado(
                                usuario.getId(),
                                EstadoSolicitudAmistad.ACEPTADA));

        return relaciones.stream()
                .sorted(
                        Comparator.comparing(
                                SolicitudAmistad::getFechaRespuesta,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()))
                                .reversed())
                .map(relacion ->
                        mapearAmigo(
                                relacion,
                                usuario.getId()))
                .filter(amigo -> amigo.getUsuario() != null)
                .toList();
    }

    public int eliminarAmistad(
            String correoUsuario,
            Long usuarioId) {

        Usuario usuarioActual =
                buscarUsuarioPorCorreo(correoUsuario);

        if (usuarioActual == null) {
            return 1;
        }

        Optional<SolicitudAmistad> directa =
                solicitudAmistadRepository
                        .findBySolicitante_IdAndReceptor_Id(
                                usuarioActual.getId(),
                                usuarioId);

        if (directa.isPresent()
                && directa.get().getEstado()
                == EstadoSolicitudAmistad.ACEPTADA) {

            solicitudAmistadRepository.delete(directa.get());
            return 0;
        }

        Optional<SolicitudAmistad> inversa =
                solicitudAmistadRepository
                        .findBySolicitante_IdAndReceptor_Id(
                                usuarioId,
                                usuarioActual.getId());

        if (inversa.isPresent()
                && inversa.get().getEstado()
                == EstadoSolicitudAmistad.ACEPTADA) {

            solicitudAmistadRepository.delete(inversa.get());
            return 0;
        }

        return 2;
    }

    public List<AmistadDTO> obtenerBloqueados(
            String correoUsuario) {

        Usuario usuario =
                buscarUsuarioPorCorreo(correoUsuario);

        if (usuario == null) {
            return new ArrayList<>();
        }

        return solicitudAmistadRepository
                .findBySolicitante_IdAndEstado(
                        usuario.getId(),
                        EstadoSolicitudAmistad.BLOQUEADA)
                .stream()
                .sorted(
                        Comparator.comparing(
                                SolicitudAmistad::getFechaRespuesta,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()))
                                .reversed())
                .map(relacion ->
                        mapearUsuarioRelacionado(relacion.getReceptor()))
                .toList();
    }

    public int bloquearUsuario(
            String correoUsuario,
            Long usuarioId) {

        Usuario usuarioActual =
                buscarUsuarioPorCorreo(correoUsuario);

        if (usuarioActual == null) {
            return 1;
        }

        if (usuarioId == null
                || usuarioActual.getId().equals(usuarioId)) {
            return 3;
        }

        Usuario usuarioObjetivo =
                usuarioRepository.findById(usuarioId)
                        .filter(Usuario::isHabilitado)
                        .orElse(null);

        if (usuarioObjetivo == null) {
            return 2;
        }

        Optional<SolicitudAmistad> directa =
                solicitudAmistadRepository
                        .findBySolicitante_IdAndReceptor_Id(
                                usuarioActual.getId(),
                                usuarioObjetivo.getId());

        if (directa.isPresent()
                && directa.get().getEstado()
                        == EstadoSolicitudAmistad.BLOQUEADA) {
            return 4;
        }

        SolicitudAmistad relacion =
                directa.orElseGet(() ->
                        solicitudAmistadRepository
                                .findBySolicitante_IdAndReceptor_Id(
                                        usuarioObjetivo.getId(),
                                        usuarioActual.getId())
                                .orElseGet(SolicitudAmistad::new));

        relacion.setSolicitante(usuarioActual);
        relacion.setReceptor(usuarioObjetivo);
        relacion.setEstado(EstadoSolicitudAmistad.BLOQUEADA);
        relacion.setFechaSolicitud(LocalDateTime.now());
        relacion.setFechaRespuesta(LocalDateTime.now());

        solicitudAmistadRepository.save(relacion);

        return 0;
    }

    public int desbloquearUsuario(
            String correoUsuario,
            Long usuarioId) {

        Usuario usuarioActual =
                buscarUsuarioPorCorreo(correoUsuario);

        if (usuarioActual == null) {
            return 1;
        }

        Optional<SolicitudAmistad> relacion =
                solicitudAmistadRepository
                        .findBySolicitante_IdAndReceptor_Id(
                                usuarioActual.getId(),
                                usuarioId);

        if (!relacion.isPresent()
                || relacion.get().getEstado()
                        != EstadoSolicitudAmistad.BLOQUEADA) {
            return 2;
        }

        solicitudAmistadRepository.delete(relacion.get());

        return 0;
    }

    public boolean existeBloqueoEntreUsuarios(
            Long primerUsuarioId,
            Long segundoUsuarioId) {

        if (primerUsuarioId == null
                || segundoUsuarioId == null) {
            return false;
        }

        return solicitudAmistadRepository
                .existsBySolicitante_IdAndReceptor_IdAndEstado(
                        primerUsuarioId,
                        segundoUsuarioId,
                        EstadoSolicitudAmistad.BLOQUEADA)
                || solicitudAmistadRepository
                        .existsBySolicitante_IdAndReceptor_IdAndEstado(
                                segundoUsuarioId,
                                primerUsuarioId,
                                EstadoSolicitudAmistad.BLOQUEADA);
    }

    private int manejarRelacionDirectaExistente(
            SolicitudAmistad solicitud) {

        if (solicitud.getEstado()
                == EstadoSolicitudAmistad.PENDIENTE) {
            return 5;
        }

        if (solicitud.getEstado()
                == EstadoSolicitudAmistad.ACEPTADA) {
            return 7;
        }

        if (solicitud.getEstado()
                == EstadoSolicitudAmistad.BLOQUEADA) {
            return 8;
        }

        solicitud.setEstado(EstadoSolicitudAmistad.PENDIENTE);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setFechaRespuesta(null);

        solicitudAmistadRepository.save(solicitud);

        return 0;
    }

    private Usuario buscarUsuarioPorCorreo(
            String correo) {

        if (correo == null
                || correo.isBlank()) {
            return null;
        }

        return usuarioRepository.findByCorreo(correo)
                .filter(Usuario::isHabilitado)
                .orElse(null);
    }

    private SolicitudAmistadDTO mapearSolicitud(
            SolicitudAmistad solicitud) {

        SolicitudAmistadDTO dto =
                new SolicitudAmistadDTO();

        dto.setId(solicitud.getId());
        dto.setSolicitanteId(
                solicitud.getSolicitante().getId());
        dto.setSolicitanteUsuario(
                solicitud.getSolicitante().getUsuario());
        dto.setSolicitanteNombre(
                solicitud.getSolicitante().getNombrePersona());
        dto.setSolicitanteFotoPerfil(
                solicitud.getSolicitante().getFotoPerfil());
        dto.setReceptorId(
                solicitud.getReceptor().getId());
        dto.setReceptorUsuario(
                solicitud.getReceptor().getUsuario());
        dto.setReceptorNombre(
                solicitud.getReceptor().getNombrePersona());
        dto.setReceptorFotoPerfil(
                solicitud.getReceptor().getFotoPerfil());
        dto.setEstado(solicitud.getEstado());
        dto.setFechaSolicitud(solicitud.getFechaSolicitud());
        dto.setFechaRespuesta(solicitud.getFechaRespuesta());

        return dto;
    }

    private AmistadDTO mapearAmigo(
            SolicitudAmistad solicitud,
            Long usuarioActualId) {

        Usuario amigo =
                solicitud.getSolicitante()
                        .getId()
                        .equals(usuarioActualId)
                                ? solicitud.getReceptor()
                                : solicitud.getSolicitante();

        if (!amigo.isHabilitado()) {
            return new AmistadDTO();
        }

        AmistadDTO dto =
                new AmistadDTO();

        dto.setSolicitudId(solicitud.getId());
        dto.setUsuarioId(amigo.getId());
        dto.setUsuario(amigo.getUsuario());
        dto.setNombrePersona(amigo.getNombrePersona());
        dto.setFotoPerfil(amigo.getFotoPerfil());
        dto.setSobreMi(amigo.getSobreMi());
        dto.setEnLinea(amigo.isEnLinea());
        dto.setFechaAmistad(solicitud.getFechaRespuesta());

        return dto;
    }

    private AmistadDTO mapearUsuarioRelacionado(
            Usuario usuario) {

        if (usuario == null
                || !usuario.isHabilitado()) {
            return new AmistadDTO();
        }

        AmistadDTO dto =
                new AmistadDTO();

        dto.setUsuarioId(usuario.getId());
        dto.setUsuario(usuario.getUsuario());
        dto.setNombrePersona(usuario.getNombrePersona());
        dto.setFotoPerfil(usuario.getFotoPerfil());
        dto.setSobreMi(usuario.getSobreMi());
        dto.setEnLinea(usuario.isEnLinea());

        return dto;
    }
}
