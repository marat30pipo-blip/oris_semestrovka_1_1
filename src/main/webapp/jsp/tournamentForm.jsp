<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Создание турнира · Дружба Cup</title>
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
                    <h1 class="h3 fw-bold mb-4">Создание турнира</h1>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <c:choose>
                        <c:when test="${empty years}">
                            <p class="text-body-secondary">
                                Сначала добавьте хотя бы один сезон.
                            </p>

                            <a href="/admin/seasons/add"
                               class="btn btn-primary">
                                Добавить сезон
                            </a>
                        </c:when>

                        <c:otherwise>
                            <form method="post" action="/admin/tournaments/add">
                                <div class="mb-3">
                                    <label for="seasonYear" class="form-label">
                                        Сезон
                                    </label>

                                    <select id="seasonYear" name="seasonYear"
                                            class="form-select" required>
                                        <option value="">Выберите сезон</option>

                                        <c:forEach items="${years}" var="year">
                                            <option value="${year}"
                                                ${param.seasonYear eq year ? 'selected' : ''}>
                                                Сезон ${year}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label for="name" class="form-label">
                                        Название турнира
                                    </label>

                                    <input id="name" name="name" type="text"
                                           class="form-control"
                                           maxlength="150" required
                                           value="<c:out value='${param.name}'/>">
                                </div>

                                <div class="mb-3">
                                    <label for="type" class="form-label">
                                        Тип турнира
                                    </label>

                                    <select id="type" name="type"
                                            class="form-select" required>
                                        <option value="">Выберите тип</option>
                                        <option value="Чемпионат"
                                            ${param.type eq 'Чемпионат' ? 'selected' : ''}>
                                            Чемпионат
                                        </option>
                                        <option value="Кубок"
                                            ${param.type eq 'Кубок' ? 'selected' : ''}>
                                            Кубок
                                        </option>
                                    </select>
                                </div>

                                <div class="row g-3 mb-3">
                                    <div class="col-12 col-md-6">
                                        <label for="startDate" class="form-label">
                                            Дата начала
                                        </label>

                                        <input id="startDate" name="startDate"
                                               type="date" class="form-control"
                                               required
                                               value="<c:out value='${param.startDate}'/>">
                                    </div>

                                    <div class="col-12 col-md-6">
                                        <label for="endDate" class="form-label">
                                            Дата окончания
                                        </label>

                                        <input id="endDate" name="endDate"
                                               type="date" class="form-control"
                                               required
                                               value="<c:out value='${param.endDate}'/>">
                                    </div>
                                </div>

                                <div class="mb-4">
                                    <label for="description" class="form-label">
                                        Описание
                                    </label>

                                    <textarea id="description" name="description"
                                              class="form-control" rows="4"
                                              maxlength="1000"><c:out value="${param.description}"/></textarea>

                                    <div class="form-text">
                                        Необязательное поле, до 1000 символов.
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-primary">
                                    Создать турнир
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>

                    <a href="/home" class="btn btn-outline-secondary mt-3">
                        На главную
                    </a>
                </div>
            </div>
        </section>
    </div>
</main>

<ui:footer/>

</body>
</html>