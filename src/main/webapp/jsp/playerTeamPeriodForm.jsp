<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Период выступлений · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header activePage="players"/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <div class="row justify-content-center">
        <section class="col-12 col-lg-6">
            <div class="card">
                <div class="card-body p-4">
                    <h1 class="h3 fw-bold mb-3">
                        Добавление периода выступлений
                    </h1>

                    <p class="text-body-secondary">
                        <c:out value="${player.lastName}"/>
                        <c:out value="${player.name}"/>
                        <c:out value="${player.middleName}"/>
                    </p>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <c:choose>
                        <c:when test="${empty teams or empty years}">
                            <p class="text-body-secondary">
                                Для добавления периода нужны
                                хотя бы одна команда и один сезон.
                            </p>

                            <div class="d-flex flex-wrap gap-2">
                                <c:if test="${empty teams}">
                                    <a href="/admin/teams/add"
                                       class="btn btn-primary">
                                        Добавить команду
                                    </a>
                                </c:if>

                                <c:if test="${empty years}">
                                    <a href="/admin/seasons/add"
                                       class="btn btn-primary">
                                        Добавить сезон
                                    </a>
                                </c:if>
                            </div>
                        </c:when>

                        <c:otherwise>
                            <form method="post"
                                  action="/admin/players/periods/add">

                                <input type="hidden" name="playerId"
                                       value="${player.playerId}">

                                <div class="mb-3">
                                    <label for="teamId" class="form-label">
                                        Команда
                                    </label>

                                    <select id="teamId" name="teamId"
                                            class="form-select" required>
                                        <option value="">Выберите команду</option>

                                        <c:forEach items="${teams}" var="team">
                                            <option value="${team.teamId}"
                                                ${param.teamId eq team.teamId ? 'selected' : ''}>
                                                <c:out value="${team.name}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label for="seasonYear" class="form-label">
                                        Сезон
                                    </label>

                                    <select id="seasonYear" name="seasonYear"
                                            class="form-select" required>
                                        <option value="">Выберите сезон</option>

                                        <c:forEach items="${years}" var="year">
                                            <option value="${year}"
                                                ${param.seasonYear eq year ? 'selected' : ''}>
                                                Сезон ${year}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="row g-3 mb-4">
                                    <div class="col-12 col-md-6">
                                        <label for="startDate" class="form-label">
                                            Начало периода
                                        </label>

                                        <input id="startDate" name="startDate"
                                               type="date" class="form-control"
                                               required
                                               value="<c:out value='${param.startDate}'/>">
                                    </div>

                                    <div class="col-12 col-md-6">
                                        <label for="endDate" class="form-label">
                                            Окончание периода
                                        </label>

                                        <input id="endDate" name="endDate"
                                               type="date" class="form-control"
                                               value="<c:out value='${param.endDate}'/>">
                                    </div>
                                </div>

                                <p class="small text-body-secondary">
                                    Даты включаются в период.
                                    Если окончание пока неизвестно,
                                    оставьте поле пустым.
                                </p>

                                <button type="submit" class="btn btn-primary">
                                    Добавить период
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>

                    <a href="/player?id=${player.playerId}"
                       class="btn btn-outline-secondary mt-3">
                        Назад к игроку
                    </a>
                </div>
            </div>
        </section>
    </div>
</main>

<ui:footer/>

</body>
</html>
