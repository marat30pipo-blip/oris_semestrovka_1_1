package ru.itis.servlets;

import ru.itis.models.Tournament;
import ru.itis.services.MatchesService;
import ru.itis.services.TournamentsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/matches/filter")
public class MatchesFilterServlet extends HttpServlet {

    private MatchesService matchesService;
    private TournamentsService tournamentsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        matchesService = (MatchesService) config.getServletContext().getAttribute("matchesService");
        tournamentsService = (TournamentsService) config.getServletContext().getAttribute("tournamentsService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int tournamentId;

        try {
            tournamentId = Integer.parseInt(req.getParameter("tournamentId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (tournamentId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String status = req.getParameter("status");

        try {
            Tournament tournament = tournamentsService.findById(tournamentId);

            if (tournament == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            req.setAttribute("tournament", tournament);
            req.setAttribute("matches", matchesService.findByTournamentIdAndStatus(tournamentId, status));

        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить матчи", e);
        }

        req.getRequestDispatcher("/jsp/matchesTable.jsp").forward(req, resp);
    }
}