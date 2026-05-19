package co.edu.unbosque.proyectofinal.enums;

public enum EstadoLlamada {
	INICIADA,   // El llamante inició, esperando que el receptor acepte
	ACTIVA,     // Ambos usuarios están en la llamada
	FINALIZADA, // La llamada terminó normalmente
	PERDIDA     // El receptor no contestó o rechazó
}
