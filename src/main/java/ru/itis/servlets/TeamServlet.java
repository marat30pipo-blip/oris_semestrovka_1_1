package ru.itis.servlets;

import ru.itis.models.Team;
import ru.itis.services.PlayersService;
import ru.itis.services.SeasonsService;
import ru.itis.services.TeamsService;
import ru.itis.services.TournamentsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@WebServlet("/team")
public class TeamServlet extends HttpServlet {

    private TeamsService teamsService;
    private PlayersService playersService;
    private SeasonsService seasonsService;
    private TournamentsService tournamentsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        teamsService = (TeamsService) config.getServletContext().getAttribute("teamsService");
        playersService = (PlayersService) config.getServletContext().getAttribute("playersService");
        seasonsService = (SeasonsService) config.getServletContext().getAttribute("seasonsService");
        tournamentsService = (TournamentsService) config.getServletContext().getAttribute("tournamentsService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int teamId;

        try {
            teamId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (teamId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Team team = teamsService.findById(teamId);

            if (team == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            List<Integer> years = seasonsService.findAllYears();

            req.setAttribute("team", team);
            req.setAttribute("years", years);

            String yearParameter = req.getParameter("seasonYear");
            Integer seasonYear = null;

            if (yearParameter != null) {
                try {
                    seasonYear = Integer.parseInt(yearParameter);
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                if (!years.contains(seasonYear)) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

            } else if (!years.isEmpty()) {
                seasonYear = years.get(0);
            }

            if (seasonYear == null) {
                req.setAttribute("players", Collections.emptyList());
                req.setAttribute("tournaments", Collections.emptyList());

            } else {
                req.setAttribute("seasonYear", seasonYear);
                req.setAttribute("players", playersService.findByTeamIdAndSeasonYear(teamId, seasonYear));
                req.setAttribute("tournaments", tournamentsService.findByTeamIdAndSeasonYear(teamId, seasonYear));
            }

            req.getRequestDispatcher("/jsp/team.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить страницу команды", e);
        }
    }
}