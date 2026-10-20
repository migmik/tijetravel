<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Error" />
<jsp:include page="comun/header.jsp" />

<div style="max-width: 600px; margin: 3rem auto; text-align: center;">
    <div class="card" style="padding: 2.5rem 1.5rem;">
        <div style="font-size: 3rem; margin-bottom: 1rem;">⚠️</div>
        <h2 style="font-size: 1.6rem; font-weight: 800; color: var(--color-primary); margin-bottom: 0.5rem;">
            Ocurrió un inconveniente
        </h2>
        <p style="color: var(--color-text-muted); font-size: 0.95rem; margin-bottom: 1.5rem;">
            <c:choose>
                <c:when test="${not empty message}">
                    <c:out value="${message}"/>
                </c:when>
                <c:otherwise>
                    La página que busca no está disponible o no posee los permisos requeridos para acceder.
                </c:otherwise>
            </c:choose>
        </p>

        <a href="${pageContext.request.contextPath}/" class="btn-primary" style="justify-content: center;">
            Volver a la página principal
        </a>
    </div>
</div>

<jsp:include page="comun/footer.jsp" />

