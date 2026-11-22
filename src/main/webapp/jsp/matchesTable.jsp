<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${empty matches}">
        <p class="text-body-secondary mb-0">
            Матчей с выбранным статусом нет.
        </p>
    </c:when>

    <c:otherwise>
        <div class="table-responsive">
            <table class="table align-middle mb-0">
                <thead>
                <tr>
                    <th scope="col">Дата</th>
                    <th scope="col">Время</th>
                    <c:if test="${tournament.type eq 'Кубок'}">
                        <th scope="col">Стадия</th>
                    </c:if>
                    <th scope="col">Команды</th>
                    <th scope="col">Счёт</th>
                    <th scope="col">Статус</th>

                    <c:if test="${sessionScope.role eq 'ADMIN'}">
                        <th scope="col">Действия</th>
                    </c:if>
                </tr>
                </thead>

                <tbody>
                <c:forEach items="${matches}" var="match">
                    <tr>
                        <td>
                            <c:out value="${match.matchDate}"
                                   default="Уточняется"/>
                        </td>
                        <td>
                            <c:out value="${match.startTime}"
                                   default="Уточняется"/>
                        </td>
                        <c:if test="${tournament.type eq 'Кубок'}">
                            <td>
                                <c:choose>
                                    <c:when test="${not empty match.stage}">
                                        <c:out value="${match.stage}"/>
                                    </c:when>
                                    <c:otherwise>Не указана</c:otherwise>
                                </c:choose>
                            </td>
                        </c:if>
                        <td>
                            <a href="/match?id=${match.matchId}" class="link-primary">
                                <c:out value="${match.team1Name}"/>
                                —
                                <c:out value="${match.team2Name}"/>
                            </a>
                        </td>
                        <td class="text-nowrap">
                            <c:choose>
                                <c:when test="${match.score1 ne null
                                                and match.score2 ne null}">
                                    <strong>
                                            ${match.score1} : ${match.score2}
                                    </strong>
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>

                            <c:if test="${tournament.type eq 'Кубок'
                                and match.penalty1 ne null
                                and match.penalty2 ne null}">
                                <div class="small text-body-secondary mt-1">
                                    По пенальти:
                                        ${match.penalty1} : ${match.penalty2}
                                </div>
                            </c:if>

                        </td>
                        <td>
                            <c:out value="${match.status}"/>
                        </td>

                        <c:if test="${sessionScope.role eq 'ADMIN'}">
                            <td>
                                <div class="d-flex flex-wrap gap-2">
                                    <a href="/admin/matches/edit?id=${match.matchId}"
                                       class="btn btn-outline-primary btn-sm">
                                        Редактировать
                                    </a>

                                    <form method="post"
                                          action="/admin/matches/delete">
                                        <input type="hidden" name="id"
                                               value="${match.matchId}">
                                        <button type="submit"
                                                class="btn btn-outline-danger btn-sm">
                                            Удалить
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>