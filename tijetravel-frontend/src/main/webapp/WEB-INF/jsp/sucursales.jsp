<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Nuestras Sucursales" />
<jsp:include page="comun/header.jsp" />

<div class="card">
    <h2 class="section-title">📍 Nuestras Sucursales</h2>
    <p style="color: var(--color-text-muted); margin-bottom: 1.5rem;">
        Encontrá la sucursal de Tije Travel más cercana a tu domicilio para recibir asesoramiento personalizado.
    </p>

    <form action="${pageContext.request.contextPath}/sucursales" method="get">
        <div class="search-form-row">
            <div class="form-group-compact" style="flex: 1 1 300px;">
                <label for="ciudad">Filtrar por Ciudad</label>
                <select id="ciudad" name="ciudad" class="form-control">
                    <option value="">Todas las ciudades</option>
                    <c:forEach items="${ciudadesSucursales}" var="ciu">
                        <option value="<c:out value='${ciu}'/>" <c:if test="${filtroCiudad eq ciu}">selected</c:if>>
                            <c:out value="${ciu}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div>
                <button type="submit" class="btn-primary" style="height: 42px;">Filtrar</button>
            </div>
        </div>
    </form>
</div>

<div class="table-container">
    <table class="data-table">
        <thead>
            <tr>
                <th>Código</th>
                <th>Ciudad</th>
                <th>Dirección</th>
                <th>Teléfono de Contacto</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${not empty sucursales}">
                    <c:forEach items="${sucursales}" var="suc">
                        <tr>
                            <td><strong><c:out value="${suc.codigo}"/></strong></td>
                            <td><c:out value="${suc.ciudad}"/></td>
                            <td><c:out value="${suc.direccion}"/></td>
                            <td>📞 <c:out value="${suc.telefono}"/></td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="4" style="text-align: center; color: var(--color-text-muted); padding: 1.5rem;">
                            No se encontraron sucursales registradas para la ciudad seleccionada.
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<jsp:include page="comun/footer.jsp" />

