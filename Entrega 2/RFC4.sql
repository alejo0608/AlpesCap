SELECT 
    s.tipo                  AS tipo_servicio,
    s.nivel                 AS nivel_servicio,
    COUNT(v.id_viaje)       AS total_servicios,
    ROUND( (COUNT(v.id_viaje) * 100.0 / SUM(COUNT(v.id_viaje)) OVER()), 2 ) AS porcentaje
FROM solicitud_servicio s
JOIN viaje v
    ON v.id_solicitud = s.id_solicitud
JOIN vehiculo vh
    ON vh.id_vehiculo = v.id_vehiculo
JOIN ciudad ciu
    ON ciu.id_ciudad = vh.id_ciudad_expedicion
WHERE ciu.nombre = :nombre_ciudad
  AND v.fecha_asignacion BETWEEN TO_DATE(:fecha_inicio, 'YYYY-MM-DD') 
                             AND TO_DATE(:fecha_fin, 'YYYY-MM-DD')
GROUP BY s.tipo, s.nivel
ORDER BY total_servicios DESC;
