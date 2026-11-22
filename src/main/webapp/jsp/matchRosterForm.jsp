<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Добавление в состав · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <div class="row justify-content-center">
        <section class="col-12 col-md-8 col-lg-6">
            <div class="card">
                <div class="card-body p-4">
                    <h1 class="h3 fw-bold mb-3">
                        Добавление игрока в состав
                    </h1>

                    <p class="fw-semibold mb-2">
                        Команда: <c:out value="${teamName}"/>
                    </p>

                    <p class="text-body-secondary mb-1">
                        <c:out value="${match.team1Name}"/>
                        —
                        <c:out value="${match.team2Name}"/>
                    </p>

                    <p class="small text-body-secondary mb-4">
                        Сезон ${match.seasonYear}
                        ·
                        <c:out value="${match.matchDate}"
                               default="Дата не назначена"/>
                    </p>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <c:choose>
                        <c:when test="${match.matchDate eq null}">
                            <c:if test="${empty message}">
                                <p class="text-body-secondary">
                                    Сначала укажите дату матча.
                                </p>
                            </c:if>

                            <a href="/admin/matches/edit?id=${match.matchId}"
                               class="btn btn-primary">
                                Редактировать матч
                            </a>
                        </c:when>

                        <c:when test="${empty players}">
                            <p class="text-body-secondary">
                                Нет игроков с подходящим периодом
                                выступлений за эту команду
                                на дату матча в выбранном сезоне.
                            </p>

                            <a href="/players"
                               class="btn btn-outline-primary">
                                К списку игроков
                            </a>
                        </c:when>

                        <c:otherwise>
                            <form method="post"
                                  action="/admin/matches/rosters/add">

                                <input type="hidden" name="matchId"
                                       value="${match.matchId}">

                                <input type="hidden" name="teamId"
                                       value="${teamId}">

                                <div class="mb-4">
                                    <label for="playerId" class="form-label">
                                        Игрок
                                    </label>

                                    <select id="playerId" name="playerId"
                                            class="form-select" required>
                                        <option value="">Выберите игрока</option>

                                        <c:forEach items="${players}" var="player">
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
                                    </select>

                                    <div class="form-text">
                                        Повторное добавление игрока
                                        в этот матч не допускается.
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-primary">
                                    Добавить в состав
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