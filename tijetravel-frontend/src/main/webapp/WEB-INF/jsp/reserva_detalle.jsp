<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Comprobante de Reserva" />
<jsp:include page="comun/header.jsp" />

<div style="max-width: 800px; margin: 1.5rem auto;">
    <c:if test="${not empty exitoMensaje}">
        <div class="alert alert-success" style="font-size: 1rem; font-weight: 600;">
            ✅ <c:out value="${exitoMensaje}"/>
        </div>
    </c:if>

    <div class="card" style="border: 2px solid var(--color-border); box-shadow: var(--shadow-md);">
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid var(--color-primary); padding-bottom: 1rem; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
            <div>
                <h1 style="font-size: 1.6rem; color: var(--color-primary); font-weight: 800;">
                    Voucher de Viaje — Tije Travel
                </h1>
                <p style="color: var(--color-text-muted); font-size: 0.9rem;">
                    Código de Reserva: <strong><c:out value="${reserva.codigo}"/></strong>
                </p>
            </div>

            <div>
                <c:choose>
                    <c:when test="${reserva.estado eq 'PAGADO'}">
                        <span class="status-badge status-paid" style="font-size: 1rem; padding: 0.4rem 1rem;">
                            ✔ PAGADO
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="status-badge status-pending" style="font-size: 1rem; padding: 0.4rem 1rem;">
                            ⏳ PENDIENTE
                        </span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- 1. Datos del Pasajero y Sucursal -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.25rem; margin-bottom: 1.5rem; background-color: #f8fafc; padding: 1.25rem; border-radius: var(--radius-sm);">
            <div>
                <span style="font-size: 0.8rem; color: var(--color-text-muted); font-weight: 600; text-transform: uppercase;">Pasajero Titular</span>
                <div style="font-size: 1.05rem; font-weight: 700; color: var(--color-primary);"><c:out value="${reserva.turistaNombreCompleto}"/></div>
                <div style="font-size: 0.85rem; color: var(--color-text);">DNI: <c:out value="${reserva.turistaDni}"/></div>
            </div>

            <div>
                <span style="font-size: 0.8rem; color: var(--color-text-muted); font-weight: 600; text-transform: uppercase;">Sucursal de Contratación (Dato Histórico)</span>
                <div style="font-size: 0.95rem; font-weight: 600; color: var(--color-text);"><c:out value="${reserva.sucursalContratacion}"/></div>
            </div>

            <div>
                <span style="font-size: 0.8rem; color: var(--color-text-muted); font-weight: 600; text-transform: uppercase;">Fecha y Hora de Pago</span>
                <div style="font-size: 0.95rem; font-weight: 600; color: var(--color-text);">
                    <c:choose>
                        <c:when test="${not empty reserva.fechaPago}">
                            <c:out value="${reserva.fechaPago}"/>
                        </c:when>
                        <c:otherwise>
                            <em>Pendiente de acreditación</em>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- 2. Detalle de los Servicios Capturados -->
        <h3 style="font-size: 1.15rem; color: var(--color-primary); font-weight: 700; margin-bottom: 0.75rem;">
            Servicios Contratados
        </h3>

        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Concepto</th>
                        <th>Detalle</th>
                        <th>Modalidad</th>
                        <th style="text-align: right;">Importe Capturado</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><strong>Transporte Aéreo</strong></td>
                        <td>
                            Vuelo <strong><c:out value="${reserva.vueloNumero}"/></strong><br>
                            <small><c:out value="${reserva.vueloOrigen}"/> ➔ <c:out value="${reserva.vueloDestino}"/></small><br>
                            <small>Fecha: <c:out value="${reserva.vueloFechaHora}"/></small>
                        </td>
                        <td><span class="status-badge status-paid"><c:out value="${reserva.claseVuelo}"/></span></td>
                        <td style="text-align: right; font-weight: 600;">
                            $<fmt:formatNumber value="${reserva.precioVueloCapturado}" type="number" groupingUsed="true"/>
                        </td>
                    </tr>
                    <tr>
                        <td><strong>Hospedaje</strong></td>
                        <td>
                            Hotel <strong><c:out value="${reserva.hotelNombre}"/></strong> (<c:out value="${reserva.hotelCodigo}"/>)<br>
                            <small><c:out value="${reserva.hotelCiudad}"/></small><br>
                            <small>Estadía: <c:out value="${reserva.fechaLlegada}"/> al <c:out value="${reserva.fechaPartida}"/> (<strong><c:out value="${reserva.noches}"/></strong> noches)</small>
                        </td>
                        <td><span class="status-badge status-paid"><c:out value="${reserva.tipoHospedaje}"/></span></td>
                        <td style="text-align: right; font-weight: 600;">
                            $<fmt:formatNumber value="${reserva.precioHotelCapturado}" type="number" groupingUsed="true"/> / noche
                            <div style="font-size: 0.8rem; color: var(--color-text-muted);">
                                Subtotal (<c:out value="${reserva.noches}"/> noches):
                                $<fmt:formatNumber value="${reserva.precioTotal - reserva.precioVueloCapturado}" type="number" groupingUsed="true"/>
                            </div>
                        </td>
                    </tr>
                </tbody>
                <tfoot>
                    <tr style="background-color: #f8fafc; font-size: 1.15rem;">
                        <th colspan="3" style="text-align: right; font-weight: 700; color: var(--color-primary); padding: 1rem;">
                            TOTAL FINAL CONTRATADO:
                        </th>
                        <th style="text-align: right; font-weight: 800; color: var(--color-accent); padding: 1rem;">
                            $<fmt:formatNumber value="${reserva.precioTotal}" type="number" groupingUsed="true"/>
                        </th>
                    </tr>
                </tfoot>
            </table>
        </div>

        <div style="margin-top: 1.5rem; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
            <a href="${pageContext.request.contextPath}/reservas/mis-reservas" class="btn-primary">
                ← Volver a Mis Reservas
            </a>

            <button type="button" onclick="window.print()" class="btn-search" style="height: auto; padding: 0.6rem 1.25rem; font-size: 0.95rem;">
                🖨️ Imprimir Comprobante
            </button>
        </div>
    </div>
</div>

<jsp:include page="comun/footer.jsp" />

