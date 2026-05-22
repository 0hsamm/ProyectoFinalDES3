package co.edu.unbosque.proyectofinal.dto;

import java.util.Objects;

public class CrearSolicitudAmistadDTO {

    private String usernameDestino;

    public CrearSolicitudAmistadDTO() {
    }

    public CrearSolicitudAmistadDTO(
            String usernameDestino) {
        super();
        this.usernameDestino = usernameDestino;
    }

    public String getUsernameDestino() {
        return usernameDestino;
    }

    public void setUsernameDestino(
            String usernameDestino) {
        this.usernameDestino = usernameDestino;
    }

    @Override
    public int hashCode() {
        return Objects.hash(usernameDestino);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CrearSolicitudAmistadDTO other =
                (CrearSolicitudAmistadDTO) obj;
        return Objects.equals(
                usernameDestino,
                other.usernameDestino);
    }

    @Override
    public String toString() {
        return "CrearSolicitudAmistadDTO [usernameDestino="
                + usernameDestino
                + "]";
    }
}
