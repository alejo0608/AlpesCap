SELECT 
    c.id_usuario_servicio     AS id_conductor,
    c.nombre                  AS nombre_conductor,
    c.correo                  AS correo_conductor,
    c.telefono                AS telefono_conductor,
    COUNT(v.id_viaje)         AS total_servicios
FROM usuario_servicio c
JOIN viaje v 
    ON c.id_usuario_servicio = v.id_usuario_conductor
GROUP BY c.id_usuario_servicio, c.nombre, c.correo, c.telefono
ORDER BY total_servicios DESC
FETCH FIRST 20 ROWS ONLY;