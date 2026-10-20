<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Mis Reservas" />
<jsp:include page="comun/header.jsp" />

<div class="card" style="margin-bottom: 2rem;">
    <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
        <div>
            <h2 class="section-title" style="margin-bottom: 0.25rem;">📋 Reservas de mi Grupo Familiar</h2>
            <p style="color: var(--color-text-muted); font-size: 0.95rem;">
                Historial completo de viajes contratados para usted y los integrantes de su familia.
            </p>
        </div>

        <div>
            <a href="${pageContext.request.contextPath}/" class="btn-reserve" style="font-size: 0.95rem;">
                + Buscar Nuevo Viaje
            </a>
        </div>
    </div>
</div>

<div class="table-container">
    <table class="data-table">
        <thead>
            <tr>
                <th>Código Reserva</th>
                <th>Pasajero</th>
                <th>Vuelo</th>
                <th>Hotel</th>
                <th>Fechas</th>
                <th>Total Capturado</th>
                <th>Estado</th>
                <th>Acción</th>
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
                                <small style="color: var(--color-text-muted);">DNI: <c:out value="${res.turistaDni}"/></small>
                            </td>
                            <td>
                                <strong><c:out value="${res.vueloNumero}"/></strong> (<c:out value="${res.claseVuelo}"/>)<br>
                                <small><c:out value="${res.vueloOrigen}"/> ➔ <c:out value="${res.vueloDestino}"/></small>
                            </td>
                            <td>
                                <strong><c:out value="${res.hotelNombre}"/></strong><br>
                                <small><c:out value="${res.hotelCiudad}"/></small>
                            </td>
                            <td>
                                <c:out value="${res.fechaLlegada}"/> al <c:out value="${res.fechaPartida}"/><br>
                                <small style="color: var(--color-text-muted);">(<c:out value="${res.noches}"/> noches)</small>
                            </td>
                            <td>
                                <strong>$<fmt:formatNumber value="${res.precioTotal}" type="number" groupingUsed="true"/></strong>
                            </td>
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
                                <c:choose>
                                    <c:when test="${res.estado eq 'PENDIENTE'}">
                                        <a href="${pageContext.request.contextPath}/reservas/<c:out value='${res.id}'/>/procesando-pago"
                                           class="btn-reserve" style="font-size: 0.8rem; padding: 0.35rem 0.75rem;">
                                            Pagar Ahora
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/reservas/<c:out value='${res.id}'/>/detalle"
                                           class="btn-primary" style="font-size: 0.8rem; padding: 0.35rem 0.75rem;">
                                            Ver Voucher
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="8" style="text-align: center; color: var(--color-text-muted); padding: 2rem;">
                            No posee reservas registradas hasta el momento. ¡Utilice el buscador para planificar su viaje!
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<jsp:include page="comun/footer.jsp" />

