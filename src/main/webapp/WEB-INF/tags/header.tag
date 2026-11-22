<%@ tag pageEncoding="UTF-8" body-content="empty" %>
<%@ attribute name="activePage" required="false"
              type="java.lang.String" %>

<nav class="navbar navbar-expand bg-dark" data-bs-theme="dark">
    <div class="container flex-wrap gap-3">
        <a class="navbar-brand fw-bold" href="/home">
            Дружба Cup
        </a>

        <ul class="navbar-nav me-auto">
            <li class="nav-item">
                <a href="/home"
                   class="nav-link ${activePage eq 'home' ? 'active' : ''}">
                    Главная
                </a>
            </li>
            <li class="nav-item">
                <a href="/teams"
                   class="nav-link ${activePage eq 'teams' ? 'active' : ''}">
                    Команды
                </a>
            </li>
            <li class="nav-item">
                <a href="/players"
                   class="nav-link ${activePage eq 'players' ? 'active' : ''}">
                    Игроки
                </a>
            </li>
        </ul>

        <form method="post" action="/logout">
            <button type="submit" class="btn btn-outline-light">
                Выйти
            </button>
        </form>
    </div>
</nav>