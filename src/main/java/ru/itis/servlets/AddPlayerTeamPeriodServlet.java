package ru.itis.servlets;

import ru.itis.dto.PlayerTeamPeriodForm;
import ru.itis.models.Player;
import ru.itis.services.PlayersService;
import ru.itis.services.PlayerTeamPeriodsService;
import ru.itis.services.SeasonsService;
import ru.itis.services.TeamsService;

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

@WebServlet("/admin/players/periods/add")
public class AddPlayerTeamPeriodServlet extends HttpServlet {

    private PlayersService playersService;
    private TeamsService teamsService;
    private SeasonsService seasonsService;
    private PlayerTeamPeriodsService periodsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        playersService = (PlayersService) config.getServletContext().getAttribute("playersService");
        teamsService = (TeamsService) config.getServletContext().getAttribute("teamsService");
        seasonsService = (SeasonsService) config.getServletContext().getAttribute("seasonsService");
        periodsService = (PlayerTeamPeriodsService) config.getServletContext().getAttribute("playerTeamPeriodsService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int playerId;

        try {
            playerId = Integer.parseInt(req.getParameter("playerId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (playerId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Player player = playersService.findById(playerId);

            if (player == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            req.setAttribute("player", player);
            req.setAttribute("teams", teamsService.findAll());
            req.setAttribute("years", seasonsService.findAllYears());

            req.getRequestDispatcher("/jsp/playerTeamPeriodForm.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить данные для добавления периода", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        int playerId;

        try {
            playerId = Integer.parseInt(req.getParameter("playerId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (playerId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Player player = playersService.findById(playerId);

            if (player == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            try {
                int teamId = Integer.parseInt(req.getParameter("teamId")
                );

                int seasonYear = Integer.parseInt(req.getParameter("seasonYear")
                );

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

                PlayerTeamPeriodForm form = PlayerTeamPeriodForm.builder()
                        .playerId(playerId)
                        .teamId(teamId)
                        .seasonYear(seasonYear)
                        .startDate(startDate)
                        .endDate(endDate)
                        .build();

                boolean added = periodsService.save(form);

                if (added) {
                    resp.sendRedirect("/player?id=" + playerId);
                    return;
                }

                req.setAttribute("message", "Выбранный сезон не найден");

            } catch (NumberFormatException e) {
                req.setAttribute("message", "Выберите команду и сезон");

            } catch (DateTimeParseException e) {
                req.setAttribute("message", "Укажите корректные даты периода");

            } catch (IllegalArgumentException e) {
                req.setAttribute("message", e.getMessage());
            }

            req.setAttribute("player", player);
            req.setAttribute("teams", teamsService.findAll());
            req.setAttribute("years", seasonsService.findAllYears());

            req.getRequestDispatcher("/jsp/playerTeamPeriodForm.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось добавить период выступлений", e);
        }
    }
}
