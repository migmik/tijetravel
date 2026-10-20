package com.tijetravel.backend.dto;

public class UsuarioDTO {

    private Long id;
    private String codigo;
    private String username;
    private String rol;
    private String dni;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String codigo, String username, String rol, String dni) {
        this.id = id;
        this.codigo = codigo;
        this.username = username;
        this.rol = rol;
        this.dni = dni;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}

