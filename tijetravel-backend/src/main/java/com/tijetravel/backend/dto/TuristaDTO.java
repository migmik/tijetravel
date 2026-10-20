package com.tijetravel.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TuristaDTO {

    private Long id;

    private String codigo;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}$", message = "El DNI debe tener 7 u 8 digitos numericos")
    private String dni;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar 100 caracteres")
    private String apellido;

    @NotBlank(message = "La direccion es obligatoria")
    @Size(max = 255, message = "La direccion no puede superar 255 caracteres")
    private String direccion;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email es invalido")
    private String email;

    private String telefonoFijo;

    @NotBlank(message = "El telefono celular es obligatorio")
    private String telefonoCelular;

    @NotNull(message = "La sucursal es obligatoria")
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

