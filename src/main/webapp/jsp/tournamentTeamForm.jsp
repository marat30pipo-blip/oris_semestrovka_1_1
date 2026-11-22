<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Добавление команды в турнир · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>
<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header/>

<main class="container py-5 flex-grow-1">
    <div class="row justify-content-center">
        <section class="col-12 col-md-8 col-lg-6">
            <div class="card">
                <div class="card-body p-4">
                    <h1 class="h3 mb-3">Добавление команды в турнир</h1>

                    <p class="text-body-secondary">
                        <c:out value="${tournament.name}"/>
                    </p>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <c:choose>
                        <c:when test="${empty teams}">
                            <p>Сначала добавьте команду в общий список.</p>
                            <a href="/admin/teams/add" class="btn btn-primary">
                                Создать команду
                            </a>
                        </c:when>

                        <c:otherwise>
                            <form method="post"
                                  action="/admin/tournaments/teams/add">
                                <input type="hidden" name="tournamentId"
                                       value="${tournament.tournamentId}">

                                <div class="mb-4">
                                    <label for="teamId" class="form-label">
                                        Команда
                                    </label>
                                    <select id="teamId" name="teamId"
                                            class="form-select" required>
                                        <option value="">Выберите команду</option>

                                        <c:forEach items="${teams}" var="team">
                                            <option value="${team.teamId}">
                                                <c:out value="${team.name}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <button type="submit" class="btn btn-primary">
                                    Добавить в турнир
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