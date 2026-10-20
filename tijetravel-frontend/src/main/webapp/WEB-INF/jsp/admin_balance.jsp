<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Balance Financiero de Ventas" />
<jsp:include page="comun/header.jsp" />

<div class="card" style="margin-bottom: 2rem;">
    <h2 class="section-title">📊 Balance Financiero y Métricas de Ventas</h2>
    <p style="color: var(--color-text-muted); font-size: 0.95rem;">
        Métricas agregadas directamente en base de datos sobre precios históricos capturados. Las reservas pendientes se informan por separado como potencial de venta.
    </p>
</div>

<!-- Tarjetas de Balance -->
<div class="balance-grid">
    <!-- Reservas Pagadas (Ventas Reales Concretadas) -->
    <div class="balance-card paid">
        <div class="balance-title">Ventas Concretadas (Reservas Pagadas)</div>
        <div class="balance-amount" style="color: var(--color-success);">
            $<fmt:formatNumber value="${balance.pagadas.total}" type="number" groupingUsed="true"/>
        </div>
        <p style="font-size: 0.9rem; color: var(--color-text); margin-bottom: 0.75rem;">
            Cantidad de Pasajes Vendidos: <strong><c:out value="${balance.pagadas.cantidad}"/></strong>
        </p>
        <div class="balance-breakdown">
            <span>✈️ Recaudación Vuelos:</span>
            <strong>$<fmt:formatNumber value="${balance.pagadas.totalVuelos}" type="number" groupingUsed="true"/></strong>
        </div>
        <div class="balance-breakdown" style="border: none; padding-top: 0.25rem;">
            <span>🏨 Recaudación Hoteles:</span>
            <strong>$<fmt:formatNumber value="${balance.pagadas.totalHoteles}" type="number" groupingUsed="true"/></strong>
        </div>
    </div>

    <!-- Reservas Pendientes (Importe Potencial) -->
    <div class="balance-card pending">
        <div class="balance-title">Potencial de Venta (Reservas Pendientes)</div>
        <div class="balance-amount" style="color: var(--color-warning);">
            $<fmt:formatNumber value="${balance.pendientes.total}" type="number" groupingUsed="true"/>
        </div>
        <p style="font-size: 0.9rem; color: var(--color-text); margin-bottom: 0.75rem;">
            Cantidad de Reservas en Espera: <strong><c:out value="${balance.pendientes.cantidad}"/></strong>
        </p>
        <div class="balance-breakdown">
            <span>✈️ Potencial Vuelos:</span>
            <strong>$<fmt:formatNumber value="${balance.pendientes.totalVuelos}" type="number" groupingUsed="true"/></strong>
        </div>
        <div class="balance-breakdown" style="border: none; padding-top: 0.25rem;">
            <span>🏨 Potencial Hoteles:</span>
            <strong>$<fmt:formatNumber value="${balance.pendientes.totalHoteles}" type="number" groupingUsed="true"/></strong>
        </div>
    </div>
</div>

<div class="card" style="background-color: #f8fafc; border-left: 4px solid var(--color-primary);">
    <h3 style="font-size: 1rem; font-weight: 700; color: var(--color-primary); margin-bottom: 0.5rem;">
        Criterios Contables y de Auditoría
    </h3>
    <ul style="margin-left: 1.5rem; font-size: 0.875rem; color: var(--color-text-muted); line-height: 1.6;">
        <li>Las reservas en estado <strong>PENDIENTE</strong> no se consideran vendidas hasta que el pago haya sido acreditado.</li>
        <li>Los totales se calculan sobre los precios históricos capturados al momento exacto de la reserva; futuras modificaciones de tarifas de catálogo no alteran estos importes.</li>
        <li>El desglose hotelero contempla la estadía por persona por noche más los adicionales por pensión completa.</li>
    </ul>
</div>

<jsp:include page="comun/footer.jsp" />

