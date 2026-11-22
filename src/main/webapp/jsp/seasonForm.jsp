<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Добавление сезона · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <div class="row justify-content-center">
        <section class="col-12 col-md-8 col-lg-5">
            <div class="card">
                <div class="card-body p-4">
                    <h1 class="h3 fw-bold mb-4">Добавление сезона</h1>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <form method="post" action="/admin/seasons/add">
                        <div class="mb-4">
                            <label for="seasonYear" class="form-label">
                                Год сезона
                            </label>

                            <input id="seasonYear" name="seasonYear"
                                   type="number" min="1000" max="9999"
                                   class="form-control"
                                   value="<c:out value='${param.seasonYear}'/>"
                                   placeholder="Например, 2027" required>

                            <div class="form-text">
                                Укажите четырёхзначный год.
                            </div>
                        </div>

                        <div class="d-flex flex-wrap gap-2">
                            <button type="submit" class="btn btn-primary">
                                Добавить сезон
                            </button>

                            <a href="/home" class="btn btn-outline-secondary">
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