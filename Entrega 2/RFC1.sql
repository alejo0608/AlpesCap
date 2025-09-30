SELECT 
    u.id_usuario_servicio       AS id_pasajero,
    u.nombre                    AS nombre_pasajero,
    u.correo                    AS correo_pasajero,
    u.telefono                  AS telefono_pasajero,
    s.id_solicitud              AS id_solicitud,
    s.tipo                      AS tipo_servicio,
    s.nivel                     AS nivel_servicio,
    s.estado                    AS estado_solicitud,
    v.id_viaje                  AS id_viaje,
    v.fecha_asignacion,
    v.hora_inicio,
    v.hora_fin,
    v.distancia_km,
    v.costo_total,
    c.id_usuario_servicio       AS id_conductor,
    c.nombre                    AS nombre_conductor,
    vh.placa,
    vh.marca,
    vh.modelo,
    vh.color,
    p.id_pago,
    p.monto                     AS monto_pago,
    p.estado                    AS estado_pago,
    r.id_resenia,
    r.calificacion,
    r.comentario,
    r.autor_rol                 AS resenia_autor
FROM usuario_servicio u
JOIN solicitud_servicio s 
    ON s.id_usuario_servicio = u.id_usuario_servicio
LEFT JOIN viaje v
    ON v.id_solicitud = s.id_solicitud
LEFT JOIN usuario_servicio c 
    ON c.id_usuario_servicio = v.id_usuario_conductor
LEFT JOIN vehiculo vh
    ON vh.id_vehiculo = v.id_vehiculo
LEFT JOIN pago p
    ON p.id_viaje = v.id_viaje
LEFT JOIN resenia r
    ON r.id_viaje = v.id_viaje
WHERE u.id_usuario_servicio = :id_usuario
ORDER BY v.fecha_asignacion DESC, v.hora_inicio DESC;