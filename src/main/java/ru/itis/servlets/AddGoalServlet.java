package ru.itis.servlets;

import ru.itis.dto.GoalForm;
import ru.itis.dto.MatchDetailsDto;
import ru.itis.services.GoalsService;
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

@WebServlet("/admin/matches/goals/add")
public class AddGoalServlet extends HttpServlet {

    private GoalsService goalsService;
    private MatchesService matchesService;
    private MatchRostersService rostersService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        goalsService = (GoalsService) config.getServletContext().getAttribute("goalsService");
        matchesService = (MatchesService) config.getServletContext().getAttribute("matchesService");
        rostersService = (MatchRostersService) config.getServletContext().getAttribute("matchRostersService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int matchId;

        try {
            matchId = Integer.parseInt(req.getParameter("matchId"));
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

            req.getRequestDispatcher("/jsp/goalForm.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить данные для добавления гола", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        int matchId;

        try {
            matchId = Integer.parseInt(req.getParameter("matchId"));
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

            try {
                int playerId = Integer.parseInt(req.getParameter("playerId"));

                int teamId = Integer.parseInt(req.getParameter("teamId"));

                int minute = Integer.parseInt(req.getParameter("minute"));

                GoalForm form = GoalForm.builder()
                        .matchId(matchId)
                        .playerId(playerId)
                        .teamId(teamId)
                        .minute(minute)
                        .build();

                goalsService.save(form);

                resp.sendRedirect("/match?id=" + matchId);
                return;

            } catch (NumberFormatException e) {
                req.setAttribute("message", "Выберите игрока, команду и укажите минуту целым числом");

            } catch (IllegalArgumentException e) {
                req.setAttribute("message", e.getMessage());
            }

            req.setAttribute("match", match);

            req.setAttribute("team1Players", rostersService.findByMatchIdAndTeamId(matchId, match.getTeam1Id()));

            req.setAttribute("team2Players", rostersService.findByMatchIdAndTeamId(matchId, match.getTeam2Id()));

            req.getRequestDispatcher("/jsp/goalForm.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось сохранить гол", e);
        }
    }
}