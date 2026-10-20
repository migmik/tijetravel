<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Redirección automática de 4 segundos sin JavaScript según especificación -->
    <meta http-equiv="refresh" content="4;url=${pageContext.request.contextPath}/reservas/<c:out value='${reserva.id}'/>/confirmar-pago" />
    <title>Procesando Pago Seguro - Tije Travel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body style="background-color: #f1f5f9;">

<div class="payment-processing-card">
    <div class="spinner-css"></div>

    <h2 style="font-size: 1.6rem; font-weight: 800; color: var(--color-primary); margin-bottom: 0.5rem;">
        Procesando Pago Seguro...
    </h2>

    <p style="color: var(--color-text-muted); font-size: 0.95rem; margin-bottom: 1.5rem;">
        Por favor espere mientras autorizamos la operación. Esta ventana se actualizará automáticamente en 4 segundos.
    </p>

    <!-- Barra de progreso animada con puro CSS (sin JavaScript) -->
    <div class="progress-bar-container">
        <div class="progress-bar-fill"></div>
    </div>

    <div style="background-color: #f8fafc; border: 1px solid var(--color-border); border-radius: var(--radius-sm); padding: 1rem; text-align: left; font-size: 0.9rem; margin-bottom: 1.5rem;">
        <div>Reserva N°: <strong><c:out value="${reserva.codigo}"/></strong></div>
        <div>Pasajero: <strong><c:out value="${reserva.turistaNombreCompleto}"/></strong> (DNI: <c:out value="${reserva.turistaDni}"/>)</div>
        <div>Total a Abonar: <strong>$<fmt:formatNumber value="${reserva.precioTotal}" type="number" groupingUsed="true"/></strong></div>
    </div>

    <p style="font-size: 0.8rem; color: #94a3b8; margin-bottom: 1rem;">
        🔒 Simulación académica de pago electrónico: no se debitará dinero real de ninguna cuenta o tarjeta.
    </p>

    <p style="font-size: 0.85rem;">
        Si la página no redirige automáticamente:
        <a href="${pageContext.request.contextPath}/reservas/<c:out value='${reserva.id}'/>/confirmar-pago" style="color: var(--color-accent); font-weight: 600;">
            Haga clic aquí para continuar
        </a>
    </p>
</div>

</body>
</html>

