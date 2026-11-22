package ru.itis.servlets;

import ru.itis.dto.MatchDto;
import ru.itis.dto.StandingDto;
import ru.itis.models.Team;
import ru.itis.models.Tournament;
import ru.itis.services.MatchesService;
import ru.itis.services.StandingsService;
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
import java.util.List;

@WebServlet("/tournament")
public class TournamentServlet extends HttpServlet {

    private TournamentsService tournamentsService;
    private MatchesService matchesService;
    private TeamsService teamsService;
    private StandingsService standingsService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String idParameter = req.getParameter("id");

        if (idParameter == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST); // 400 ошибка
            return;
        }

        int tournamentId;

        try {
            tournamentId = Integer.parseInt(idParameter);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST); // 400 ошибка
            return;
        }

        if (tournamentId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Tournament tournament = tournamentsService.findById(tournamentId);
            if (tournament == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            List<MatchDto> matches = matchesService.findByTournamentId(tournamentId);
            List<Team> teams = teamsService.findByTournamentId(tournamentId);

            if ("Чемпионат".equals(tournament.getType())) {
                List<StandingDto> standings = standingsService.calculate(teams, matches);
                req.setAttribute("standings", standings);
            }

            req.setAttribute("matches", matches);
            req.setAttribute("tournament", tournament);
            req.setAttribute("teams", teams);

            req.getRequestDispatcher("/jsp/tournament.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить турнир" ,e);
        }



    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        tournamentsService = (TournamentsService) config.getServletContext().getAttribute("tournamentsService");
        matchesService = (MatchesService) config.getServletContext().getAttribute("matchesService");
        teamsService = (TeamsService) config.getServletContext().getAttribute("teamsService");
        standingsService = (StandingsService) config.getServletContext().getAttribute("standingsService");
    }
}
