SELECT 
    c.id_usuario_servicio        AS id_conductor,
    c.nombre                     AS nombre_conductor,
    vh.id_vehiculo,
    vh.placa,
    vh.marca,
    vh.modelo,
    s.tipo                       AS tipo_servicio,
    SUM(v.costo_total * 0.80)    AS total_ganado -- suponiendo comisión del 20%
FROM usuario_servicio c
JOIN viaje v 
    ON c.id_usuario_servicio = v.id_usuario_conductor
JOIN vehiculo vh
    ON vh.id_vehiculo = v.id_vehiculo
JOIN solicitud_servicio s
    ON s.id_solicitud = v.id_solicitud
GROUP BY c.id_usuario_servicio, c.nombre, vh.id_vehiculo, vh.placa, vh.marca, vh.modelo, s.tipo
ORDER BY c.id_usuario_servicio, vh.id_vehiculo, s.tipo;