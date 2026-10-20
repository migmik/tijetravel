package com.tijetravel.backend.dto;

public class LoginResponseDTO {

    private String codigo;
    private String username;
    private String rol;
    private String dni;
    private String nombreCompleto;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String codigo, String username, String rol, String dni, String nombreCompleto) {
        this.codigo = codigo;
        this.username = username;
        this.rol = rol;
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

