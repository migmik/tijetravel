<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Resultados de Búsqueda" />
<jsp:include page="comun/header.jsp" />

<div class="card" style="margin-bottom: 2rem;">
    <h2 style="font-size: 1.25rem; font-weight: 700; color: var(--color-primary); margin-bottom: 0.75rem;">
        Búsqueda: <c:out value="${busquedaForm.origen}"/> ➔ <c:out value="${busquedaForm.destino}"/>
    </h2>
    <p style="color: var(--color-text-muted); font-size: 0.95rem;">
        Llegada: <strong><c:out value="${busquedaForm.fechaLlegada}"/></strong> |
        Salida: <strong><c:out value="${busquedaForm.fechaPartida}"/></strong> |
        Viajeros: <strong><c:out value="${busquedaForm.personas}"/></strong> persona(s)
    </p>
</div>

<!-- 1. PAQUETES COMBINADOS (VUELO + HOTEL) -->
<section style="margin-bottom: 2.5rem;">
    <h2 class="section-title">✨ Paquetes Recomendados (Vuelo + Hotel)</h2>

    <c:choose>
        <c:when test="${not empty resultado.paquetes}">
            <c:forEach items="${resultado.paquetes}" var="paquete">
                <div class="package-card">
                    <div class="package-info">
                        <span class="package-badge">Paquete Completo</span>
                        <h3 class="package-title">
                            Vuelo <c:out value="${paquete.vuelo.numero}"/> + Hotel <c:out value="${paquete.hotel.nombre}"/>
                        </h3>
                        <p class="package-details">
                            🛫 Salida: <strong><c:out value="${paquete.vuelo.origen}"/></strong> ➔ <strong><c:out value="${paquete.vuelo.destino}"/></strong> (<c:out value="${paquete.vuelo.fechaHora}"/>)<br>
                            🏨 Estadía: <strong><c:out value="${paquete.noches}"/></strong> noche(s) en <c:out value="${paquete.hotel.ciudad}"/> (<c:out value="${paquete.hotel.direccion}"/>)
                        </p>
                    </div>

                    <div class="package-price-box">
                        <div class="price-sub">Por persona desde</div>
                        <div class="price-tag">
                            $<fmt:formatNumber value="${paquete.precioPorPersona}" type="number" groupingUsed="true" maxFractionDigits="0"/>
                        </div>
                        <div class="price-sub">
                            Total grupo (<c:out value="${busquedaForm.personas}"/> pers.):
                            $<fmt:formatNumber value="${paquete.precioTotalGrupo}" type="number" groupingUsed="true" maxFractionDigits="0"/>
                        </div>
                        <a href="${pageContext.request.contextPath}/reservas/nueva?vueloNumero=<c:out value='${paquete.vuelo.numero}'/>&hotelCodigo=<c:out value='${paquete.hotel.codigo}'/>&fechaLlegada=<c:out value='${busquedaForm.fechaLlegada}'/>&fechaPartida=<c:out value='${busquedaForm.fechaPartida}'/>&personas=<c:out value='${busquedaForm.personas}'/>"
                           class="btn-reserve">
                            Reservar Ahora
                        </a>
                    </div>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info">
                No se encontraron combinaciones completas de vuelo + hotel para los criterios exactos seleccionados.
            </div>
        </c:otherwise>
    </c:choose>
</section>

<!-- SUGERENCIAS DE VUELOS EN OTRAS FECHAS (SI NO HUBO VUELOS EN LA FECHA) -->
<c:if test="${empty resultado.vuelos and not empty resultado.sugerenciasVuelos}">
    <section style="margin-bottom: 2.5rem;">
        <div class="alert alert-info" style="border-left: 5px solid var(--color-accent);">
            <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 0.5rem; color: var(--color-primary);">
                ✈️ Vuelos sugeridos hacia <c:out value="${busquedaForm.destino}"/> en fechas cercanas
            </h3>
            <p style="margin-bottom: 1rem; font-size: 0.9rem;">
                No hay vuelos programados para el <strong><c:out value="${busquedaForm.fechaLlegada}"/></strong>. A continuación le sugerimos vuelos alternativos para este destino. Recuerde que si cambia la fecha de vuelo, el alojamiento debe volver a comprobarse.
            </p>

            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>N° Vuelo</th>
                            <th>Fecha y Hora</th>
                            <th>Ruta</th>
                            <th>Tarifa Base</th>
                            <th>Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${resultado.sugerenciasVuelos}" var="sug">
                            <tr>
                                <td><strong><c:out value="${sug.numero}"/></strong></td>
                                <td><c:out value="${sug.fechaHora}"/></td>
                                <td><c:out value="${sug.origen}"/> ➔ <c:out value="${sug.destino}"/></td>
                                <td>$<fmt:formatNumber value="${sug.precioBase}" type="number" groupingUsed="true"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/buscar?origen=<c:out value='${sug.origen}'/>&destino=<c:out value='${sug.destino}'/>&fechaLlegada=<c:out value='${sug.fechaHora.toLocalDate()}'/>&fechaPartida=<c:out value='${sug.fechaHora.toLocalDate().plusDays(3)}'/>&personas=<c:out value='${busquedaForm.personas}'/>"
                                       class="btn-primary" style="font-size: 0.8rem; padding: 0.35rem 0.75rem;">
                                        Buscar con esta fecha
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </section>
</c:if>

<!-- 2. VUELOS INDIVIDUALES DISPONIBLES -->
<section style="margin-bottom: 2.5rem;">
    <h3 style="font-size: 1.25rem; font-weight: 700; color: var(--color-primary); margin-bottom: 1rem;">
        🛫 Vuelos Disponibles para la fecha
    </h3>

    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>N° Vuelo</th>
                    <th>Horario</th>
                    <th>Origen / Destino</th>
                    <th>Plazas Turista</th>
                    <th>Plazas Primera</th>
                    <th>Precio Base</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty resultado.vuelos}">
                        <c:forEach items="${resultado.vuelos}" var="vuelo">
                            <tr>
                                <td><strong><c:out value="${vuelo.numero}"/></strong></td>
                                <td><c:out value="${vuelo.fechaHora}"/></td>
                                <td><c:out value="${vuelo.origen}"/> ➔ <c:out value="${vuelo.destino}"/></td>
                                <td><c:out value="${vuelo.plazasTuristaDisponibles}"/> disp.</td>
                                <td><c:out value="${vuelo.plazasPrimeraDisponibles}"/> disp.</td>
                                <td><strong>$<fmt:formatNumber value="${vuelo.precioBase}" type="number" groupingUsed="true"/></strong></td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="6" style="text-align: center; color: var(--color-text-muted); padding: 1.5rem;">
                                No se encontraron vuelos directos para la fecha seleccionada.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</section>

<!-- 3. HOTELES INDIVIDUALES DISPONIBLES -->
<section style="margin-bottom: 2.5rem;">
    <h3 style="font-size: 1.25rem; font-weight: 700; color: var(--color-primary); margin-bottom: 1rem;">
        🏨 Hoteles con Disponibilidad en <c:out value="${busquedaForm.destino}"/>
    </h3>

    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Código</th>
                    <th>Nombre</th>
                    <th>Dirección</th>
                    <th>Plazas Disponibles</th>
                    <th>Precio / Noche por Persona</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty resultado.hoteles}">
                        <c:forEach items="${resultado.hoteles}" var="hotel">
                            <tr>
                                <td><c:out value="${hotel.codigo}"/></td>
                                <td><strong><c:out value="${hotel.nombre}"/></strong></td>
                                <td><c:out value="${hotel.direccion}"/>, <c:out value="${hotel.ciudad}"/></td>
                                <td><c:out value="${hotel.plazasDisponibles}"/> plazas</td>
                                <td><strong>$<fmt:formatNumber value="${hotel.precioBaseNoche}" type="number" groupingUsed="true"/></strong></td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="5" style="text-align: center; color: var(--color-text-muted); padding: 1.5rem;">
                                No hay hoteles con plazas disponibles para el período seleccionado.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</section>

<jsp:include page="comun/footer.jsp" />

