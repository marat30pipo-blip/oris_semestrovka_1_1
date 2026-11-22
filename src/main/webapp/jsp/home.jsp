<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Дружба Cup · Сезон ${seasonYear}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>
<body class="bg-body-tertiary d-flex flex-column min-vh-100">


<ui:header activePage="home"/>


<main class="container py-4 py-lg-5 flex-grow-1" id="main">

    <c:if test="${sessionScope.role eq 'ADMIN'}">
        <div class="d-flex flex-wrap gap-2 mb-4">
            <a href="/admin/seasons/add" class="btn btn-outline-primary">
                Добавить сезон
            </a>

            <a href="/admin/tournaments/add?seasonYear=${seasonYear}"
               class="btn btn-primary">
                Добавить турнир
            </a>
        </div>
    </c:if>

    <div class="d-flex align-items-center justify-content-between flex-wrap gap-3 mb-4">
        <c:choose>
            <c:when test="${empty years}">
                <h1 class="fw-bold mb-0">Сезонов пока нет</h1>
            </c:when>

            <c:otherwise>
                <h1 class="fw-bold mb-0">Сезон ${seasonYear}</h1>

                <form method="get" action="/home" class="d-flex gap-2">
                    <select name="seasonYear" class="form-select w-auto"
                            aria-label="Выберите сезон">
                        <c:forEach items="${years}" var="year">
                            <option value="${year}"
                                ${year eq seasonYear ? 'selected' : ''}>
                                Сезон ${year}
                            </option>
                        </c:forEach>
                    </select>

                    <button type="submit" class="btn btn-primary">
                        Показать
                    </button>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
    <section class="row g-3 mb-4" id="tournaments" aria-label="Турниры сезона">
        <c:if test="${empty tournaments}">
            <div class="col-12">
                <p class="text-body-secondary mb-0">
                    В этом сезоне турниров пока нет
                </p>
            </div>
        </c:if>

        <c:forEach items="${tournaments}" var="tournament">
            <div class="col-12 col-md-6">
                <article class="card h-100">
                    <div class="card-body p-4">
                        <h2 class="h4 fw-bold mb-2">
                            <c:out value="${tournament.name}"/>
                        </h2>

                        <p class="text-body-secondary mb-2">
                            <c:out value="${tournament.type}"/>
                        </p>

                        <p class="small text-body-secondary mb-2">
                            <c:out value="${tournament.startDate}"/>
                            —
                            <c:out value="${tournament.endDate}"/>
                        </p>

                        <c:if test="${not empty tournament.description}">
                            <p class="mb-0">
                                <c:out value="${tournament.description}"/>
                            </p>
                        </c:if>
                        <a class="btn btn-outline-primary mt-3"
                           href="/tournament?id=${tournament.tournamentId}">
                            Открыть турнир
                        </a>
                    </div>
                </article>
            </div>
        </c:forEach>
    </section>
    <div class="row g-3 mb-4">

        <section class="col-12" id="matches">
            <div class="card h-100">
                <div class="card-body p-4">
                    <h2 class="h4 fw-bold mb-3">Матчи сезона</h2>
                    <c:if test="${empty matches}">
                        <p class="text-body-secondary mb-0">Матчей пока нет</p>
                    </c:if>

                    <ul class="list-group list-group-flush">
                        <c:forEach items="${matches}" var="match">
                            <li class="list-group-item px-0 py-3">

                                <c:if test="${match.roundNumber ne null}">
                                    <div class="small text-body-secondary">
                                        Тур: <c:out value="${match.roundNumber}"/>
                                    </div>
                                </c:if>

                                <strong>
                                    <c:out value="${match.team1Name}"/>
                                    —
                                    <c:out value="${match.team2Name}"/>
                                </strong>

                                <div class="mt-1">
                                    <c:choose>
                                        <c:when test="${match.score1 ne null and match.score2 ne null}">
                                            <span class="fw-bold">
                                                <c:out value="${match.score1}"/>
                                                :
                                                <c:out value="${match.score2}"/>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-body-secondary">Счёт пока не внесён</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="small text-body-secondary">
                                    Статус: <c:out value="${match.status}"/>
                                </div>

                                <div class="small text-body-secondary">
                                    <c:out value="${match.tournamentName}"/>
                                </div>

                                <div class="small text-body-secondary">
                                    <c:out value="${match.matchDate}" default="Дата уточняется"/>
                                    ·
                                    <c:out value="${match.startTime}" default="Время уточняется"/>
                                </div>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </div>
        </section>
    </div>
    <section class="card">
        <div class="card-body p-4">
            <h2 class="h4 fw-bold mb-3">Последние результаты</h2>

            <c:if test="${empty results}">
                <p class="text-body-secondary mb-0">Результатов пока нет</p>
            </c:if>

            <div class="row g-3">
                <c:forEach items="${results}" var="match" end="2">
                    <div class="col-12 col-md-4">
                        <div class="border rounded p-3 text-center">
                            <c:out value="${match.team1Name}"/>

                            <strong class="mx-2">
                                <c:out value="${match.score1}" default="—"/>
                                :
                                <c:out value="${match.score2}" default="—"/>
                            </strong>

                            <c:out value="${match.team2Name}"/>

                            <div class="small text-body-secondary mt-2">
                                <c:out value="${match.matchDate}" default="Дата уточняется"/>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

        </div>
    </section>
</main>
<ui:footer/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
