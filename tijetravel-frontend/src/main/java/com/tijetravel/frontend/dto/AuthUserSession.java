package com.tijetravel.frontend.dto;

import java.io.Serializable;

public class AuthUserSession implements Serializable {

    private String username;
    private String password; // Conservado temporalmente en la sesion web del usuario para autenticar llamadas HTTP Basic hacia el backend
    private String rol;
    private String dni;
    private String nombreCompleto;

    public AuthUserSession() {
    }

    public AuthUserSession(String username, String password, String rol, String dni, String nombreCompleto) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
}

