<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Completar Reserva" />
<jsp:include page="comun/header.jsp" />

<div style="max-width: 750px; margin: 1.5rem auto;">
    <div class="card">
        <h2 style="font-size: 1.5rem; font-weight: 700; color: var(--color-primary); margin-bottom: 0.5rem;">
            ✈️ Formulario de Contratación de Reserva
        </h2>
        <p style="color: var(--color-text-muted); font-size: 0.9rem; margin-bottom: 1.5rem;">
            Revise los datos de su viaje, seleccione al pasajero y configure las preferencias de vuelo y hospedaje.
        </p>

        <c:if test="${not empty errorValidacion}">
            <div class="alert alert-danger">
                <strong>Error:</strong> <c:out value="${errorValidacion}"/>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/reservas/nueva" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <!-- Datos precargados del vuelo y hotel -->
            <div style="background-color: #f1f5f9; padding: 1rem; border-radius: var(--radius-sm); margin-bottom: 1.5rem;">
                <h3 style="font-size: 1rem; color: var(--color-primary); font-weight: 700; margin-bottom: 0.5rem;">
                    Resumen de Itinerario Precargado
                </h3>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; font-size: 0.9rem;">
                    <div>Vuelo Seleccionado: <strong><c:out value="${vueloNumero}"/></strong></div>
                    <div>Hotel Seleccionado: <strong><c:out value="${hotelCodigo}"/></strong></div>
                    <div>Fecha Llegada (Ida): <strong><c:out value="${fechaLlegada}"/></strong></div>
                    <div>Fecha Salida (Hotel): <strong><c:out value="${fechaPartida}"/></strong></div>
                </div>

                <input type="hidden" name="vueloNumero" value="<c:out value='${vueloNumero}'/>" />
                <input type="hidden" name="hotelCodigo" value="<c:out value='${hotelCodigo}'/>" />
                <input type="hidden" name="fechaLlegada" value="<c:out value='${fechaLlegada}'/>" />
                <input type="hidden" name="fechaPartida" value="<c:out value='${fechaPartida}'/>" />
            </div>

            <div class="form-grid">
                <!-- Selección de pasajero -->
                <div class="form-group" style="grid-column: 1 / -1;">
                    <label for="turistaDni">Pasajero de la Reserva (1 reserva = 1 persona)</label>
                    <c:choose>
                        <c:when test="${not empty miembrosFamilia}">
                            <select id="turistaDni" name="turistaDni" class="form-control" required>
                                <c:forEach items="${miembrosFamilia}" var="miembro">
                                    <option value="<c:out value='${miembro.dni}'/>">
                                        <c:out value="${miembro.nombre}"/> <c:out value="${miembro.apellido}"/> (DNI: <c:out value="${miembro.dni}"/>)
                                    </option>
                                </c:forEach>
                            </select>
                        </c:when>
                        <c:otherwise>
                            <input type="text" id="turistaDni" name="turistaDni" class="form-control"
                                   placeholder="Ingrese DNI del turista (7 u 8 dígitos)" pattern="[0-9]{7,8}" required />
                        </c:otherwise>
                    </c:choose>
                    <small style="color: var(--color-text-muted); margin-top: 0.25rem;">
                        En viajes familiares, cada integrante cuenta con su propia reserva individual vinculada al vuelo y hotel.
                    </small>
                </div>

                <!-- Clase de vuelo -->
                <div class="form-group">
                    <label for="claseVuelo">Clase de Vuelo</label>
                    <select id="claseVuelo" name="claseVuelo" class="form-control" required>
                        <option value="TURISTA">Clase Turista (Tarifa estándar)</option>
                        <option value="PRIMERA">Primera Clase (+10% sobre tarifa de vuelo)</option>
                    </select>
                </div>

                <!-- Tipo de hospedaje -->
                <div class="form-group">
                    <label for="tipoHospedaje">Tipo de Hospedaje</label>
                    <select id="tipoHospedaje" name="tipoHospedaje" class="form-control" required>
                        <option value="ESTANDAR">Hospedaje Estándar (Tarifa base)</option>
                        <option value="COMPLETA">Pensión Completa (+5% sobre tarifa de hotel)</option>
                    </select>
                </div>
            </div>

            <div class="alert alert-info" style="font-size: 0.85rem; margin-top: 1rem;">
                ℹ️ <strong>Importante:</strong> Los precios capturados al momento de confirmar quedarán inmutables en su reserva. Si los precios del catálogo cambian con posterioridad, su reserva mantendrá la tarifa acordada.
            </div>

            <div style="margin-top: 1.5rem; display: flex; justify-content: space-between; align-items: center;">
                <a href="${pageContext.request.contextPath}/" style="color: var(--color-text-muted); text-decoration: none;">
                    ← Volver al buscador
                </a>
                <button type="submit" class="btn-reserve" style="font-size: 1.05rem; padding: 0.75rem 2rem;">
                    Confirmar y Proceder al Pago
                </button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="comun/footer.jsp" />

