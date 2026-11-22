package ru.itis.servlets;

import ru.itis.dto.MatchForm;
import ru.itis.services.MatchesService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/matches/delete")
public class DeleteMatchServlet extends HttpServlet {

    private MatchesService matchesService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        matchesService = (MatchesService) config.getServletContext().getAttribute("matchesService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
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
            MatchForm match = matchesService.findById(matchId);

            if (match == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            int tournamentId = match.getTournamentId();

            boolean deleted = matchesService.deleteById(matchId);

            if (!deleted) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            resp.sendRedirect("/tournament?id=" + tournamentId);

        } catch (SQLException e) {
            throw new ServletException("Не удалось удалить матч", e);
        }
    }
}