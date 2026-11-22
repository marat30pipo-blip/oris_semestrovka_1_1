<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${team.name}"/> · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header activePage="teams"/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <a href="/teams" class="link-primary">К списку команд</a>

    <article class="card mt-3">
        <div class="card-body p-4">
            <div class="d-flex align-items-center flex-wrap gap-4">
                <c:if test="${team.emblemId ne null}">
                    <img src="/files?id=${team.emblemId}"
                         alt="Эмблема команды"
                         width="120" height="120"
                         class="object-fit-contain">
                </c:if>

                <div>
                    <h1 class="h3 fw-bold mb-2">
                        <c:out value="${team.name}"/>
                    </h1>

                    <c:if test="${team.foundedOn ne null}">
                        <p class="text-body-secondary mb-0">
                            Дата основания:
                            <c:out value="${team.foundedOn}"/>
                        </p>
                    </c:if>
                </div>
            </div>

            <c:if test="${not empty team.description}">
                <p class="mt-3 mb-0">
                    <c:out value="${team.description}"/>
                </p>
            </c:if>

            <c:if test="${sessionScope.role eq 'ADMIN'}">
                <div class="d-flex flex-wrap gap-2 mt-3">
                    <a href="/admin/teams/edit?id=${team.teamId}"
                       class="btn btn-outline-primary btn-sm">
                        Редактировать
                    </a>

                    <a href="/admin/teams/emblem?id=${team.teamId}"
                       class="btn btn-outline-secondary btn-sm">
                        Изменить эмблему
                    </a>
                </div>
            </c:if>
        </div>
    </article>

    <section class="card mt-4">
        <div class="card-body p-4">
            <div class="d-flex justify-content-between align-items-center
                        flex-wrap gap-3 mb-3">
                <h2 class="h4 fw-bold mb-0">Состав по сезонам</h2>

                <c:if test="${not empty years}">
                    <form method="get" action="/team"
                          class="d-flex flex-wrap gap-2">

                        <input type="hidden" name="id"
                               value="${team.teamId}">

                        <select name="seasonYear"
                                class="form-select w-auto"
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
                </c:if>
            </div>

            <c:choose>
                <c:when test="${empty years}">
                    <p class="text-body-secondary mb-0">
                        Сезоны пока не добавлены.
                    </p>
                </c:when>

                <c:when test="${empty players}">
                    <p class="text-body-secondary mb-0">
                        В выбранном сезоне игроки команды пока не указаны.
                    </p>
                </c:when>

                <c:otherwise>
                    <p class="small text-body-secondary">
                        Все игроки, выступавшие за команду в сезоне
                            ${seasonYear}, включая завершивших выступления.
                    </p>

                    <div class="table-responsive">
                        <table class="table align-middle mb-0">
                            <thead>
                            <tr>
                                <th scope="col">Игрок</th>
                                <th scope="col">Позиция</th>
                                <th scope="col">Дата рождения</th>
                            </tr>
                            </thead>

                            <tbody>
                            <c:forEach items="${players}" var="player">
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center gap-3">
                                            <c:if test="${player.photoId ne null}">
                                                <img src="/files?id=${player.photoId}"
                                                     alt="Фотография игрока"
                                                     width="48" height="48"
                                                     class="rounded object-fit-cover"
                                                     loading="lazy">
                                            </c:if>

                                            <a href="/player?id=${player.playerId}"
                                               class="link-primary">
                                                <c:out value="${player.lastName}"/>
                                                <c:out value="${player.name}"/>
                                                <c:out value="${player.middleName}"/>
                                            </a>
                                        </div>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty player.position}">
                                                <c:out value="${player.position}"/>
                                            </c:when>
                                            <c:otherwise>Не указана</c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:out value="${player.birthDate}"
                                               default="Не указана"/>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
    <section class="card mt-4">
        <div class="card-body p-4">
            <h2 class="h4 fw-bold mb-3">Турниры команды</h2>

            <c:choose>
                <c:when test="${empty years}">
                    <p class="text-body-secondary mb-0">
                        Сезоны пока не добавлены.
                    </p>
                </c:when>

                <c:when test="${empty tournaments}">
                    <p class="text-body-secondary mb-0">
                        В выбранном сезоне команда пока не заявлена
                        ни в один турнир.
                    </p>
                </c:when>

                <c:otherwise>
                    <p class="text-body-secondary">
                        Сезон ${seasonYear}
                    </p>

                    <div class="list-group">
                        <c:forEach items="${tournaments}" var="tournament">
                            <a href="/tournament?id=${tournament.tournamentId}"
                               class="list-group-item list-group-item-action">
                                <div class="fw-semibold">
                                    <c:out value="${tournament.name}"/>
                                </div>

                                <div class="small text-body-secondary">
                                    <c:out value="${tournament.type}"/>
                                    ·
                                    <c:out value="${tournament.startDate}"/>
                                    —
                                    <c:out value="${tournament.endDate}"/>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

</main>

<ui:footer/>

</body>
</html>