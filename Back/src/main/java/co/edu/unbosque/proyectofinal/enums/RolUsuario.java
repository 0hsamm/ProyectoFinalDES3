package co.edu.unbosque.proyectofinal.enums;

import org.springframework.security.core.GrantedAuthority;

public enum RolUsuario
implements GrantedAuthority {

    ROLE_USER,

    ROLE_ADMIN;

    @Override
    public String getAuthority() {

        return name();
    }
}