package ru.itis.servlets;

import ru.itis.services.MatchRostersService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/matches/rosters/delete")
public class DeleteMatchRosterPlayerServlet extends HttpServlet {

    private MatchRostersService rostersService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        rostersService = (MatchRostersService) config.getServletContext().getAttribute("matchRostersService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int matchId;
        int teamId;
        int playerId;

        try {
            matchId = Integer.parseInt(req.getParameter("matchId"));
            teamId = Integer.parseInt(req.getParameter("teamId"));
            playerId = Integer.parseInt(req.getParameter("playerId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (matchId <= 0 || teamId <= 0 || playerId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            boolean deleted = rostersService.delete(matchId, teamId, playerId);

            if (!deleted) {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "Игрок отсутствует в составе или ему записаны голы в этом матче. Проверьте состав и записи голов.");
                return;
            }

            resp.sendRedirect("/match?id=" + matchId);

        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (SQLException e) {
            throw new ServletException("Не удалось удалить игрока из состава", e);
        }
    }
}