<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Фотография игрока · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<ui:header activePage="players"/>

<main class="container py-4 py-lg-5 flex-grow-1">
    <div class="row justify-content-center">
        <section class="col-12 col-md-8 col-lg-6">
            <div class="card">
                <div class="card-body p-4">
                    <h1 class="h3 fw-bold mb-3">
                        Фотография игрока
                    </h1>

                    <p class="text-body-secondary">
                        <c:out value="${player.lastName}"/>
                        <c:out value="${player.name}"/>
                        <c:out value="${player.middleName}"/>
                    </p>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <div class="mb-4">
                        <c:choose>
                            <c:when test="${player.photoId ne null}">
                                <img src="/files?id=${player.photoId}"
                                     alt="Текущая фотография игрока"
                                     width="160" height="160"
                                     class="rounded object-fit-cover">
                            </c:when>

                            <c:otherwise>
                                <p class="text-body-secondary">
                                    Фотография пока не добавлена.
                                </p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <form method="post" action="/admin/players/photo"
                          enctype="multipart/form-data">

                        <input type="hidden" name="id"
                               value="${player.playerId}">

                        <div class="mb-4">
                            <label for="photo" class="form-label">
                                Новая фотография
                            </label>

                            <input id="photo" name="photo" type="file"
                                   class="form-control"
                                   accept="image/png,image/jpeg" required>

                            <div class="form-text">
                                PNG или JPEG, до 5 МБ.
                                При ошибке файл потребуется выбрать повторно.
                            </div>
                        </div>

                        <div class="d-flex flex-wrap gap-2">
                            <button type="submit" class="btn btn-primary">
                                Сохранить фотографию
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