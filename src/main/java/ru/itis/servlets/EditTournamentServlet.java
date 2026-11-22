package ru.itis.servlets;

import ru.itis.dto.TournamentForm;
import ru.itis.models.Tournament;
import ru.itis.services.TournamentsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/admin/tournaments/edit")
public class EditTournamentServlet extends HttpServlet {

    private TournamentsService tournamentsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        tournamentsService = (TournamentsService) config.getServletContext()
                .getAttribute("tournamentsService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int tournamentId;

        try {
            tournamentId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
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

            req.setAttribute("tournament", tournament);

            req.getRequestDispatcher("/jsp/tournamentEdit.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить турнир для редактирования", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        int tournamentId;

        try {
            tournamentId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
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

            try {
                String startDateParameter = req.getParameter("startDate");
                String endDateParameter = req.getParameter("endDate");

                LocalDate startDate = null;
                LocalDate endDate = null;

                if (startDateParameter != null
                        && !startDateParameter.isBlank()) {
                    startDate = LocalDate.parse(startDateParameter);
                }

                if (endDateParameter != null
                        && !endDateParameter.isBlank()) {
                    endDate = LocalDate.parse(endDateParameter);
                }

                TournamentForm form = TournamentForm.builder()
                        .name(req.getParameter("name"))
                        .type(tournament.getType())
                        .startDate(startDate)
                        .endDate(endDate)
                        .description(req.getParameter("description"))
                        .build();

                boolean updated = tournamentsService.update(tournamentId, form);

                if (!updated) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                resp.sendRedirect("/tournament?id=" + tournamentId);
                return;

            } catch (DateTimeParseException e) {
                req.setAttribute("message", "Укажите корректные даты начала и окончания");

            } catch (IllegalArgumentException e) {
                req.setAttribute("message", e.getMessage());
            }

            req.setAttribute("tournament", tournament);

            req.getRequestDispatcher("/jsp/tournamentEdit.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось обновить турнир", e);
        }
    }
}