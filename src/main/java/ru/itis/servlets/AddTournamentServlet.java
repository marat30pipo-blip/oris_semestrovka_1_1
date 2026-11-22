package ru.itis.servlets;

import ru.itis.dto.TournamentForm;
import ru.itis.services.SeasonsService;
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

@WebServlet("/admin/tournaments/add")
public class AddTournamentServlet extends HttpServlet {

    private TournamentsService tournamentsService;
    private SeasonsService seasonsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        tournamentsService = (TournamentsService) config.getServletContext().getAttribute("tournamentsService");
        seasonsService = (SeasonsService) config.getServletContext().getAttribute("seasonsService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            req.setAttribute("years", seasonsService.findAllYears());
        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить сезоны", e);
        }

        req.getRequestDispatcher("/jsp/tournamentForm.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        try {
            String yearParameter = req.getParameter("seasonYear");
            String startDateParameter = req.getParameter("startDate");
            String endDateParameter = req.getParameter("endDate");

            if (yearParameter == null || yearParameter.isBlank()) {
                throw new IllegalArgumentException("Выберите сезон");
            }

            int seasonYear = Integer.parseInt(yearParameter.trim());

            LocalDate startDate = null;
            LocalDate endDate = null;

            if (startDateParameter != null && !startDateParameter.isBlank()) {
                startDate = LocalDate.parse(startDateParameter);
            }

            if (endDateParameter != null && !endDateParameter.isBlank()) {
                endDate = LocalDate.parse(endDateParameter);
            }

            TournamentForm form = TournamentForm.builder()
                    .seasonYear(seasonYear)
                    .name(req.getParameter("name"))
                    .type(req.getParameter("type"))
                    .startDate(startDate)
                    .endDate(endDate)
                    .description(req.getParameter("description"))
                    .build();

            boolean added = tournamentsService.save(form);

            if (added) {
                resp.sendRedirect("/home?seasonYear=" + seasonYear);
                return;
            }

            req.setAttribute("message", "Выбранный сезон не найден");

        } catch (NumberFormatException e) {
            req.setAttribute("message", "Год должен быть целым числом");

        } catch (DateTimeParseException e) {
            req.setAttribute("message", "Укажите корректные даты начала и окончания");

        } catch (IllegalArgumentException e) {
            req.setAttribute("message", e.getMessage());

        } catch (SQLException e) {
            throw new ServletException("Не удалось создать турнир", e);
        }

        try {
            req.setAttribute("years", seasonsService.findAllYears());
        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить сезоны", e);
        }

        req.getRequestDispatcher("/jsp/tournamentForm.jsp").forward(req, resp);
    }
}