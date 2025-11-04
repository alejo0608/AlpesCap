package uniandes.edu.co.proyecto.service.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.repositorio.ReseniaRepository;
import uniandes.edu.co.proyecto.repositorio.SolicitudServicioRepository;
import uniandes.edu.co.proyecto.repositorio.ViajeRepository;
import uniandes.edu.co.proyecto.service.ReseniaService;

@Service
public class ReseniaServiceImpl implements ReseniaService {

    private final ReseniaRepository reseniaRepo;
    private final ViajeRepository viajeRepo;
    private final SolicitudServicioRepository solicitudRepo;

    public ReseniaServiceImpl(ReseniaRepository reseniaRepo,
                              ViajeRepository viajeRepo,
                              SolicitudServicioRepository solicitudRepo) {
        this.reseniaRepo = reseniaRepo;
        this.viajeRepo = viajeRepo;
        this.solicitudRepo = solicitudRepo;
    }

    @Override
    @Transactional
    public void pasajeroCalificaConductor(Long idResenia, Long idViaje, Long idUsuarioServicioAutor,
                                          Integer calificacion, String comentario) {

        validarParametrosBasicos(idResenia, idViaje, idUsuarioServicioAutor, calificacion);

        // 1) Validar que el viaje existe
        var viajeOpt = viajeRepo.findById(idViaje);
        if (viajeOpt.isEmpty()) {
            throw new IllegalArgumentException("Viaje inexistente: " + idViaje);
        }

        // 2) Obtener la solicitud asociada al viaje (por repo, no por getter inexistente)
        Long idSolicitud = viajeRepo.findSolicitudIdByViaje(idViaje);
        if (idSolicitud == null) {
            throw new IllegalStateException("El viaje no tiene solicitud asociada");
        }

        // 3) Verificar estado FINALIZADA
        String estado = solicitudRepo.findEstadoById(idSolicitud);
        if (estado == null) {
            throw new IllegalStateException("Solicitud no encontrada para el viaje: " + idViaje);
        }
        if (!"FINALIZADA".equalsIgnoreCase(estado)) {
            throw new IllegalStateException("El viaje aún no está finalizado");
        }

        // 4) Verificar que el autor es el PASAJERO (id_usuario_servicio de la solicitud)
        Long idUsuarioServicioSolicitante = solicitudRepo.findUsuarioServicioIdBySolicitud(idSolicitud);
        if (idUsuarioServicioSolicitante == null || !idUsuarioServicioSolicitante.equals(idUsuarioServicioAutor)) {
            throw new IllegalStateException("El autor no es el pasajero del viaje");
        }

        // 5) Control de duplicado por rol (app-level, sin UNIQUE en SQL)
        if (reseniaRepo.countByViajeAndRol(idViaje, "PASAJERO") > 0) {
            throw new IllegalStateException("Ya existe reseña PASAJERO para este viaje");
        }

        // 6) Insertar reseña
        String fecha = LocalDate.now().toString(); // YYYY-MM-DD
        reseniaRepo.insertarResenia(
                idResenia,
                calificacion,
                (comentario == null ? "" : comentario),
                fecha,
                idViaje,
                "PASAJERO"
        );
    }

    @Override
    @Transactional
    public void conductorCalificaPasajero(Long idResenia, Long idViaje, Long idUsuarioConductorAutor,
                                          Integer calificacion, String comentario) {

        validarParametrosBasicos(idResenia, idViaje, idUsuarioConductorAutor, calificacion);

        // 1) Validar viaje
        var viajeOpt = viajeRepo.findById(idViaje);
        if (viajeOpt.isEmpty()) {
            throw new IllegalArgumentException("Viaje inexistente: " + idViaje);
        }

        // 2) Obtener solicitud y estado
        Long idSolicitud = viajeRepo.findSolicitudIdByViaje(idViaje);
        if (idSolicitud == null) {
            throw new IllegalStateException("El viaje no tiene solicitud asociada");
        }
        String estado = solicitudRepo.findEstadoById(idSolicitud);
        if (estado == null) {
            throw new IllegalStateException("Solicitud no encontrada para el viaje: " + idViaje);
        }
        if (!"FINALIZADA".equalsIgnoreCase(estado)) {
            throw new IllegalStateException("El viaje aún no está finalizado");
        }

        // 3) Verificar que el autor es el CONDUCTOR del viaje
        Long idConductorDelViaje = viajeRepo.findConductorIdByViaje(idViaje);
        if (idConductorDelViaje == null || !idConductorDelViaje.equals(idUsuarioConductorAutor)) {
            throw new IllegalStateException("El autor no es el conductor del viaje");
        }

        // 4) Control de duplicado
        if (reseniaRepo.countByViajeAndRol(idViaje, "CONDUCTOR") > 0) {
            throw new IllegalStateException("Ya existe reseña CONDUCTOR para este viaje");
        }

        // 5) Insertar reseña
        String fecha = LocalDate.now().toString();
        reseniaRepo.insertarResenia(
                idResenia,
                calificacion,
                (comentario == null ? "" : comentario),
                fecha,
                idViaje,
                "CONDUCTOR"
        );
    }

    private static void validarParametrosBasicos(Long idResenia, Long idViaje, Long idAutor, Integer calificacion) {
        if (idResenia == null || idViaje == null || idAutor == null || calificacion == null) {
            throw new IllegalArgumentException("idResenia, idViaje, idAutor y calificacion son obligatorios");
        }
        if (calificacion < 0 || calificacion > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 0 y 5");
        }
    }
}
