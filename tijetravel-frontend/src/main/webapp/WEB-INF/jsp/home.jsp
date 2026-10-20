<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Inicio - Buscador de Viajes" />
<jsp:include page="comun/header.jsp" />

<div class="hero-banner">
    <h1 class="hero-title">Encontrá tu próximo destino en Argentina</h1>
    <p class="hero-subtitle">Combiná vuelos y hoteles con las mejores tarifas para viajar solo o en familia.</p>

    <div class="search-card">
        <c:if test="${not empty errorValidacion}">
            <div class="alert alert-danger">
                <strong>Atención:</strong> <c:out value="${errorValidacion}"/>
            </div>
        </c:if>

        <c:if test="${not empty errorServicio}">
            <div class="alert alert-danger">
                <strong>Error de Servicio:</strong> <c:out value="${errorServicio}"/>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/buscar" method="get">
            <div class="search-form-row">
                <div class="form-group-compact">
                    <label for="origen">Origen</label>
                    <select id="origen" name="origen" class="form-control" required>
                        <option value="">Seleccionar ciudad...</option>
                        <c:forEach items="${ciudadesOrigen}" var="ciu">
                            <option value="<c:out value='${ciu}'/>" <c:if test="${busquedaForm.origen eq ciu}">selected</c:if>>
                                <c:out value="${ciu}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group-compact">
                    <label for="destino">Destino</label>
                    <select id="destino" name="destino" class="form-control" required>
                        <option value="">Seleccionar ciudad...</option>
                        <c:forEach items="${ciudadesDestino}" var="ciu">
                            <option value="<c:out value='${ciu}'/>" <c:if test="${busquedaForm.destino eq ciu}">selected</c:if>>
                                <c:out value="${ciu}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group-compact">
                    <label for="fechaLlegada">Llegada (Vuelo)</label>
                    <input type="date" id="fechaLlegada" name="fechaLlegada" class="form-control"
                           value="<c:out value='${busquedaForm.fechaLlegada}'/>" required />
                </div>

                <div class="form-group-compact">
                    <label for="fechaPartida">Salida (Hotel)</label>
                    <input type="date" id="fechaPartida" name="fechaPartida" class="form-control"
                           value="<c:out value='${busquedaForm.fechaPartida}'/>" required />
                </div>

                <div class="form-group-compact" style="flex: 0 1 110px;">
                    <label for="personas">Personas</label>
                    <input type="number" id="personas" name="personas" class="form-control"
                           min="1" max="20" value="<c:out value='${busquedaForm.personas}'/>" required />
                </div>

                <div class="form-group-compact" style="flex: 0 1 150px;">
                    <label for="ordenPrecio">Ordenar por</label>
                    <select id="ordenPrecio" name="ordenPrecio" class="form-control">
                        <option value="ASC" <c:if test="${busquedaForm.ordenPrecio eq 'ASC'}">selected</c:if>>Precio: Menor a Mayor</option>
                        <option value="DESC" <c:if test="${busquedaForm.ordenPrecio eq 'DESC'}">selected</c:if>>Precio: Mayor a Menor</option>
                    </select>
                </div>

                <div>
                    <button type="submit" class="btn-search">Buscar Viajes</button>
                </div>
            </div>
        </form>
    </div>
</div>

<jsp:include page="comun/footer.jsp" />

