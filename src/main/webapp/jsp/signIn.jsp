<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Вход · Дружба Cup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>

<body class="bg-body-tertiary">
<main class="container py-5">
    <div class="row justify-content-center">
        <section class="col-12 col-sm-8 col-lg-4">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <p class="text-primary fw-bold mb-2">Дружба Cup</p>
                    <h1 class="h3 mb-4">Вход</h1>
                    <c:if test="${not empty message}">
                        <div class="alert alert-danger">
                            <c:out value="${message}"/>
                        </div>
                    </c:if>
                    <form method="post" action="${pageContext.request.contextPath}/signIn">
                        <div class="mb-3">
                            <label for="email" class="form-label">Электронная почта</label>
                            <input id="email" name="email" type="email"
                                   class="form-control" autocomplete="username" required>
                        </div>

                        <div class="mb-4">
                            <label for="password" class="form-label">Пароль</label>
                            <input id="password" name="password" type="password"
                                   class="form-control" autocomplete="current-password" required>
                        </div>

                        <button type="submit" class="btn btn-primary w-100">Войти</button>
                    </form>
                    <a href="/signUp">Создать аккаунт</a>
                </div>
            </div>
        </section>
    </div>
</main>
</body>
</html>
