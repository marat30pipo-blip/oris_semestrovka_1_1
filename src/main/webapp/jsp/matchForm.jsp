<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Добавление матча · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>
<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <div class="row justify-content-center">
        <section class="col-12 col-lg-8">
            <div class="card">
                <div class="card-body p-4">
                    <h1 class="h3 fw-bold">Добавление матча</h1>

                    <p class="text-body-secondary">
                        <c:out value="${tournament.name}"/>
                    </p>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <c:choose>
                        <c:when test="${teams.size() lt 2}">
                            <div class="alert alert-info">
                                Для создания матча добавьте в турнир
                                минимум две команды.
                            </div>

                            <a href="/admin/tournaments/teams/add?tournamentId=${tournament.tournamentId}"
                               class="btn btn-primary">
                                Добавить команду
                            </a>
                        </c:when>

                        <c:otherwise>
                            <form method="post" action="/admin/matches/add">
                                <input type="hidden" name="tournamentId"
                                       value="${tournament.tournamentId}">

                                <div class="row g-3">
                                    <div class="col-12 col-md-6">
                                        <label for="team1Id" class="form-label">
                                            Первая команда
                                        </label>
                                        <select id="team1Id" name="team1Id"
                                                class="form-select" required>
                                            <option value="">Выберите команду</option>
                                            <c:forEach items="${teams}" var="team">
                                                <option value="${team.teamId}"
                                                    ${param.team1Id eq team.teamId ? 'selected' : ''}>
                                                    <c:out value="${team.name}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <div class="col-12 col-md-6">
                                        <label for="team2Id" class="form-label">
                                            Вторая команда
                                        </label>
                                        <select id="team2Id" name="team2Id"
                                                class="form-select" required>
                                            <option value="">Выберите команду</option>
                                            <c:forEach items="${teams}" var="team">
                                                <option value="${team.teamId}"
                                                    ${param.team2Id eq team.teamId ? 'selected' : ''}>
                                                    <c:out value="${team.name}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <div class="col-12">
                                        <label for="venueId" class="form-label">
                                            Площадка
                                        </label>
                                        <select id="venueId" name="venueId" class="form-select">
                                            <option value="">Пока не определена</option>

                                            <c:forEach items="${venues}" var="venue">
                                                <option value="${venue.venueId}"
                                                    ${param.venueId eq venue.venueId ? 'selected' : ''}>
                                                    <c:out value="${venue.name}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <div class="col-12 col-md-6">
                                        <label for="matchDate" class="form-label">
                                            Дата матча
                                        </label>
                                        <input id="matchDate" name="matchDate"
                                               type="date" class="form-control"
                                               value="<c:out value='${param.matchDate}'/>">
                                    </div>

                                    <div class="col-12 col-md-6">
                                        <label for="startTime" class="form-label">
                                            Время начала
                                        </label>
                                        <input id="startTime" name="startTime"
                                               type="time" class="form-control"
                                               value="<c:out value='${param.startTime}'/>">
                                    </div>

                                    <div class="col-12 col-md-6">
                                        <label for="roundNumber" class="form-label">
                                            Номер тура
                                        </label>
                                        <input id="roundNumber" name="roundNumber"
                                               type="number" min="1"
                                               class="form-control"
                                               value="<c:out value='${param.roundNumber}'/>">
                                    </div>

                                    <div class="col-12 col-md-6">
                                        <label for="status" class="form-label">
                                            Статус
                                        </label>
                                        <select id="status" name="status"
                                                class="form-select" required>
                                            <option value="Запланирован"
                                                ${param.status eq 'Запланирован' ? 'selected' : ''}>
                                                Запланирован
                                            </option>
                                            <option value="Перенесён"
                                                ${param.status eq 'Перенесён' ? 'selected' : ''}>
                                                Перенесён
                                            </option>
                                            <option value="Завершён"
                                                ${param.status eq 'Завершён' ? 'selected' : ''}>
                                                Завершён
                                            </option>
                                            <option value="Отменён"
                                                ${param.status eq 'Отменён' ? 'selected' : ''}>
                                                Отменён
                                            </option>
                                            <option value="Технический результат"
                                                ${param.status eq 'Технический результат' ? 'selected' : ''}>
                                                Технический результат
                                            </option>
                                        </select>
                                    </div>
                                </div>

                                <c:if test="${tournament.type eq 'Кубок'}">
                                    <div class="mt-3">
                                        <label for="stage" class="form-label">
                                            Стадия кубка
                                        </label>

                                        <input id="stage" name="stage" type="text"
                                               class="form-control"
                                               maxlength="50"
                                               placeholder="Например, Полуфинал"
                                               value="<c:out value='${param.stage}'/>">
                                    </div>
                                </c:if>

                                <h2 class="h5 mt-4">Основной счёт</h2>
                                <p class="small text-body-secondary">
                                    Заполните для завершённого матча
                                    или технического результата.
                                </p>

                                <div class="row g-3">
                                    <div class="col-6">
                                        <label for="score1" class="form-label">
                                            Первая команда
                                        </label>
                                        <input id="score1" name="score1"
                                               type="number" min="0"
                                               class="form-control"
                                               value="<c:out value='${param.score1}'/>">
                                    </div>
                                    <div class="col-6">
                                        <label for="score2" class="form-label">
                                            Вторая команда
                                        </label>
                                        <input id="score2" name="score2"
                                               type="number" min="0"
                                               class="form-control"
                                               value="<c:out value='${param.score2}'/>">
                                    </div>
                                </div>

                                <c:if test="${tournament.type eq 'Кубок'}">
                                    <h2 class="h5 mt-4">Серия пенальти</h2>
                                    <p class="small text-body-secondary">
                                        Заполните при ничьей
                                        в завершённом матче кубка.
                                    </p>

                                    <div class="row g-3">
                                        <div class="col-6">
                                            <label for="penalty1" class="form-label">
                                                Первая команда
                                            </label>
                                            <input id="penalty1" name="penalty1"
                                                   type="number" min="0"
                                                   class="form-control"
                                                   value="<c:out value='${param.penalty1}'/>">
                                        </div>
                                        <div class="col-6">
                                            <label for="penalty2" class="form-label">
                                                Вторая команда
                                            </label>
                                            <input id="penalty2" name="penalty2"
                                                   type="number" min="0"
                                                   class="form-control"
                                                   value="<c:out value='${param.penalty2}'/>">
                                        </div>
                                    </div>
                                </c:if>

                                <button type="submit" class="btn btn-primary mt-4">
                                    Сохранить матч
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>

                    <a href="/tournament?id=${tournament.tournamentId}"
                       class="btn btn-outline-secondary mt-3">
                        Назад к турниру
                    </a>
                </div>
            </div>
        </section>
    </div>
</main>

<ui:footer/>

</body>
</html>
