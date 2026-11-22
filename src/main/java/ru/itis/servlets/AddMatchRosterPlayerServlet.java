package ru.itis.servlets;

import ru.itis.dto.MatchDetailsDto;
import ru.itis.services.MatchesService;
import ru.itis.services.MatchRostersService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/matches/rosters/add")
public class AddMatchRosterPlayerServlet extends HttpServlet {

    private MatchesService matchesService;
    private MatchRostersService rostersService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        matchesService = (MatchesService) config.getServletContext().getAttribute("matchesService");
        rostersService = (MatchRostersService) config.getServletContext().getAttribute("matchRostersService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int matchId;
        int teamId;

        try {
            matchId = Integer.parseInt(req.getParameter("matchId"));
            teamId = Integer.parseInt(req.getParameter("teamId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (matchId <= 0 || teamId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            MatchDetailsDto match = matchesService.findDetailsById(matchId);

            if (match == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (teamId != match.getTeam1Id() && teamId != match.getTeam2Id()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            req.setAttribute("match", match);
            req.setAttribute("teamId", teamId);

            if (teamId == match.getTeam1Id()) {
                req.setAttribute("teamName", match.getTeam1Name());
            } else {
                req.setAttribute("teamName", match.getTeam2Name());
            }

            if (match.getMatchDate() == null) {
                req.setAttribute("message", "Сначала укажите дату матча");
            } else {
                req.setAttribute("players", rostersService.findEligiblePlayers(matchId, teamId));
            }

            req.getRequestDispatcher("/jsp/matchRosterForm.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить игроков для состава", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        int matchId;
        int teamId;

        try {
            matchId = Integer.parseInt(req.getParameter("matchId"));
            teamId = Integer.parseInt(req.getParameter("teamId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (matchId <= 0 || teamId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            MatchDetailsDto match = matchesService.findDetailsById(matchId);

            if (match == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (teamId != match.getTeam1Id() && teamId != match.getTeam2Id()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            req.setAttribute("match", match);
            req.setAttribute("teamId", teamId);

            if (teamId == match.getTeam1Id()) {
                req.setAttribute("teamName", match.getTeam1Name());
            } else {
                req.setAttribute("teamName", match.getTeam2Name());
            }

            if (match.getMatchDate() == null) {
                req.setAttribute("message", "Сначала укажите дату матча");

            } else {
                try {
                    int playerId = Integer.parseInt(req.getParameter("playerId"));

                    boolean added = rostersService.save(matchId, teamId, playerId);

                    if (added) {
                        resp.sendRedirect("/match?id=" + matchId);
                        return;
                    }

                    req.setAttribute("message", "Игрок уже добавлен в состав этого матча");

                } catch (NumberFormatException e) {
                    req.setAttribute("message", "Выберите игрока");

                } catch (IllegalArgumentException e) {
                    req.setAttribute("message", e.getMessage());
                }

                req.setAttribute("players", rostersService.findEligiblePlayers(matchId, teamId));
            }

            req.getRequestDispatcher("/jsp/matchRosterForm.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось добавить игрока в состав", e);
        }
    }
}