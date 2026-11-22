package ru.itis.servlets;

import ru.itis.dto.MatchDetailsDto;
import ru.itis.services.GoalsService;
import ru.itis.services.MatchRostersService;
import ru.itis.services.MatchesService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/match")
public class MatchServlet extends HttpServlet {

    private MatchesService matchesService;
    private MatchRostersService rostersService;
    private GoalsService goalsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        matchesService = (MatchesService) config.getServletContext().getAttribute("matchesService");
        rostersService = (MatchRostersService) config.getServletContext().getAttribute("matchRostersService");
        goalsService = (GoalsService) config.getServletContext().getAttribute("goalsService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int matchId;

        try {
            matchId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (matchId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            MatchDetailsDto match = matchesService.findDetailsById(matchId);

            if (match == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            req.setAttribute("match", match);
            req.setAttribute("team1Players", rostersService.findByMatchIdAndTeamId(matchId, match.getTeam1Id()));
            req.setAttribute("team2Players", rostersService.findByMatchIdAndTeamId(matchId, match.getTeam2Id()));
            req.setAttribute("goals", goalsService.findByMatchId(matchId));

            req.getRequestDispatcher("/jsp/match.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить матч", e);
        }
    }
}
