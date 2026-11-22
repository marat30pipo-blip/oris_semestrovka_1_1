<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Игрок · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header activePage="players"/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <a href="/players" class="link-primary">К списку игроков</a>

    <article class="card mt-3">
        <div class="card-body p-4">
            <div class="row g-4">
                <div class="col-12 col-md-4 text-center">
                    <c:choose>
                        <c:when test="${player.photoId ne null}">
                            <img src="/files?id=${player.photoId}"
                                 alt="Фотография игрока"
                                 width="200" height="200"
                                 class="rounded object-fit-cover">
                        </c:when>

                        <c:otherwise>
                            <div class="border rounded bg-body-tertiary p-4">
                                <span class="text-body-secondary">
                                    Фото пока не добавлено
                                </span>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="col-12 col-md-8">
                    <h1 class="h3 fw-bold mb-4">
                        <c:out value="${player.lastName}"/>
                        <c:out value="${player.name}"/>
                        <c:out value="${player.middleName}"/>
                    </h1>

                    <dl class="row mb-0">
                        <dt class="col-6 col-sm-4">Дата рождения</dt>
                        <dd class="col-6 col-sm-8">
                            <c:out value="${player.birthDate}"
                                   default="Не указана"/>
                        </dd>

                        <dt class="col-6 col-sm-4">Позиция</dt>
                        <dd class="col-6 col-sm-8">
                            <c:choose>
                                <c:when test="${not empty player.position}">
                                    <c:out value="${player.position}"/>
                                </c:when>
                                <c:otherwise>Не указана</c:otherwise>
                            </c:choose>
                        </dd>

                        <dt class="col-6 col-sm-4">Рост</dt>
                        <dd class="col-6 col-sm-8">
                            <c:choose>
                                <c:when test="${player.heightCm ne null}">
                                    <c:out value="${player.heightCm}"/> см
                                </c:when>
                                <c:otherwise>Не указан</c:otherwise>
                            </c:choose>
                        </dd>

                        <dt class="col-6 col-sm-4">Вес</dt>
                        <dd class="col-6 col-sm-8">
                            <c:choose>
                                <c:when test="${player.weightKg ne null}">
                                    <c:out value="${player.weightKg}"/> кг
                                </c:when>
                                <c:otherwise>Не указан</c:otherwise>
                            </c:choose>
                        </dd>

                        <dt class="col-6 col-sm-4">Номер</dt>
                        <dd class="col-6 col-sm-8">
                            <c:out value="${player.shirtNumber}" default="Не указан"/>
                        </dd>
                    </dl>

                    <c:if test="${sessionScope.role eq 'ADMIN'}">
                        <div class="d-flex flex-wrap gap-2 mt-3">
                            <a href="/admin/players/edit?id=${player.playerId}"
                               class="btn btn-outline-primary btn-sm">
                                Редактировать
                            </a>

                            <a href="/admin/players/photo?id=${player.playerId}"
                               class="btn btn-outline-secondary btn-sm">
                                Изменить фото
                            </a>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </article>

    <section class="card mt-4">
        <div class="card-body p-4">
            <h2 class="h4 fw-bold mb-3">История выступлений</h2>

            <c:if test="${sessionScope.role eq 'ADMIN'}">
                <a href="/admin/players/periods/add?playerId=${player.playerId}"
                   class="btn btn-primary mb-3">
                    Добавить период выступлений
                </a>
            </c:if>

            <c:choose>
                <c:when test="${empty periods}">
                    <p class="text-body-secondary mb-0">
                        История выступлений пока не добавлена.
                    </p>
                </c:when>

                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table align-middle mb-0">
                            <thead>
                            <tr>
                                <th scope="col">Сезон</th>
                                <th scope="col">Команда</th>
                                <th scope="col">Начало периода</th>
                                <th scope="col">Окончание периода</th>

                                <c:if test="${sessionScope.role eq 'ADMIN'}">
                                    <th scope="col">Действия</th>
                                </c:if>

                            </tr>
                            </thead>

                            <tbody>
                            <c:forEach items="${periods}" var="period">
                                <tr>
                                    <td>${period.seasonYear}</td>
                                    <td>
                                        <c:out value="${period.teamName}"/>
                                    </td>
                                    <td>
                                        <c:out value="${period.startDate}"/>
                                    </td>
                                    <td>
                                        <c:out value="${period.endDate}"
                                               default="Не указано"/>
                                    </td>
                                    <c:if test="${sessionScope.role eq 'ADMIN'}">
                                        <td>
                                            <c:if test="${period.endDate eq null}">
                                                <a href="/admin/players/periods/close?id=${period.appearanceId}"
                                                   class="btn btn-outline-primary btn-sm">
                                                    Закрыть период
                                                </a>
                                            </c:if>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</main>

<ui:footer/>

</body>
</html>