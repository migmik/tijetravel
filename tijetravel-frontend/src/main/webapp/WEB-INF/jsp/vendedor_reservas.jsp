<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Gestión de Reservas" />
<jsp:include page="comun/header.jsp" />

<div class="card" style="margin-bottom: 1.5rem;">
    <h2 class="section-title">📑 Administración de Reservas</h2>
    <p style="color: var(--color-text-muted); font-size: 0.9rem; margin-bottom: 1.25rem;">
        Filtros combinables en base de datos para seguimiento y administración de reservas de clientes.
    </p>

    <!-- Filtros combinables -->
    <form action="${pageContext.request.contextPath}/reservas-admin" method="get">
        <div class="search-form-row">
            <div class="form-group-compact" style="flex: 1 1 120px;">
                <label for="dni">DNI Turista</label>
                <input type="text" id="dni" name="dni" class="form-control" value="<c:out value='${dni}'/>" placeholder="DNI..." />
            </div>

            <div class="form-group-compact" style="flex: 1 1 120px;">
                <label for="numeroVuelo">N° Vuelo</label>
                <input type="text" id="numeroVuelo" name="numeroVuelo" class="form-control" value="<c:out value='${numeroVuelo}'/>" placeholder="Ej: TJ-101" />
            </div>

            <div class="form-group-compact" style="flex: 1 1 120px;">
                <label for="codigoHotel">Cód. Hotel</label>
                <input type="text" id="codigoHotel" name="codigoHotel" class="form-control" value="<c:out value='${codigoHotel}'/>" placeholder="Ej: HOT-..." />
            </div>

            <div class="form-group-compact" style="flex: 1 1 130px;">
                <label for="fechaDesde">Llegada Desde</label>
                <input type="date" id="fechaDesde" name="fechaDesde" class="form-control" value="<c:out value='${fechaDesde}'/>" />
            </div>

            <div class="form-group-compact" style="flex: 1 1 130px;">
                <label for="fechaHasta">Llegada Hasta</label>
                <input type="date" id="fechaHasta" name="fechaHasta" class="form-control" value="<c:out value='${fechaHasta}'/>" />
            </div>

            <div class="form-group-compact" style="flex: 1 1 130px;">
                <label for="estado">Estado</label>
                <select id="estado" name="estado" class="form-control">
                    <option value="">Todos los estados</option>
                    <option value="PAGADO" <c:if test="${estado eq 'PAGADO'}">selected</c:if>>Pagadas</option>
                    <option value="PENDIENTE" <c:if test="${estado eq 'PENDIENTE'}">selected</c:if>>Pendientes</option>
                </select>
            </div>

            <div>
                <button type="submit" class="btn-primary" style="height: 42px;">Aplicar Filtros</button>
            </div>
        </div>
    </form>
</div>

<div class="table-container">
    <table class="data-table">
        <thead>
            <tr>
                <th>Código</th>
                <th>Pasajero (DNI)</th>
                <th>Vuelo</th>
                <th>Hotel</th>
                <th>Fechas</th>
                <th>Importe Capturado</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${not empty paginaReservas.content}">
                    <c:forEach items="${paginaReservas.content}" var="res">
                        <tr>
                            <td><strong><c:out value="${res.codigo}"/></strong></td>
                            <td>
                                <c:out value="${res.turistaNombreCompleto}"/><br>
                                <small>DNI: <c:out value="${res.turistaDni}"/></small>
                            </td>
                            <td>
                                <c:out value="${res.vueloNumero}"/> (<c:out value="${res.claseVuelo}"/>)<br>
                                <small><c:out value="${res.vueloOrigen}"/> ➔ <c:out value="${res.vueloDestino}"/></small>
                            </td>
                            <td>
                                <c:out value="${res.hotelNombre}"/><br>
                                <small><c:out value="${res.hotelCiudad}"/></small>
                            </td>
                            <td><c:out value="${res.fechaLlegada}"/> al <c:out value="${res.fechaPartida}"/></td>
                            <td><strong>$<fmt:formatNumber value="${res.precioTotal}" type="number" groupingUsed="true"/></strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${res.estado eq 'PAGADO'}">
                                        <span class="status-badge status-paid">PAGADO</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-badge status-pending">PENDIENTE</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div style="display: flex; gap: 0.5rem; align-items: center;">
                                    <a href="${pageContext.request.contextPath}/reservas/<c:out value='${res.id}'/>/detalle"
                                       class="btn-primary" style="font-size: 0.75rem; padding: 0.25rem 0.5rem;">
                                        Ver
                                    </a>

                                    <!-- Solo las reservas PENDIENTES pueden cancelarse -->
                                    <c:if test="${res.estado eq 'PENDIENTE'}">
                                        <form action="${pageContext.request.contextPath}/reservas-admin/<c:out value='${res.id}'/>/cancelar" method="post" style="display:inline;">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                            <button type="submit" class="btn-danger" style="font-size: 0.75rem; padding: 0.25rem 0.5rem;"
                                                    onclick="return confirm('¿Seguro que desea cancelar esta reserva pendiente?')">
                                                Cancelar
                                            </button>
                                        </form>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="8" style="text-align: center; color: var(--color-text-muted); padding: 2rem;">
                            No se encontraron reservas con los criterios especificados.
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<jsp:include page="comun/footer.jsp" />

