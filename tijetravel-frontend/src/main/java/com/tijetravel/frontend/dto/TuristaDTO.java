package com.tijetravel.frontend.dto;

public class TuristaDTO {

    private Long id;
    private String codigo;
    private String dni;
    private String nombre;
    private String apellido;
    private String direccion;
    private String email;
    private String telefonoFijo;
    private String telefonoCelular;
    private Long sucursalId;
    private String sucursalCodigo;
    private String sucursalCiudad;
    private Long titularId;
    private String titularNombreCompleto;

    public TuristaDTO() {
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefonoFijo() {
        return telefonoFijo;
    }

    public void setTelefonoFijo(String telefonoFijo) {
        this.telefonoFijo = telefonoFijo;
    }

    public String getTelefonoCelular() {
        return telefonoCelular;
    }

    public void setTelefonoCelular(String telefonoCelular) {
        this.telefonoCelular = telefonoCelular;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }

    public String getSucursalCodigo() {
        return sucursalCodigo;
    }

    public void setSucursalCodigo(String sucursalCodigo) {
        this.sucursalCodigo = sucursalCodigo;
    }

    public String getSucursalCiudad() {
        return sucursalCiudad;
    }

    public void setSucursalCiudad(String sucursalCiudad) {
        this.sucursalCiudad = sucursalCiudad;
    }

    public Long getTitularId() {
        return titularId;
    }

    public void setTitularId(Long titularId) {
        this.titularId = titularId;
    }

    public String getTitularNombreCompleto() {
        return titularNombreCompleto;
    }

    public void setTitularNombreCompleto(String titularNombreCompleto) {
        this.titularNombreCompleto = titularNombreCompleto;
    }
}

