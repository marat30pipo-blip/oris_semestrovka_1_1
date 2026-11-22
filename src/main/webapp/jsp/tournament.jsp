<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${tournament.name}"/> · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>
<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <a class="link-primary" href="/home">На главную</a>

    <article class="card mt-3">
        <div class="card-body p-4">
            <h1 class="fw-bold mb-3">
                <c:out value="${tournament.name}"/>
            </h1>

            <c:if test="${sessionScope.role eq 'ADMIN'}">
                <a href="/admin/tournaments/edit?id=${tournament.tournamentId}"
                   class="btn btn-outline-primary mb-3">
                    Редактировать турнир
                </a>
            </c:if>

            <p class="text-body-secondary mb-2">
                <c:out value="${tournament.type}"/>
            </p>

            <p class="text-body-secondary">
                <c:out value="${tournament.startDate}"/>
                —
                <c:out value="${tournament.endDate}"/>
            </p>

            <c:if test="${not empty tournament.description}">
                <p class="mb-0">
                    <c:out value="${tournament.description}"/>
                </p>
            </c:if>
        </div>
    </article>

    <c:if test="${tournament.type eq 'Чемпионат'}">
        <section class="card mt-4">
            <div class="card-body p-4">
                <h2 class="h4 fw-bold mb-3">Турнирная таблица</h2>

                <c:choose>
                    <c:when test="${empty standings}">
                        <p class="text-body-secondary mb-0">
                            Команды пока не добавлены
                        </p>
                    </c:when>

                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-striped align-middle text-center mb-0">
                                <thead>
                                <tr>
                                    <th scope="col">Место</th>
                                    <th scope="col" class="text-start">Команда</th>
                                    <th scope="col">И</th>
                                    <th scope="col">В</th>
                                    <th scope="col">Н</th>
                                    <th scope="col">П</th>
                                    <th scope="col">ЗМ</th>
                                    <th scope="col">ПМ</th>
                                    <th scope="col">РМ</th>
                                    <th scope="col">О</th>
                                </tr>
                                </thead>

                                <tbody>
                                <c:forEach items="${standings}"
                                           var="standing" varStatus="position">
                                    <tr>
                                        <td>${position.count}</td>
                                        <th scope="row" class="text-start">
                                            <c:out value="${standing.teamName}"/>
                                        </th>
                                        <td>${standing.played}</td>
                                        <td>${standing.wins}</td>
                                        <td>${standing.draws}</td>
                                        <td>${standing.losses}</td>
                                        <td>${standing.goalsFor}</td>
                                        <td>${standing.goalsAgainst}</td>
                                        <td>${standing.goalDifference}</td>
                                        <td class="fw-bold">${standing.points}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <p class="small text-body-secondary mt-3 mb-0">
                            И — игры, В — победы, Н — ничьи, П — поражения,
                            ЗМ — забитые мячи, ПМ — пропущенные мячи,
                            РМ — разница мячей, О — очки.
                        </p>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>
    </c:if>

    <section class="card mt-4">
        <div class="card-body p-4">
            <h2 class="h4 fw-bold mb-3">Команды турнира</h2>

            <c:if test="${sessionScope.role eq 'ADMIN'}">
                <a href="/admin/tournaments/teams/add?tournamentId=${tournament.tournamentId}"
                   class="btn btn-primary mb-3">
                    Добавить команду в турнир
                </a>
            </c:if>

            <c:if test="${empty teams}">
                <p class="text-body-secondary mb-0">
                    Команды пока не добавлены
                </p>
            </c:if>

            <ul class="list-group list-group-flush">
                <c:forEach items="${teams}" var="team">
                    <li class="list-group-item px-0">
                        <c:out value="${team.name}"/>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </section>

    <section class="card mt-4">
        <div class="card-body p-4">
            <div class="d-flex justify-content-between align-items-center
                    flex-wrap gap-3 mb-3">
                <h2 class="h4 fw-bold mb-0">Матчи турнира</h2>

                <c:if test="${sessionScope.role eq 'ADMIN'}">
                    <a href="/admin/matches/add?tournamentId=${tournament.tournamentId}"
                       class="btn btn-primary">
                        Добавить матч
                    </a>
                </c:if>
            </div>

            <div class="mb-3">
                <label for="matchStatus" class="form-label">
                    Статус матча
                </label>

                <select id="matchStatus" class="form-select w-auto"
                        data-tournament-id="${tournament.tournamentId}">
                    <option value="">Все статусы</option>
                    <option value="Запланирован">Запланирован</option>
                    <option value="Перенесён">Перенесён</option>
                    <option value="Завершён">Завершён</option>
                    <option value="Отменён">Отменён</option>
                    <option value="Технический результат">
                        Технический результат
                    </option>
                </select>
            </div>

            <p id="matchesMessage" class="text-danger"
               role="status"></p>

            <div id="matchesContainer">
                <jsp:include page="/jsp/matchesTable.jsp"/>
            </div>
        </div>
    </section>
</main>

<ui:footer/>

<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
<script src="/js/matches.js"></script>
</body>
</html>