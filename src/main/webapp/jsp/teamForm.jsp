<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Добавление команды · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header activePage="teams"/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <div class="row justify-content-center">
        <section class="col-12 col-md-8 col-lg-6">
            <a href="/home" class="link-primary">На главную</a>

            <div class="card mt-3">
                <div class="card-body p-4">
                    <h1 class="h3 fw-bold mb-4">Добавление команды</h1>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <form method="post" action="/admin/teams/add"
                          enctype="multipart/form-data">

                        <div class="mb-3">
                            <label for="name" class="form-label">
                                Название команды
                            </label>
                            <input id="name" name="name" type="text"
                                   class="form-control"
                                   value="<c:out value='${name}'/>"
                                   maxlength="100" required>
                        </div>

                        <div class="mb-4">
                            <label for="description" class="form-label">
                                Описание
                            </label>
                            <textarea id="description" name="description"
                                      class="form-control"
                                      rows="5"
                                      maxlength="1000"><c:out value="${description}"/></textarea>

                            <div class="form-text">
                                Необязательное поле, до 1000 символов.
                            </div>
                        </div>

                        <div class="mb-4">
                            <label for="emblem" class="form-label">
                                Эмблема команды
                            </label>
                            <input id="emblem" name="emblem" type="file"
                                   class="form-control"
                                   accept="image/png,image/jpeg">

                            <div class="form-text">
                                Необязательное поле. PNG или JPEG, до 5 МБ.
                                При ошибке отправки файл потребуется выбрать повторно.
                            </div>
                        </div>

                        <div class="d-flex flex-wrap gap-2">
                            <button type="submit" class="btn btn-primary">
                                Добавить команду
                            </button>
                            <a href="/teams" class="btn btn-outline-secondary">
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