package co.edu.unbosque.proyectofinal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import co.edu.unbosque.proyectofinal.enums.RolUsuario;
import jakarta.persistence.*;

@Entity
@Table(name = "chat_usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuario;

    private String correo;

    private String nombrePersona;

    @Column(name = "contrasena_hash")
    private String contrasenaHash;

    private String sobreMi;

    private String fotoPerfil;

    private boolean enLinea;

    private boolean habilitado;

    private LocalDate fechaNacimiento;

    private LocalDateTime ultimaVezEnLinea;

    private LocalDateTime fechaCreacionCuenta;

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    /*
     * =========================
     * RELACIONES
     * =========================
     */

    @OneToMany(
            mappedBy = "usuario",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TokenVerificacion> tokens =
            new ArrayList<>();

    @OneToMany(
            mappedBy = "usuario",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RegistroAuditoria> auditorias =
            new ArrayList<>();

    public Usuario() {
    }

    /*
     * =========================
     * GETTERS Y SETTERS
     * =========================
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public String getSobreMi() {
        return sobreMi;
    }

    public void setSobreMi(String sobreMi) {
        this.sobreMi = sobreMi;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public boolean isEnLinea() {
        return enLinea;
    }

    public void setEnLinea(boolean enLinea) {
        this.enLinea = enLinea;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDateTime getUltimaVezEnLinea() {
        return ultimaVezEnLinea;
    }

    public void setUltimaVezEnLinea(
            LocalDateTime ultimaVezEnLinea) {

        this.ultimaVezEnLinea =
                ultimaVezEnLinea;
    }

    public LocalDateTime getFechaCreacionCuenta() {
        return fechaCreacionCuenta;
    }

    public void setFechaCreacionCuenta(
            LocalDateTime fechaCreacionCuenta) {

        this.fechaCreacionCuenta =
                fechaCreacionCuenta;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    
    /*
     * =========================
     * SPRING SECURITY
     * =========================
     */

    public List<TokenVerificacion> getTokens() {
		return tokens;
	}

	public void setTokens(List<TokenVerificacion> tokens) {
		this.tokens = tokens;
	}

	public List<RegistroAuditoria> getAuditorias() {
		return auditorias;
	}

	public void setAuditorias(List<RegistroAuditoria> auditorias) {
		this.auditorias = auditorias;
	}

	@Override
    public java.util.Collection<? extends GrantedAuthority>
    getAuthorities() {
        if (rol == null) {
            return List.of();
        }
        return List.of(rol);
    }
    @Override
    public String getPassword() {
        return contrasenaHash;
    }

    @Override
    public String getUsername() {
        return correo;
    }
}
