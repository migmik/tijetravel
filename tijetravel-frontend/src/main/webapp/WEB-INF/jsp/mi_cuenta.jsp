<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Mi Cuenta" />
<jsp:include page="comun/header.jsp" />

<div class="card" style="margin-bottom: 2rem;">
    <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
        <div>
            <h2 style="font-size: 1.5rem; font-weight: 700; color: var(--color-primary);">
                Bienvenido, <c:out value="${usuario.nombreCompleto}"/>
            </h2>
            <p style="color: var(--color-text-muted); font-size: 0.95rem;">
                Usuario: <strong><c:out value="${usuario.username}"/></strong> |
                Rol: <span class="status-badge status-paid"><c:out value="${usuario.rol}"/></span> |
                DNI: <strong><c:out value="${usuario.dni}"/></strong>
            </p>
        </div>

        <c:if test="${usuario.rol eq 'CLIENTE'}">
            <div>
                <a href="${pageContext.request.contextPath}/reservas/mis-reservas" class="btn-primary">
                    📋 Ver Mis Reservas
                </a>
            </div>
        </c:if>
    </div>
</div>

<c:if test="${usuario.rol eq 'CLIENTE'}">
    <!-- Grupo Familiar del Cliente -->
    <section class="card" style="margin-bottom: 2rem;">
        <h3 class="section-title">👨‍👩‍👧‍👦 Mi Grupo Familiar</h3>
        <p style="color: var(--color-text-muted); font-size: 0.9rem; margin-bottom: 1.25rem;">
            Los miembros de su grupo familiar comparten su sucursal de atención y usted puede realizar reservas a nombre de cualquiera de ellos.
        </p>

        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>DNI</th>
                        <th>Nombre y Apellido</th>
                        <th>Email</th>
                        <th>Celular</th>
                        <th>Parentesco / Rol</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${grupoFamiliar}" var="fam">
                        <tr>
                            <td><strong><c:out value="${fam.dni}"/></strong></td>
                            <td><c:out value="${fam.nombre}"/> <c:out value="${fam.apellido}"/></td>
                            <td><c:out value="${fam.email}"/></td>
                            <td><c:out value="${fam.telefonoCelular}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${empty fam.titularId}">
                                        <span class="status-badge status-paid">Titular de Cuenta</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-badge status-pending">Familiar a Cargo</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Formulario para agregar nuevo familiar -->
        <div style="background-color: #f8fafc; border: 1px solid var(--color-border); border-radius: var(--radius-sm); padding: 1.25rem; margin-top: 1.5rem;">
            <h4 style="font-size: 1rem; color: var(--color-primary); font-weight: 700; margin-bottom: 1rem;">
                ➕ Agregar nuevo integrante a mi grupo familiar
            </h4>

            <form action="${pageContext.request.contextPath}/familia/agregar" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="form-grid">
                    <div class="form-group">
                        <label for="dni">DNI del familiar (7 u 8 dígitos)</label>
                        <input type="text" id="dni" name="dni" class="form-control" placeholder="Ej: 42123456" pattern="[0-9]{7,8}" required />
                    </div>

                    <div class="form-group">
                        <label for="nombre">Nombre</label>
                        <input type="text" id="nombre" name="nombre" class="form-control" required />
                    </div>

                    <div class="form-group">
                        <label for="apellido">Apellido</label>
                        <input type="text" id="apellido" name="apellido" class="form-control" required />
                    </div>

                    <div class="form-group">
                        <label for="direccion">Dirección</label>
                        <input type="text" id="direccion" name="direccion" class="form-control" value="<c:out value='${turista.direccion}'/>" required />
                    </div>

                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" class="form-control" required />
                    </div>

                    <div class="form-group">
                        <label for="telefonoCelular">Celular</label>
                        <input type="tel" id="telefonoCelular" name="telefonoCelular" class="form-control" required />
                    </div>
                </div>

                <button type="submit" class="btn-primary">
                    Guardar Familiar
                </button>
            </form>
        </div>
    </section>
</c:if>

<jsp:include page="comun/footer.jsp" />

