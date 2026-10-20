<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Gestión del Sistema" />
<jsp:include page="comun/header.jsp" />

<div class="card" style="margin-bottom: 1.5rem;">
    <h2 class="section-title">⚙️ Panel de Gestión de Catálogo y Entidades</h2>
    <p style="color: var(--color-text-muted); font-size: 0.95rem; margin-bottom: 1.5rem;">
        Administración de vuelos, hoteles, sucursales y turistas de Tije Travel.
    </p>

    <!-- Pestañas de navegación -->
    <div style="display: flex; gap: 0.5rem; border-bottom: 2px solid var(--color-border); padding-bottom: 0.5rem; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/gestion?seccion=vuelos"
           class="btn-primary" style="background-color: <c:out value="${seccionActiva eq 'vuelos' ? 'var(--color-primary)' : '#64748b'}"/>;">
            🛫 Vuelos
        </a>
        <a href="${pageContext.request.contextPath}/gestion?seccion=hoteles"
           class="btn-primary" style="background-color: <c:out value="${seccionActiva eq 'hoteles' ? 'var(--color-primary)' : '#64748b'}"/>;">
            🏨 Hoteles
        </a>
        <a href="${pageContext.request.contextPath}/gestion?seccion=sucursales"
           class="btn-primary" style="background-color: <c:out value="${seccionActiva eq 'sucursales' ? 'var(--color-primary)' : '#64748b'}"/>;">
            📍 Sucursales
        </a>
        <a href="${pageContext.request.contextPath}/gestion?seccion=turistas"
           class="btn-primary" style="background-color: <c:out value="${seccionActiva eq 'turistas' ? 'var(--color-primary)' : '#64748b'}"/>;">
            👥 Turistas
        </a>
    </div>
</div>

<!-- ================= SECCION VUELOS ================= -->
<c:if test="${seccionActiva eq 'vuelos'}">
    <div class="card" style="margin-bottom: 2rem; background-color: #f8fafc;">
        <h3 style="font-size: 1.15rem; color: var(--color-primary); font-weight: 700; margin-bottom: 1rem;">
            ➕ Alta de Nuevo Vuelo
        </h3>

        <form action="${pageContext.request.contextPath}/gestion/vuelos/crear" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <div class="form-grid">
                <div class="form-group">
                    <label for="numero">Número de Vuelo</label>
                    <input type="text" id="numero" name="numero" class="form-control" placeholder="Ej: TJ-601" required />
                </div>

                <div class="form-group">
                    <label for="fechaHora">Fecha y Hora</label>
                    <input type="datetime-local" id="fechaHora" name="fechaHora" class="form-control" required />
                </div>

                <div class="form-group">
                    <label for="origen">Origen</label>
                    <input type="text" id="origen" name="origen" class="form-control" placeholder="Ciudad origen" required />
                </div>

                <div class="form-group">
                    <label for="destino">Destino (Distinto a origen)</label>
                    <input type="text" id="destino" name="destino" class="form-control" placeholder="Ciudad destino" required />
                </div>

                <div class="form-group">
                    <label for="asientosTotales">Asientos Totales</label>
                    <input type="number" id="asientosTotales" name="asientosTotales" class="form-control" min="1" value="150" required />
                </div>

                <div class="form-group">
                    <label for="cupoTurista">Cupo Turista</label>
                    <input type="number" id="cupoTurista" name="cupoTurista" class="form-control" min="0" value="120" required />
                </div>

                <div class="form-group">
                    <label for="cupoPrimera">Cupo Primera</label>
                    <input type="number" id="cupoPrimera" name="cupoPrimera" class="form-control" min="0" value="30" required />
                </div>

                <div class="form-group">
                    <label for="precioBase">Precio Base ($150.000-$300.000, múlt. de $10.000)</label>
                    <input type="number" id="precioBase" name="precioBase" class="form-control"
                           min="150000" max="300000" step="10000" value="180000" required />
                </div>
            </div>

            <button type="submit" class="btn-primary">Registrar Vuelo</button>
        </form>
    </div>

    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>N° Vuelo</th>
                    <th>Fecha y Hora</th>
                    <th>Ruta</th>
                    <th>Asientos</th>
                    <th>Cupo Turista</th>
                    <th>Cupo Primera</th>
                    <th>Precio Base</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${datosPagina.content}" var="v">
                    <tr>
                        <td><strong><c:out value="${v.numero}"/></strong></td>
                        <td><c:out value="${v.fechaHora}"/></td>
                        <td><c:out value="${v.origen}"/> ➔ <c:out value="${v.destino}"/></td>
                        <td><c:out value="${v.asientosTotales}"/></td>
                        <td><c:out value="${v.cupoTurista}"/></td>
                        <td><c:out value="${v.cupoPrimera}"/></td>
                        <td>$<fmt:formatNumber value="${v.precioBase}" type="number" groupingUsed="true"/></td>
                        <td>
                            <form action="${pageContext.request.contextPath}/gestion/vuelos/<c:out value='${v.id}'/>/eliminar" method="post" style="display:inline;">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" class="btn-danger" onclick="return confirm('¿Eliminar vuelo?')">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<!-- ================= SECCION HOTELES ================= -->
<c:if test="${seccionActiva eq 'hoteles'}">
    <div class="card" style="margin-bottom: 2rem; background-color: #f8fafc;">
        <h3 style="font-size: 1.15rem; color: var(--color-primary); font-weight: 700; margin-bottom: 1rem;">
            ➕ Alta de Nuevo Hotel
        </h3>

        <form action="${pageContext.request.contextPath}/gestion/hoteles/crear" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <div class="form-grid">
                <div class="form-group">
                    <label for="codigo">Código de Hotel</label>
                    <input type="text" id="codigo" name="codigo" class="form-control" placeholder="Ej: HOT-BUE-05" required />
                </div>

                <div class="form-group">
                    <label for="nombre">Nombre</label>
                    <input type="text" id="nombre" name="nombre" class="form-control" placeholder="Nombre único en ciudad" required />
                </div>

                <div class="form-group">
                    <label for="ciudad">Ciudad</label>
                    <input type="text" id="ciudad" name="ciudad" class="form-control" placeholder="Ciudad" required />
                </div>

                <div class="form-group">
                    <label for="direccion">Dirección</label>
                    <input type="text" id="direccion" name="direccion" class="form-control" required />
                </div>

                <div class="form-group">
                    <label for="telefono">Teléfono</label>
                    <input type="text" id="telefono" name="telefono" class="form-control" required />
                </div>

                <div class="form-group">
                    <label for="capacidadTotal">Capacidad Total (Personas)</label>
                    <input type="number" id="capacidadTotal" name="capacidadTotal" class="form-control" min="1" value="80" required />
                </div>

                <div class="form-group">
                    <label for="precioBaseNoche">Tarifa Base / Noche ($25.000-$55.000, múlt. de $1.000)</label>
                    <input type="number" id="precioBaseNoche" name="precioBaseNoche" class="form-control"
                           min="25000" max="55000" step="1000" value="35000" required />
                </div>
            </div>

            <button type="submit" class="btn-primary">Registrar Hotel</button>
        </form>
    </div>

    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Código</th>
                    <th>Nombre</th>
                    <th>Ciudad</th>
                    <th>Dirección</th>
                    <th>Capacidad</th>
                    <th>Precio / Noche</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${datosPagina.content}" var="h">
                    <tr>
                        <td><strong><c:out value="${h.codigo}"/></strong></td>
                        <td><c:out value="${h.nombre}"/></td>
                        <td><c:out value="${h.ciudad}"/></td>
                        <td><c:out value="${h.direccion}"/></td>
                        <td><c:out value="${h.capacidadTotal}"/> personas</td>
                        <td>$<fmt:formatNumber value="${h.precioBaseNoche}" type="number" groupingUsed="true"/></td>
                        <td>
                            <form action="${pageContext.request.contextPath}/gestion/hoteles/<c:out value='${h.id}'/>/eliminar" method="post" style="display:inline;">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" class="btn-danger" onclick="return confirm('¿Eliminar hotel?')">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<!-- ================= SECCION SUCURSALES ================= -->
<c:if test="${seccionActiva eq 'sucursales'}">
    <div class="card" style="margin-bottom: 2rem; background-color: #f8fafc;">
        <h3 style="font-size: 1.15rem; color: var(--color-primary); font-weight: 700; margin-bottom: 1rem;">
            ➕ Alta de Nueva Sucursal
        </h3>

        <form action="${pageContext.request.contextPath}/gestion/sucursales/crear" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <div class="form-grid">
                <div class="form-group">
                    <label for="codigo">Código Sucursal</label>
                    <input type="text" id="codigo" name="codigo" class="form-control" placeholder="Ej: SUC-MDQ" required />
                </div>

                <div class="form-group">
                    <label for="ciudad">Ciudad</label>
                    <input type="text" id="ciudad" name="ciudad" class="form-control" placeholder="Ciudad" required />
                </div>

                <div class="form-group">
                    <label for="direccion">Dirección</label>
                    <input type="text" id="direccion" name="direccion" class="form-control" placeholder="Dirección" required />
                </div>

                <div class="form-group">
                    <label for="telefono">Teléfono</label>
                    <input type="text" id="telefono" name="telefono" class="form-control" placeholder="Teléfono" required />
                </div>
            </div>

            <button type="submit" class="btn-primary">Registrar Sucursal</button>
        </form>
    </div>

    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Código</th>
                    <th>Ciudad</th>
                    <th>Dirección</th>
                    <th>Teléfono</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${sucursales}" var="s">
                    <tr>
                        <td><strong><c:out value="${s.codigo}"/></strong></td>
                        <td><c:out value="${s.ciudad}"/></td>
                        <td><c:out value="${s.direccion}"/></td>
                        <td><c:out value="${s.telefono}"/></td>
                        <td>
                            <form action="${pageContext.request.contextPath}/gestion/sucursales/<c:out value='${s.id}'/>/eliminar" method="post" style="display:inline;">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" class="btn-danger" onclick="return confirm('¿Eliminar sucursal?')">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<!-- ================= SECCION TURISTAS ================= -->
<c:if test="${seccionActiva eq 'turistas'}">
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Código</th>
                    <th>DNI</th>
                    <th>Nombre Completo</th>
                    <th>Email</th>
                    <th>Celular</th>
                    <th>Sucursal</th>
                    <th>Titular Familiar</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${datosPagina.content}" var="t">
                    <tr>
                        <td><strong><c:out value="${t.codigo}"/></strong></td>
                        <td><c:out value="${t.dni}"/></td>
                        <td><c:out value="${t.nombre}"/> <c:out value="${t.apellido}"/></td>
                        <td><c:out value="${t.email}"/></td>
                        <td><c:out value="${t.telefonoCelular}"/></td>
                        <td><c:out value="${t.sucursalCodigo}"/> (<c:out value="${t.sucursalCiudad}"/>)</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty t.titularNombreCompleto}">
                                    <c:out value="${t.titularNombreCompleto}"/>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-paid">Titular</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<jsp:include page="comun/footer.jsp" />

