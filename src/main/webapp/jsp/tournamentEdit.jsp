<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<c:set var="values" value="${tournament}"/>
<c:if test="${pageContext.request.method eq 'POST'}">
    <c:set var="values" value="${param}"/>
</c:if>

<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Редактирование турнира · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <div class="row justify-content-center">
        <section class="col-12 col-lg-7">
            <div class="card">
                <div class="card-body p-4">
                    <h1 class="h3 fw-bold mb-3">
                        Редактирование турнира
                    </h1>

                    <p class="text-body-secondary">
                        Тип: <c:out value="${tournament.type}"/>
                    </p>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <form method="post" action="/admin/tournaments/edit">
                        <input type="hidden" name="id"
                               value="${tournament.tournamentId}">

                        <div class="mb-3">
                            <label for="name" class="form-label">
                                Название турнира
                            </label>

                            <input id="name" name="name" type="text"
                                   class="form-control"
                                   maxlength="150" required
                                   value="<c:out value='${values.name}'/>">
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-12 col-md-6">
                                <label for="startDate" class="form-label">
                                    Дата начала
                                </label>

                                <input id="startDate" name="startDate"
                                       type="date" class="form-control"
                                       required
                                       value="<c:out value='${values.startDate}'/>">
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="endDate" class="form-label">
                                    Дата окончания
                                </label>

                                <input id="endDate" name="endDate"
                                       type="date" class="form-control"
                                       required
                                       value="<c:out value='${values.endDate}'/>">
                            </div>
                        </div>

                        <div class="mb-4">
                            <label for="description" class="form-label">
                                Описание
                            </label>

                            <textarea id="description" name="description"
                                      class="form-control" rows="4"
                                      maxlength="1000"><c:out value="${values.description}"/></textarea>

                            <div class="form-text">
                                Необязательное поле, до 1000 символов.
                            </div>
                        </div>

                        <div class="d-flex flex-wrap gap-2">
                            <button type="submit" class="btn btn-primary">
                                Сохранить изменения
                            </button>

                            <a href="/tournament?id=${tournament.tournamentId}"
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