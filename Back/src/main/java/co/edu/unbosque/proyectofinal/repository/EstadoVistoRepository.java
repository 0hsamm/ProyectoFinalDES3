package co.edu.unbosque.proyectofinal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.Estado;
import co.edu.unbosque.proyectofinal.entity.EstadoVisto;
import co.edu.unbosque.proyectofinal.entity.Usuario;

public interface EstadoVistoRepository extends JpaRepository<EstadoVisto, Long>{

	boolean existsByEstadoAndUsuario(
            Estado estado,
            Usuario usuario);

    /*
     * Obtener vistas de un estado.
     */
    List<EstadoVisto> findByEstado(
            Estado estado);

    /*
     * Obtener estados vistos por usuario.
     */
    List<EstadoVisto> findByUsuario(
            Usuario usuario);

    /*
     * Contar vistas de un estado.
     */
    int countByEstado(
            Estado estado);

    void deleteByEstado(
            Estado estado);

    /*
     * Buscar visualización específica.
     */
    EstadoVisto findByEstadoAndUsuario(
            Estado estado,
            Usuario usuario);
	
	
}
