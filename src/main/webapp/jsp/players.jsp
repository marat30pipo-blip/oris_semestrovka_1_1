<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Игроки · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header activePage="players"/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <a href="/home" class="link-primary">На главную</a>

    <div class="d-flex justify-content-between align-items-center
                flex-wrap gap-3 my-4">
        <h1 class="fw-bold mb-0">Игроки</h1>
    </div>

    <c:if test="${sessionScope.role eq 'ADMIN'}">
        <a href="/admin/players/add" class="btn btn-primary">
            Добавить игрока
        </a>
    </c:if>

    <c:choose>
        <c:when test="${empty players}">
            <div class="card">
                <div class="card-body p-4">
                    <p class="text-body-secondary mb-0">
                        Игроки пока не добавлены.
                    </p>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div class="row g-3">
                <c:forEach items="${players}" var="player">
                    <div class="col-12 col-md-6 col-lg-4">
                        <article class="card h-100">
                            <div class="card-body p-4">
                                <div class="text-center mb-3">
                                    <c:choose>
                                        <c:when test="${player.photoId ne null}">
                                            <img src="/files?id=${player.photoId}"
                                                 alt="Фотография игрока"
                                                 width="160" height="160"
                                                 class="rounded object-fit-cover"
                                                 loading="lazy">
                                        </c:when>

                                        <c:otherwise>
                                            <div class="border rounded bg-body-tertiary
                                                        d-flex align-items-center
                                                        justify-content-center p-4">
                                                <span class="text-body-secondary">
                                                    Фото пока не добавлено
                                                </span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <h2 class="h5 fw-bold mb-3">
                                    <c:out value="${player.lastName}"/>
                                    <c:out value="${player.name}"/>
                                    <c:out value="${player.middleName}"/>
                                </h2>

                                <dl class="row small mb-0">
                                    <dt class="col-6">Дата рождения</dt>
                                    <dd class="col-6">
                                        <c:out value="${player.birthDate}"
                                               default="Не указана"/>
                                    </dd>

                                    <dt class="col-6">Позиция</dt>
                                    <dd class="col-6">
                                        <c:choose>
                                            <c:when test="${not empty player.position}">
                                                <c:out value="${player.position}"/>
                                            </c:when>
                                            <c:otherwise>Не указана</c:otherwise>
                                        </c:choose>
                                    </dd>

                                    <dt class="col-6">Рост</dt>
                                    <dd class="col-6">
                                        <c:choose>
                                            <c:when test="${player.heightCm ne null}">
                                                <c:out value="${player.heightCm}"/> см
                                            </c:when>
                                            <c:otherwise>Не указан</c:otherwise>
                                        </c:choose>
                                    </dd>

                                    <dt class="col-6">Вес</dt>
                                    <dd class="col-6">
                                        <c:choose>
                                            <c:when test="${player.weightKg ne null}">
                                                <c:out value="${player.weightKg}"/> кг
                                            </c:when>
                                            <c:otherwise>Не указан</c:otherwise>
                                        </c:choose>
                                    </dd>
                                </dl>

                                <div class="mt-3">
                                    <a href="/player?id=${player.playerId}"
                                       class="btn btn-outline-primary btn-sm">
                                        Подробнее
                                    </a>
                                </div>

                                <c:if test="${sessionScope.role eq 'ADMIN'}">
                                    <div class="mt-3">
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
                        </article>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<ui:footer/>

</body>
</html>