<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Регистрация · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary">
<main class="container py-5">
    <div class="row justify-content-center">
        <section class="col-12 col-sm-8 col-lg-4">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <p class="text-primary fw-bold mb-2">Дружба Cup</p>
                    <h1 class="h3 mb-4">Регистрация</h1>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>

                    <form method="post" action="/signUp">
                        <div class="mb-3">
                            <label for="lastName" class="form-label">
                                Фамилия
                            </label>
                            <input id="lastName" name="lastName" type="text"
                                   class="form-control"
                                   autocomplete="family-name"
                                   maxlength="200"
                                   value="<c:out value='${param.lastName}'/>"
                                   required>
                        </div>

                        <div class="mb-3">
                            <label for="firstName" class="form-label">
                                Имя
                            </label>
                            <input id="firstName" name="firstName" type="text"
                                   class="form-control"
                                   autocomplete="given-name"
                                   maxlength="150"
                                   value="<c:out value='${param.firstName}'/>"
                                   required>
                        </div>

                        <div class="mb-3">
                            <label for="middleName" class="form-label">
                                Отчество
                            </label>
                            <input id="middleName" name="middleName" type="text"
                                   class="form-control"
                                   maxlength="150"
                                   value="<c:out value='${param.middleName}'/>"
                                   required>
                        </div>

                        <div class="mb-3">
                            <label for="email" class="form-label">
                                Электронная почта
                            </label>
                            <input id="email" name="email" type="email"
                                   class="form-control"
                                   autocomplete="email"
                                   maxlength="200"
                                   value="<c:out value='${param.email}'/>"
                                   required>
                        </div>

                        <div class="mb-4">
                            <label for="password" class="form-label">
                                Пароль
                            </label>
                            <input id="password" name="password" type="password"
                                   class="form-control"
                                   autocomplete="new-password"
                                   required>
                        </div>

                        <button type="submit" class="btn btn-primary w-100">
                            Зарегистрироваться
                        </button>
                    </form>

                    <a href="/signIn" class="d-block text-center mt-3">
                        Уже есть аккаунт? Войти
                    </a>
                </div>
            </div>
        </section>
    </div>
</main>
</body>
</html>