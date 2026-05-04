package co.edu.unbosque.proyectofinal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.Conversacion;

public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {
}