<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Команды · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header activePage="teams"/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <a href="/home" class="link-primary">На главную</a>

    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 my-4">
        <h1 class="fw-bold mb-0">Команды</h1>

        <c:if test="${sessionScope.role eq 'ADMIN'}">
            <a href="/admin/teams/add" class="btn btn-primary">
                Добавить команду
            </a>
        </c:if>
    </div>

    <c:choose>
        <c:when test="${empty teams}">
            <div class="card">
                <div class="card-body p-4">
                    <p class="text-body-secondary mb-0">
                        Команды пока не добавлены.
                    </p>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div class="row g-3">
                <c:forEach items="${teams}" var="team">
                    <div class="col-12 col-md-6 col-lg-4">
                        <article class="card h-100">
                            <div class="card-body p-4">
                                <div class="d-flex align-items-center gap-3 mb-3">
                                    <c:if test="${team.emblemId ne null}">
                                        <img src="/files?id=${team.emblemId}"
                                             alt="Эмблема команды"
                                             width="64" height="64"
                                             class="object-fit-contain">
                                    </c:if>

                                    <h2 class="h4 fw-bold mb-0">
                                        <c:out value="${team.name}"/>
                                    </h2>
                                </div>

                                <c:if test="${team.foundedOn ne null}">
                                    <p class="small text-body-secondary">
                                        Дата основания:
                                        <c:out value="${team.foundedOn}"/>
                                    </p>
                                </c:if>

                                <c:if test="${not empty team.description}">
                                    <p class="mb-0">
                                        <c:out value="${team.description}"/>
                                    </p>
                                </c:if>

                                <div class="mt-3">
                                    <a href="/team?id=${team.teamId}"
                                       class="btn btn-outline-primary btn-sm">
                                        Подробнее
                                    </a>
                                </div>

                                <c:if test="${sessionScope.role eq 'ADMIN'}">
                                    <div class="mt-3">
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
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<ui:footer/>

</body>
</html>
