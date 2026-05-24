package co.edu.unbosque.proyectofinal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.RegistroAuditoria;

/**
 * Acceso a los registros de auditoria administrativa.
 */
public interface RegistroAuditoriaRepository
        extends JpaRepository<RegistroAuditoria, Long> {
}
