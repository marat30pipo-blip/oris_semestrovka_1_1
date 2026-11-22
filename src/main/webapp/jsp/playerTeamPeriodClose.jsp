<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Закрытие периода · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header activePage="players"/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <div class="row justify-content-center">
        <section class="col-12 col-md-8 col-lg-5">
            <div class="card">
                <div class="card-body p-4">
                    <h1 class="h3 fw-bold mb-4">
                        Закрытие периода выступлений
                    </h1>

                    <p class="fw-semibold mb-2">
                        <c:out value="${player.lastName}"/>
                        <c:out value="${player.name}"/>
                        <c:out value="${player.middleName}"/>
                    </p>

                    <dl class="row mb-4">
                        <dt class="col-5">Команда</dt>
                        <dd class="col-7">
                            <c:out value="${period.teamName}"/>
                        </dd>

                        <dt class="col-5">Сезон</dt>
                        <dd class="col-7">${period.seasonYear}</dd>

                        <dt class="col-5">Начало периода</dt>
                        <dd class="col-7">
                            <c:out value="${period.startDate}"/>
                        </dd>
                    </dl>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <form method="post"
                          action="/admin/players/periods/close">

                        <input type="hidden" name="id"
                               value="${period.appearanceId}">

                        <div class="mb-4">
                            <label for="endDate" class="form-label">
                                Дата окончания
                            </label>

                            <input id="endDate" name="endDate"
                                   type="date" class="form-control"
                                   min="${period.startDate}"
                                   value="<c:out value='${param.endDate}'/>"
                                   required>

                            <div class="form-text">
                                Указанная дата входит в период выступлений.
                                Новый период в этом сезоне можно начать
                                со следующего дня.
                            </div>
                        </div>

                        <div class="d-flex flex-wrap gap-2">
                            <button type="submit" class="btn btn-primary">
                                Закрыть период
                            </button>

                            <a href="/player?id=${player.playerId}"
                               class="btn btn-outline-secondary">
                                Отмена
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </section>
    </div>
</main>

<ui:footer/>

</body>
</html>