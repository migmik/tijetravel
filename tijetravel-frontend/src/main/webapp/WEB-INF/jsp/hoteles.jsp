<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Explorar Hoteles" />
<jsp:include page="comun/header.jsp" />

<div class="card">
    <h2 class="section-title">🏨 Explorar Hoteles</h2>
    <p style="color: var(--color-text-muted); margin-bottom: 1.5rem;">
        Conozca los hoteles y alojamientos asociados en los principales destinos del país.
    </p>

    <form action="${pageContext.request.contextPath}/hoteles" method="get">
        <div class="search-form-row">
            <div class="form-group-compact">
                <label for="ciudad">Ciudad</label>
                <select id="ciudad" name="ciudad" class="form-control">
                    <option value="">Todas las ciudades</option>
                    <c:forEach items="${ciudadesHoteles}" var="ciu">
                        <option value="<c:out value='${ciu}'/>" <c:if test="${filtroCiudad eq ciu}">selected</c:if>>
                            <c:out value="${ciu}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!-- Sugerencias de nombres sin JavaScript usando HTML5 datalist -->
            <div class="form-group-compact" style="flex: 2 1 240px;">
                <label for="nombre">Nombre del Hotel (con sugerencias)</label>
                <input type="text" id="nombre" name="nombre" list="lista-hoteles" class="form-control"
                       placeholder="Escriba o elija un hotel..." value="<c:out value='${filtroNombre}'/>" />
                <datalist id="lista-hoteles">
                    <c:forEach items="${nombresHoteles}" var="nom">
                        <option value="<c:out value='${nom}'/>" />
                    </c:forEach>
                </datalist>
            </div>

            <div class="form-group-compact" style="flex: 0 1 180px;">
                <label for="orden">Tarifa por Noche</label>
                <select id="orden" name="orden" class="form-control">
                    <option value="ASC" <c:if test="${filtroOrden eq 'ASC'}">selected</c:if>>Precio: Menor a Mayor</option>
                    <option value="DESC" <c:if test="${filtroOrden eq 'DESC'}">selected</c:if>>Precio: Mayor a Menor</option>
                </select>
            </div>

            <div>
                <button type="submit" class="btn-primary" style="height: 42px;">Filtrar Hoteles</button>
            </div>
        </div>
    </form>
</div>

<c:choose>
    <c:when test="${not empty paginaHoteles}">
        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Código</th>
                        <th>Nombre</th>
                        <th>Ciudad</th>
                        <th>Dirección</th>
                        <th>Teléfono</th>
                        <th>Capacidad Total</th>
                        <th>Precio / Noche</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${paginaHoteles.content}" var="hotel">
                        <tr>
                            <td><c:out value="${hotel.codigo}"/></td>
                            <td><strong><c:out value="${hotel.nombre}"/></strong></td>
                            <td><c:out value="${hotel.ciudad}"/></td>
                            <td><c:out value="${hotel.direccion}"/></td>
                            <td><c:out value="${hotel.telefono}"/></td>
                            <td><c:out value="${hotel.capacidadTotal}"/> personas</td>
                            <td><strong>$<fmt:formatNumber value="${hotel.precioBaseNoche}" type="number" groupingUsed="true"/></strong></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:when>
    <c:otherwise>
        <div class="alert alert-info">
            Inicie sesión con su cuenta para acceder al catálogo detallado de hoteles o realice una búsqueda en la página inicial.
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="comun/footer.jsp" />

