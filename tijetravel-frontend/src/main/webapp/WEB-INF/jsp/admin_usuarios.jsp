<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Administración de Usuarios" />
<jsp:include page="comun/header.jsp" />

<div class="card" style="margin-bottom: 1.5rem;">
    <h2 class="section-title">👤 Administración de Usuarios y Roles</h2>
    <p style="color: var(--color-text-muted); font-size: 0.9rem; margin-bottom: 1.25rem;">
        Consulte y asigne permisos a las cuentas del sistema (Roles: CLIENTE, VENDEDOR, ADMINISTRADOR).
    </p>

    <!-- Filtros combinables -->
    <form action="${pageContext.request.contextPath}/admin/usuarios" method="get">
        <div class="search-form-row">
            <div class="form-group-compact">
                <label for="username">Nombre de Usuario</label>
                <input type="text" id="username" name="username" class="form-control" value="<c:out value='${username}'/>" placeholder="Usuario..." />
            </div>

            <div class="form-group-compact">
                <label for="dni">DNI</label>
                <input type="text" id="dni" name="dni" class="form-control" value="<c:out value='${dni}'/>" placeholder="DNI..." />
            </div>

            <div class="form-group-compact">
                <label for="rol">Rol</label>
                <select id="rol" name="rol" class="form-control">
                    <option value="">Todos los roles</option>
                    <option value="CLIENTE" <c:if test="${rol eq 'CLIENTE'}">selected</c:if>>CLIENTE</option>
                    <option value="VENDEDOR" <c:if test="${rol eq 'VENDEDOR'}">selected</c:if>>VENDEDOR</option>
                    <option value="ADMINISTRADOR" <c:if test="${rol eq 'ADMINISTRADOR'}">selected</c:if>>ADMINISTRADOR</option>
                </select>
            </div>

            <div>
                <button type="submit" class="btn-primary" style="height: 42px;">Filtrar Usuarios</button>
            </div>
        </div>
    </form>
</div>

<!-- Formulario para crear nuevo usuario interno -->
<div class="card" style="margin-bottom: 2rem; background-color: #f8fafc;">
    <h3 style="font-size: 1.15rem; color: var(--color-primary); font-weight: 700; margin-bottom: 1rem;">
        ➕ Crear Nueva Cuenta de Usuario
    </h3>

    <form action="${pageContext.request.contextPath}/admin/usuarios/crear" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <div class="form-grid">
            <div class="form-group">
                <label for="newUsername">Usuario</label>
                <input type="text" id="newUsername" name="username" class="form-control" placeholder="Ej: nuevo.empleado" minlength="3" required />
            </div>

            <div class="form-group">
                <label for="newPassword">Contraseña</label>
                <input type="password" id="newPassword" name="password" class="form-control" placeholder="Mínimo 6 caracteres" minlength="6" required />
            </div>

            <div class="form-group">
                <label for="newDni">DNI de la Persona</label>
                <input type="text" id="newDni" name="dni" class="form-control" placeholder="7 u 8 dígitos" pattern="[0-9]{7,8}" required />
            </div>

            <div class="form-group">
                <label for="newRol">Rol Asignado</label>
                <select id="newRol" name="rol" class="form-control" required>
                    <option value="VENDEDOR">VENDEDOR (Operaciones y reservas)</option>
                    <option value="ADMINISTRADOR">ADMINISTRADOR (Acceso total y balance)</option>
                    <option value="CLIENTE">CLIENTE (Requiere turista previo con ese DNI)</option>
                </select>
            </div>
        </div>

        <button type="submit" class="btn-primary">Crear Usuario</button>
    </form>
</div>

<!-- Listado de Usuarios -->
<div class="table-container">
    <table class="data-table">
        <thead>
            <tr>
                <th>Código</th>
                <th>Usuario</th>
                <th>DNI Asociado</th>
                <th>Rol Actual</th>
                <th>Modificar Rol</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${not empty paginaUsuarios.content}">
                    <c:forEach items="${paginaUsuarios.content}" var="u">
                        <tr>
                            <td><strong><c:out value="${u.codigo}"/></strong></td>
                            <td><c:out value="${u.username}"/></td>
                            <td><c:out value="${u.dni}"/></td>
                            <td>
                                <span class="status-badge <c:out value="${u.rol eq 'ADMINISTRADOR' ? 'status-paid' : 'status-pending'}"/>">
                                    <c:out value="${u.rol}"/>
                                </span>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/admin/usuarios/<c:out value='${u.id}'/>/rol" method="post" style="display:flex; gap:0.5rem; align-items:center;">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                    <select name="rol" class="form-control" style="padding: 0.3rem 0.5rem; font-size: 0.85rem; width: auto;">
                                        <option value="CLIENTE" <c:if test="${u.rol eq 'CLIENTE'}">selected</c:if>>CLIENTE</option>
                                        <option value="VENDEDOR" <c:if test="${u.rol eq 'VENDEDOR'}">selected</c:if>>VENDEDOR</option>
                                        <option value="ADMINISTRADOR" <c:if test="${u.rol eq 'ADMINISTRADOR'}">selected</c:if>>ADMINISTRADOR</option>
                                    </select>
                                    <button type="submit" class="btn-primary" style="padding: 0.35rem 0.75rem; font-size: 0.8rem;">
                                        Actualizar
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="5" style="text-align: center; color: var(--color-text-muted); padding: 2rem;">
                            No se encontraron usuarios coincidentes.
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<jsp:include page="comun/footer.jsp" />

