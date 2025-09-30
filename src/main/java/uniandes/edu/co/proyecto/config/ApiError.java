package uniandes.edu.co.proyecto.config;

import java.time.Instant;

public record ApiError(Instant timestamp, int status, String error, String path) { }
