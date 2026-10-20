<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Iniciar Sesión" />
<jsp:include page="comun/header.jsp" />

<div style="max-width: 420px; margin: 3rem auto;">
    <div class="card">
        <h2 style="font-size: 1.5rem; font-weight: 700; color: var(--color-primary); text-align: center; margin-bottom: 0.5rem;">
            Iniciar Sesión
        </h2>
        <p style="text-align: center; color: var(--color-text-muted); font-size: 0.9rem; margin-bottom: 1.5rem;">
            Acceda a su cuenta para gestionar sus reservas y grupo familiar.
        </p>

        <c:if test="${not empty errorMensaje}">
            <div class="alert alert-danger">
                <c:out value="${errorMensaje}"/>
            </div>
        </c:if>

        <c:if test="${not empty infoMensaje}">
            <div class="alert alert-info">
                <c:out value="${infoMensaje}"/>
            </div>
        </c:if>

        <c:if test="${not empty exitoMensaje}">
            <div class="alert alert-success">
                <c:out value="${exitoMensaje}"/>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <div class="form-group" style="margin-bottom: 1rem;">
                <label for="username">Usuario</label>
                <input type="text" id="username" name="username" class="form-control" placeholder="Ingrese su usuario" required autofocus />
            </div>

            <div class="form-group" style="margin-bottom: 1.5rem;">
                <label for="password">Contraseña</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Ingrese su contraseña" required />
            </div>

            <button type="submit" class="btn-primary" style="width: 100%; justify-content: center; height: 44px; font-size: 1rem;">
                Ingresar
            </button>
        </form>

        <div style="margin-top: 1.5rem; text-align: center; font-size: 0.9rem; color: var(--color-text-muted); border-top: 1px solid var(--color-border); padding-top: 1rem;">
            ¿No tiene cuenta de cliente? <a href="${pageContext.request.contextPath}/registro" style="color: var(--color-accent); font-weight: 600; text-decoration: none;">Registrarse</a>
        </div>
    </div>
</div>

<jsp:include page="comun/footer.jsp" />

