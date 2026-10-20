<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'Tije Travel - Agencia de Viajes'}"/> - Tije Travel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

<header class="navbar">
    <div class="navbar-container">
        <a href="${pageContext.request.contextPath}/" class="navbar-brand">
            Tije<span>Travel</span>
        </a>

        <nav>
            <ul class="navbar-menu">
                <li><a href="${pageContext.request.contextPath}/">Buscar Viajes</a></li>
                <li><a href="${pageContext.request.contextPath}/vuelos">Vuelos</a></li>
                <li><a href="${pageContext.request.contextPath}/hoteles">Hoteles</a></li>
                <li><a href="${pageContext.request.contextPath}/sucursales">Sucursales</a></li>

                <c:choose>
                    <c:when test="${empty pageContext.request.userPrincipal}">
                        <li><a href="${pageContext.request.contextPath}/login">Iniciar Sesión</a></li>
                        <li><a href="${pageContext.request.contextPath}/registro" class="btn-nav-accent">Registrarse</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageContext.request.contextPath}/mi-cuenta">Mi Cuenta</a></li>

                        <c:if test="${pageContext.request.isUserInRole('CLIENTE')}">
                            <li><a href="${pageContext.request.contextPath}/reservas/mis-reservas">Mis Reservas</a></li>
                        </c:if>

                        <c:if test="${pageContext.request.isUserInRole('VENDEDOR') or pageContext.request.isUserInRole('ADMINISTRADOR')}">
                            <li><a href="${pageContext.request.contextPath}/reservas-admin">Reservas</a></li>
                            <li><a href="${pageContext.request.contextPath}/gestion">Gestión</a></li>
                        </c:if>

                        <c:if test="${pageContext.request.isUserInRole('ADMINISTRADOR')}">
                            <li><a href="${pageContext.request.contextPath}/admin/usuarios">Usuarios</a></li>
                            <li><a href="${pageContext.request.contextPath}/admin/balance" class="btn-nav-accent">Balance</a></li>
                        </c:if>

                        <li class="user-badge">
                            👤 <c:out value="${pageContext.request.remoteUser}"/>
                        </li>

                        <li>
                            <form action="${pageContext.request.contextPath}/logout" method="post" style="display:inline;">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" style="background:none; border:none; color:#fca5a5; cursor:pointer; font-size:0.9rem; padding:0.5rem;">Salir</button>
                            </form>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </nav>
    </div>
</header>

<main class="main-content">

