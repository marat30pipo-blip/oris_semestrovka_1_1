<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Матч · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <a href="/tournament?id=${match.tournamentId}" class="link-primary">
        К турниру
    </a>

    <article class="card mt-3">
        <div class="card-body p-4">
            <p class="text-body-secondary mb-2">
                <c:out value="${match.tournamentName}"/>
                · Сезон ${match.seasonYear}
            </p>

            <h1 class="h3 fw-bold mb-4">
                <c:out value="${match.team1Name}"/>
                —
                <c:out value="${match.team2Name}"/>
            </h1>

            <div class="row g-3 align-items-center text-center mb-4">
                <div class="col-12 col-md-4">
                    <a href="/team?id=${match.team1Id}&amp;seasonYear=${match.seasonYear}"
                       class="h5 link-primary">
                        <c:out value="${match.team1Name}"/>
                    </a>
                </div>

                <div class="col-12 col-md-4">
                    <c:choose>
                        <c:when test="${match.score1 ne null
                                        and match.score2 ne null}">
                            <div class="display-5 fw-bold">
                                    ${match.score1} : ${match.score2}
                            </div>
                        </c:when>

                        <c:otherwise>
                            <p class="text-body-secondary mb-0">
                                Счёт пока не внесён
                            </p>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${match.penalty1 ne null
                                  and match.penalty2 ne null}">
                        <p class="small text-body-secondary mt-2 mb-0">
                            По пенальти:
                                ${match.penalty1} : ${match.penalty2}
                        </p>
                    </c:if>
                </div>

                <div class="col-12 col-md-4">
                    <a href="/team?id=${match.team2Id}&amp;seasonYear=${match.seasonYear}"
                       class="h5 link-primary">
                        <c:out value="${match.team2Name}"/>
                    </a>
                </div>
            </div>

            <dl class="row mb-0">
                <dt class="col-5 col-md-3">Статус</dt>
                <dd class="col-7 col-md-9">
                    <c:out value="${match.status}"/>
                </dd>

                <dt class="col-5 col-md-3">Дата</dt>
                <dd class="col-7 col-md-9">
                    <c:out value="${match.matchDate}"
                           default="Уточняется"/>
                </dd>

                <dt class="col-5 col-md-3">Время</dt>
                <dd class="col-7 col-md-9">
                    <c:out value="${match.startTime}"
                           default="Уточняется"/>
                </dd>

                <dt class="col-5 col-md-3">Площадка</dt>
                <dd class="col-7 col-md-9">
                    <c:out value="${match.venueName}"
                           default="Не определена"/>
                </dd>

                <c:if test="${match.tournamentType eq 'Чемпионат' and match.roundNumber ne null}">
                    <dt class="col-5 col-md-3">Тур</dt>
                    <dd class="col-7 col-md-9">
                        <c:out value="${match.roundNumber}"/>
                    </dd>
                </c:if>

                <c:if test="${match.tournamentType eq 'Кубок'}">
                    <dt class="col-5 col-md-3">Стадия</dt>
                    <dd class="col-7 col-md-9">
                        <c:choose>
                            <c:when test="${not empty match.stage}">
                                <c:out value="${match.stage}"/>
                            </c:when>
                            <c:otherwise>Не указана</c:otherwise>
                        </c:choose>
                    </dd>
                </c:if>

                <dt class="col-5 col-md-3">Номер тура</dt>
                <dd class="col-7 col-md-9">
                    <c:out value="${match.roundNumber}"
                           default="Не указан"/>
                </dd>
            </dl>

            <c:if test="${sessionScope.role eq 'ADMIN'}">
                <div class="d-flex flex-wrap gap-2 mt-4">
                    <a href="/admin/matches/edit?id=${match.matchId}"
                       class="btn btn-outline-primary">
                        Редактировать матч
                    </a>
                </div>
            </c:if>
        </div>
    </article>
    <section class="mt-4">
        <h2 class="h4 fw-bold mb-3">Составы на матч</h2>

        <div class="row g-3">
            <div class="col-12 col-lg-6">
                <div class="card h-100">
                    <div class="card-body p-4">
                        <h3 class="h5 fw-bold mb-3">
                            <c:out value="${match.team1Name}"/>
                        </h3>

                        <c:if test="${sessionScope.role eq 'ADMIN'}">
                            <a href="/admin/matches/rosters/add?matchId=${match.matchId}&amp;teamId=${match.team1Id}"
                               class="btn btn-outline-primary btn-sm mb-3">
                                Добавить игрока
                            </a>
                        </c:if>

                        <c:choose>
                            <c:when test="${empty team1Players}">
                                <p class="text-body-secondary mb-0">
                                    Состав пока не добавлен.
                                </p>
                            </c:when>

                            <c:otherwise>
                                <ul class="list-group list-group-flush">
                                    <c:forEach items="${team1Players}" var="player">
                                        <li class="list-group-item px-0">
                                            <div class="d-flex align-items-center gap-3">

                                                <span class="badge text-bg-primary">
                                                    <c:out value="${player.shirtNumber}" default="—"/>
                                                </span>


                                                <div>
                                                    <a href="/player?id=${player.playerId}"
                                                       class="link-primary">
                                                        <c:out value="${player.lastName}"/>
                                                        <c:out value="${player.name}"/>
                                                        <c:out value="${player.middleName}"/>
                                                    </a>

                                                    <c:if test="${not empty player.position}">
                                                        <div class="small text-body-secondary">
                                                            <c:out value="${player.position}"/>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                            <c:if test="${sessionScope.role eq 'ADMIN'}">
                                                <form method="post" action="/admin/matches/rosters/delete"
                                                      class="mt-2">

                                                    <input type="hidden" name="matchId"
                                                           value="${match.matchId}">

                                                    <input type="hidden" name="teamId"
                                                           value="${match.team1Id}">

                                                    <input type="hidden" name="playerId"
                                                           value="${player.playerId}">

                                                    <button type="submit"
                                                            class="btn btn-outline-danger btn-sm">
                                                        Убрать из состава
                                                    </button>
                                                </form>
                                            </c:if>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <div class="col-12 col-lg-6">
                <div class="card h-100">
                    <div class="card-body p-4">
                        <h3 class="h5 fw-bold mb-3">
                            <c:out value="${match.team2Name}"/>
                        </h3>

                        <c:if test="${sessionScope.role eq 'ADMIN'}">
                            <a href="/admin/matches/rosters/add?matchId=${match.matchId}&amp;teamId=${match.team2Id}"
                               class="btn btn-outline-primary btn-sm mb-3">
                                Добавить игрока
                            </a>
                        </c:if>

                        <c:choose>
                            <c:when test="${empty team2Players}">
                                <p class="text-body-secondary mb-0">
                                    Состав пока не добавлен.
                                </p>
                            </c:when>

                            <c:otherwise>
                                <ul class="list-group list-group-flush">
                                    <c:forEach items="${team2Players}" var="player">
                                        <li class="list-group-item px-0">
                                            <div class="d-flex align-items-center gap-3">

                                                <span class="badge text-bg-primary">
                                                    <c:out value="${player.shirtNumber}" default="—"/>
                                                </span>


                                                <div>
                                                    <a href="/player?id=${player.playerId}"
                                                       class="link-primary">
                                                        <c:out value="${player.lastName}"/>
                                                        <c:out value="${player.name}"/>
                                                        <c:out value="${player.middleName}"/>
                                                    </a>

                                                    <c:if test="${not empty player.position}">
                                                        <div class="small text-body-secondary">
                                                            <c:out value="${player.position}"/>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                            <c:if test="${sessionScope.role eq 'ADMIN'}">
                                                <form method="post" action="/admin/matches/rosters/delete"
                                                      class="mt-2">

                                                    <input type="hidden" name="matchId"
                                                           value="${match.matchId}">

                                                    <input type="hidden" name="teamId"
                                                           value="${match.team2Id}">

                                                    <input type="hidden" name="playerId"
                                                           value="${player.playerId}">

                                                    <button type="submit"
                                                            class="btn btn-outline-danger btn-sm">
                                                        Убрать из состава
                                                    </button>
                                                </form>
                                            </c:if>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="card mt-4">
        <div class="card-body p-4">
            <h2 class="h4 fw-bold mb-3">Голы матча</h2>

            <c:if test="${sessionScope.role eq 'ADMIN' and match.status eq 'Завершён'}">
                <a href="/admin/matches/goals/add?matchId=${match.matchId}"
                   class="btn btn-outline-primary mb-3">
                    Добавить гол
                </a>
            </c:if>

            <c:choose>
                <c:when test="${match.status eq 'Технический результат'}">
                    <p class="text-body-secondary mb-0">
                        При техническом результате голы игрокам не записываются.
                    </p>
                </c:when>

                <c:when test="${empty goals}">
                    <p class="text-body-secondary mb-0">
                        Записи о голах пока не добавлены.
                    </p>
                </c:when>

                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table align-middle mb-0">
                            <thead>
                            <tr>
                                <th scope="col">Минута</th>
                                <th scope="col">Автор</th>
                                <th scope="col">В пользу команды</th>

                                <c:if test="${sessionScope.role eq 'ADMIN'}">
                                    <th scope="col">Действия</th>
                                </c:if>

                            </tr>
                            </thead>

                            <tbody>
                            <c:forEach items="${goals}" var="goal">
                                <tr>
                                    <td class="text-nowrap">
                                            ${goal.minute}′
                                    </td>

                                    <td>
                                        <a href="/player?id=${goal.playerId}"
                                           class="link-primary">
                                            <c:out value="${goal.lastName}"/>
                                            <c:out value="${goal.name}"/>
                                            <c:out value="${goal.middleName}"/>
                                        </a>

                                        <c:if test="${goal.ownGoal}">
                                        <span class="badge text-bg-secondary ms-2">
                                            Автогол
                                        </span>
                                        </c:if>
                                    </td>

                                    <td>
                                        <a href="/team?id=${goal.teamId}&amp;seasonYear=${match.seasonYear}"
                                           class="link-primary">
                                            <c:out value="${goal.teamName}"/>
                                        </a>
                                    </td>

                                    <c:if test="${sessionScope.role eq 'ADMIN'}">
                                        <td>
                                            <form method="post" action="/admin/matches/goals/delete">
                                                <input type="hidden" name="goalId"
                                                       value="${goal.goalId}">

                                                <input type="hidden" name="matchId"
                                                       value="${match.matchId}">

                                                <button type="submit"
                                                        class="btn btn-outline-danger btn-sm">
                                                    Удалить
                                                </button>
                                            </form>
                                        </td>
                                    </c:if>

                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>

            <p class="small text-body-secondary mt-3 mb-0">
                Серия пенальти отображается отдельно от голов матча.
            </p>
        </div>
    </section>

</main>

<ui:footer/>

</body>
</html>
