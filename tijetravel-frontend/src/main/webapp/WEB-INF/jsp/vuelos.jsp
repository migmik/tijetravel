<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Explorar Vuelos" />
<jsp:include page="comun/header.jsp" />

<div class="card">
    <h2 class="section-title">🛫 Explorar Vuelos</h2>
    <p style="color: var(--color-text-muted); margin-bottom: 1.5rem;">
        Consulte la programación de vuelos disponibles entre los principales destinos del país.
    </p>

    <!-- Filtros compactos horizontales -->
    <form action="${pageContext.request.contextPath}/vuelos" method="get">
        <div class="search-form-row">
            <div class="form-group-compact">
                <label for="origen">Origen</label>
                <select id="origen" name="origen" class="form-control">
                    <option value="">Todos los orígenes</option>
                    <c:forEach items="${ciudadesOrigen}" var="ciu">
                        <option value="<c:out value='${ciu}'/>" <c:if test="${filtroOrigen eq ciu}">selected</c:if>>
                            <c:out value="${ciu}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group-compact">
                <label for="destino">Destino</label>
                <select id="destino" name="destino" class="form-control">
                    <option value="">Todos los destinos</option>
                    <c:forEach items="${ciudadesDestino}" var="ciu">
                        <option value="<c:out value='${ciu}'/>" <c:if test="${filtroDestino eq ciu}">selected</c:if>>
                            <c:out value="${ciu}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group-compact" style="flex: 0 1 180px;">
                <label for="orden">Tarifa Base</label>
                <select id="orden" name="orden" class="form-control">
                    <option value="ASC" <c:if test="${filtroOrden eq 'ASC'}">selected</c:if>>Precio: Menor a Mayor</option>
                    <option value="DESC" <c:if test="${filtroOrden eq 'DESC'}">selected</c:if>>Precio: Mayor a Menor</option>
                </select>
            </div>

            <div>
                <button type="submit" class="btn-primary" style="height: 42px;">Filtrar Vuelos</button>
            </div>
        </div>
    </form>
</div>

<c:choose>
    <c:when test="${not empty paginaVuelos}">
        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>N° Vuelo</th>
                        <th>Fecha y Hora</th>
                        <th>Origen</th>
                        <th>Destino</th>
                        <th>Cupo Turista</th>
                        <th>Cupo Primera</th>
                        <th>Precio Base</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${paginaVuelos.content}" var="vuelo">
                        <tr>
                            <td><strong><c:out value="${vuelo.numero}"/></strong></td>
                            <td><c:out value="${vuelo.fechaHora}"/></td>
                            <td><c:out value="${vuelo.origen}"/></td>
                            <td><c:out value="${vuelo.destino}"/></td>
                            <td><c:out value="${vuelo.plazasTuristaDisponibles}"/> / <c:out value="${vuelo.cupoTurista}"/></td>
                            <td><c:out value="${vuelo.plazasPrimeraDisponibles}"/> / <c:out value="${vuelo.cupoPrimera}"/></td>
                            <td><strong>$<fmt:formatNumber value="${vuelo.precioBase}" type="number" groupingUsed="true"/></strong></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:when>
    <c:otherwise>
        <div class="alert alert-info">
            Inicie sesión con su cuenta para acceder al catálogo detallado de vuelos o realice una búsqueda en la página inicial.
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="comun/footer.jsp" />

