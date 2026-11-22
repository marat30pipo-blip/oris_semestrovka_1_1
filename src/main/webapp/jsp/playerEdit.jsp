<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<c:set var="values" value="${player}"/>
<c:if test="${pageContext.request.method eq 'POST'}">
    <c:set var="values" value="${param}"/>
</c:if>

<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Редактирование игрока · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header activePage="players"/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <div class="row justify-content-center">
        <section class="col-12 col-lg-7">
            <div class="card">
                <div class="card-body p-4">
                    <h1 class="h3 fw-bold mb-4">
                        Редактирование игрока
                    </h1>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <c:if test="${player.photoId ne null}">
                        <div class="mb-4">
                            <img src="/files?id=${player.photoId}"
                                 alt="Фотография игрока"
                                 width="120" height="120"
                                 class="rounded object-fit-cover">
                        </div>
                    </c:if>

                    <form method="post" action="/admin/players/edit">
                        <input type="hidden" name="id"
                               value="${player.playerId}">

                        <div class="mb-3">
                            <label for="lastName" class="form-label">
                                Фамилия
                            </label>

                            <input id="lastName" name="lastName" type="text"
                                   class="form-control" maxlength="200"
                                   autocomplete="family-name" required
                                   value="<c:out value='${values.lastName}'/>">
                        </div>

                        <div class="mb-3">
                            <label for="name" class="form-label">
                                Имя
                            </label>

                            <input id="name" name="name" type="text"
                                   class="form-control" maxlength="150"
                                   autocomplete="given-name" required
                                   value="<c:out value='${values.name}'/>">
                        </div>

                        <div class="mb-3">
                            <label for="middleName" class="form-label">
                                Отчество
                            </label>

                            <input id="middleName" name="middleName" type="text"
                                   class="form-control" maxlength="200"
                                   autocomplete="additional-name"
                                   value="<c:out value='${values.middleName}'/>">
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-12 col-md-6">
                                <label for="birthDate" class="form-label">
                                    Дата рождения
                                </label>

                                <input id="birthDate" name="birthDate"
                                       type="date" class="form-control"
                                       value="<c:out value='${values.birthDate}'/>">
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="position" class="form-label">
                                    Позиция
                                </label>

                                <input id="position" name="position" type="text"
                                       class="form-control" maxlength="50"
                                       placeholder="Например, защитник"
                                       value="<c:out value='${values.position}'/>">
                            </div>
                        </div>

                        <div class="row g-3 mb-4">
                            <div class="col-12 col-md-6">
                                <label for="heightCm" class="form-label">
                                    Рост, см
                                </label>

                                <input id="heightCm" name="heightCm"
                                       type="number" min="1" step="1"
                                       class="form-control"
                                       value="<c:out value='${values.heightCm}'/>">
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="weightKg" class="form-label">
                                    Вес, кг
                                </label>

                                <input id="weightKg" name="weightKg"
                                       type="number" min="0.1" step="any"
                                       class="form-control"
                                       value="<c:out value='${values.weightKg}'/>">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="shirtNumber" class="form-label">
                                Номер игрока
                            </label>

                            <input id="shirtNumber" name="shirtNumber"
                                   type="number" min="1" max="99" step="1"
                                   class="form-control"
                                   value="<c:out value='${values.shirtNumber}'/>">

                            <div class="form-text">
                                Необязательное поле, от 1 до 99.
                            </div>
                        </div>

                        <div class="d-flex flex-wrap gap-2">
                            <button type="submit" class="btn btn-primary">
                                Сохранить изменения
                            </button>

                            <a href="/players" class="btn btn-outline-secondary">
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
