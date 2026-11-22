<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Добавление гола · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <div class="row justify-content-center">
        <section class="col-12 col-md-8 col-lg-6">
            <div class="card">
                <div class="card-body p-4">
                    <h1 class="h3 fw-bold mb-3">Добавление гола</h1>

                    <p class="fw-semibold">
                        <c:out value="${match.team1Name}"/>
                        —
                        <c:out value="${match.team2Name}"/>
                    </p>

                    <p class="text-body-secondary">
                        Официальный счёт:
                        <c:out value="${match.score1}" default="—"/>
                        :
                        <c:out value="${match.score2}" default="—"/>
                    </p>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <c:choose>
                        <c:when test="${match.status ne 'Завершён'}">
                            <p class="text-body-secondary">
                                Голы можно добавлять только
                                в завершённый матч.
                            </p>
                        </c:when>

                        <c:when test="${match.score1 eq null
                                        or match.score2 eq null}">
                            <p class="text-body-secondary">
                                Сначала укажите официальный счёт матча.
                            </p>

                            <a href="/admin/matches/edit?id=${match.matchId}"
                               class="btn btn-primary">
                                Редактировать матч
                            </a>
                        </c:when>

                        <c:when test="${empty team1Players
                                        and empty team2Players}">
                            <p class="text-body-secondary">
                                Сначала добавьте игроков в составы матча.
                            </p>
                        </c:when>

                        <c:otherwise>
                            <form method="post"
                                  action="/admin/matches/goals/add">

                                <input type="hidden" name="matchId"
                                       value="${match.matchId}">

                                <div class="mb-3">
                                    <label for="playerId" class="form-label">
                                        Автор гола
                                    </label>

                                    <select id="playerId" name="playerId"
                                            class="form-select" required>
                                        <option value="">Выберите игрока</option>

                                        <c:if test="${not empty team1Players}">
                                            <optgroup label="<c:out value='${match.team1Name}'/>">
                                                <c:forEach items="${team1Players}"
                                                           var="player">
                                                    <option value="${player.playerId}"
                                                        ${param.playerId eq player.playerId ? 'selected' : ''}>
                                                        <c:if test="${player.shirtNumber ne null}">
                                                            № ${player.shirtNumber} —
                                                        </c:if>
                                                        <c:out value="${player.lastName}"/>
                                                        <c:out value="${player.name}"/>
                                                        <c:out value="${player.middleName}"/>
                                                    </option>
                                                </c:forEach>
                                            </optgroup>
                                        </c:if>

                                        <c:if test="${not empty team2Players}">
                                            <optgroup label="<c:out value='${match.team2Name}'/>">
                                                <c:forEach items="${team2Players}"
                                                           var="player">
                                                    <option value="${player.playerId}"
                                                        ${param.playerId eq player.playerId ? 'selected' : ''}>
                                                        <c:if test="${player.shirtNumber ne null}">
                                                            № ${player.shirtNumber} —
                                                        </c:if>
                                                        <c:out value="${player.lastName}"/>
                                                        <c:out value="${player.name}"/>
                                                        <c:out value="${player.middleName}"/>
                                                    </option>
                                                </c:forEach>
                                            </optgroup>
                                        </c:if>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label for="teamId" class="form-label">
                                        В пользу какой команды засчитан гол
                                    </label>

                                    <select id="teamId" name="teamId"
                                            class="form-select" required>
                                        <option value="">Выберите команду</option>

                                        <option value="${match.team1Id}"
                                            ${param.teamId eq match.team1Id ? 'selected' : ''}>
                                            <c:out value="${match.team1Name}"/>
                                        </option>

                                        <option value="${match.team2Id}"
                                            ${param.teamId eq match.team2Id ? 'selected' : ''}>
                                            <c:out value="${match.team2Name}"/>
                                        </option>
                                    </select>

                                    <div class="form-text">
                                        Для автогола выберите команду соперника
                                        автора гола.
                                    </div>
                                </div>

                                <div class="mb-4">
                                    <label for="minute" class="form-label">
                                        Минута гола
                                    </label>

                                    <input id="minute" name="minute"
                                           type="number" min="1" step="1"
                                           class="form-control" required
                                           value="<c:out value='${param.minute}'/>">
                                </div>

                                <button type="submit" class="btn btn-primary">
                                    Добавить гол
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>

                    <a href="/match?id=${match.matchId}"
                       class="btn btn-outline-secondary mt-3">
                        Назад к матчу
                    </a>
                </div>
            </div>
        </section>
    </div>
</main>

<ui:footer/>

</body>
</html>