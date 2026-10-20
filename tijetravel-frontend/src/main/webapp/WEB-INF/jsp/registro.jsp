<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Registro de Cliente" />
<jsp:include page="comun/header.jsp" />

<div style="max-width: 650px; margin: 2rem auto;">
    <div class="card">
        <h2 style="font-size: 1.5rem; font-weight: 700; color: var(--color-primary); text-align: center; margin-bottom: 0.5rem;">
            Crear Cuenta de Cliente
        </h2>
        <p style="text-align: center; color: var(--color-text-muted); font-size: 0.9rem; margin-bottom: 1.5rem;">
            Complete sus datos personales y de acceso para comenzar a reservar en Tije Travel.
        </p>

        <c:if test="${not empty errorValidacion}">
            <div class="alert alert-danger">
                <c:out value="${errorValidacion}"/>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/registro" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <h3 style="font-size: 1.1rem; color: var(--color-primary); margin-bottom: 1rem; border-bottom: 1px solid var(--color-border); padding-bottom: 0.35rem;">
                1. Datos Personales del Titular
            </h3>

            <div class="form-grid">
                <div class="form-group">
                    <label for="dni">DNI (7 u 8 dígitos)</label>
                    <input type="text" id="dni" name="dni" class="form-control" placeholder="Ej: 35123456" pattern="[0-9]{7,8}" title="Debe contener 7 u 8 dígitos numéricos" required />
                </div>

                <div class="form-group">
                    <label for="sucursalCodigo">Sucursal de Atención</label>
                    <select id="sucursalCodigo" name="sucursalCodigo" class="form-control" required>
                        <option value="">Seleccionar sucursal...</option>
                        <c:forEach items="${sucursales}" var="s">
                            <option value="<c:out value='${s.codigo}'/>">
                                <c:out value="${s.codigo}"/> — <c:out value="${s.ciudad}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="nombre">Nombre</label>
                    <input type="text" id="nombre" name="nombre" class="form-control" placeholder="Ej: Juan" required />
                </div>

                <div class="form-group">
                    <label for="apellido">Apellido</label>
                    <input type="text" id="apellido" name="apellido" class="form-control" placeholder="Ej: Pérez" required />
                </div>

                <div class="form-group" style="grid-column: 1 / -1;">
                    <label for="direccion">Dirección Domiciliaria</label>
                    <input type="text" id="direccion" name="direccion" class="form-control" placeholder="Ej: Av. Corrientes 1234" required />
                </div>

                <div class="form-group">
                    <label for="email">Correo Electrónico</label>
                    <input type="email" id="email" name="email" class="form-control" placeholder="nombre@correo.com" required />
                </div>

                <div class="form-group">
                    <label for="telefonoCelular">Teléfono Celular</label>
                    <input type="tel" id="telefonoCelular" name="telefonoCelular" class="form-control" placeholder="Ej: 011-15-5555-1234" required />
                </div>

                <div class="form-group">
                    <label for="telefonoFijo">Teléfono Fijo (Opcional)</label>
                    <input type="tel" id="telefonoFijo" name="telefonoFijo" class="form-control" placeholder="Ej: 011-4777-0000" />
                </div>
            </div>

            <h3 style="font-size: 1.1rem; color: var(--color-primary); margin-top: 1rem; margin-bottom: 1rem; border-bottom: 1px solid var(--color-border); padding-bottom: 0.35rem;">
                2. Credenciales de Acceso
            </h3>

            <div class="form-grid">
                <div class="form-group">
                    <label for="username">Nombre de Usuario</label>
                    <input type="text" id="username" name="username" class="form-control" placeholder="Elija su usuario" minlength="3" required />
                </div>

                <div class="form-group">
                    <label for="password">Contraseña (mínimo 6 caracteres)</label>
                    <input type="password" id="password" name="password" class="form-control" placeholder="Contraseña segura" minlength="6" required />
                </div>
            </div>

            <button type="submit" class="btn-primary" style="width: 100%; justify-content: center; height: 46px; font-size: 1.05rem; margin-top: 1rem;">
                Registrarme como Cliente
            </button>
        </form>

        <div style="margin-top: 1.5rem; text-align: center; font-size: 0.9rem; color: var(--color-text-muted); border-top: 1px solid var(--color-border); padding-top: 1rem;">
            ¿Ya posee una cuenta? <a href="${pageContext.request.contextPath}/login" style="color: var(--color-accent); font-weight: 600; text-decoration: none;">Iniciar Sesión</a>
        </div>
    </div>
</div>

<jsp:include page="comun/footer.jsp" />

