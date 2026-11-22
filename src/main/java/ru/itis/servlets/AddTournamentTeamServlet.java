package ru.itis.servlets;

import ru.itis.models.Team;
import ru.itis.models.Tournament;
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

@WebServlet("/admin/tournaments/teams/add")
public class AddTournamentTeamServlet extends HttpServlet {

    private TeamsService teamsService;
    private TournamentsService tournamentsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        teamsService = (TeamsService) config.getServletContext()
                .getAttribute("teamsService");

        tournamentsService = (TournamentsService) config.getServletContext()
                .getAttribute("tournamentsService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParameter = req.getParameter("tournamentId");

        if (idParameter == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int tournamentId;

        try {
            tournamentId = Integer.parseInt(idParameter);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (tournamentId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Tournament tournament =
                    tournamentsService.findById(tournamentId);

            if (tournament == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            List<Team> teams = teamsService.findAll();

            req.setAttribute("tournament", tournament);
            req.setAttribute("teams", teams);

            req.getRequestDispatcher("/jsp/tournamentTeamForm.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить данные для добавления команды", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String tournamentIdParameter = req.getParameter("tournamentId");
        String teamIdParameter = req.getParameter("teamId");

        int tournamentId;
        int teamId;

        try {
            tournamentId = Integer.parseInt(tournamentIdParameter);
            teamId = Integer.parseInt(teamIdParameter);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (tournamentId <= 0 || teamId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Tournament tournament = tournamentsService.findById(tournamentId);

            if (tournament == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String message;

            try {
                boolean added = teamsService.addToTournament(tournamentId, teamId);

                if (added) {
                    resp.sendRedirect("/tournament?id=" + tournamentId);
                    return;
                }

                message = "Команда уже участвует в турнире";

            } catch (IllegalArgumentException e) {
                message = e.getMessage();
            }

            req.setAttribute("message", message);
            req.setAttribute("tournament", tournament);
            req.setAttribute("teams", teamsService.findAll());

            req.getRequestDispatcher("/jsp/tournamentTeamForm.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось добавить команду в турнир", e);
        }
    }
}