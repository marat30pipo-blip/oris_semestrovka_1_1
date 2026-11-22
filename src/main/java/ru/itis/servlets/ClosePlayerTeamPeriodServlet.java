package ru.itis.servlets;

import ru.itis.dto.PlayerTeamPeriodDto;
import ru.itis.models.Player;
import ru.itis.services.PlayersService;
import ru.itis.services.PlayerTeamPeriodsService;

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

@WebServlet("/admin/players/periods/close")
public class ClosePlayerTeamPeriodServlet extends HttpServlet {

    private PlayerTeamPeriodsService periodsService;
    private PlayersService playersService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        periodsService = (PlayerTeamPeriodsService) config.getServletContext()
                .getAttribute("playerTeamPeriodsService");

        playersService = (PlayersService) config.getServletContext()
                .getAttribute("playersService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int appearanceId;

        try {
            appearanceId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (appearanceId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            PlayerTeamPeriodDto period =
                    periodsService.findById(appearanceId);

            if (period == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (period.getEndDate() != null) {
                resp.sendRedirect("/player?id=" + period.getPlayerId());
                return;
            }

            Player player = playersService.findById(period.getPlayerId());

            if (player == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            req.setAttribute("period", period);
            req.setAttribute("player", player);

            req.getRequestDispatcher("/jsp/playerTeamPeriodClose.jsp")
                    .forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(
                    "Не удалось загрузить период выступлений", e
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        int appearanceId;

        try {
            appearanceId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (appearanceId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            PlayerTeamPeriodDto period = periodsService.findById(appearanceId);

            if (period == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (period.getEndDate() != null) {
                resp.sendRedirect("/player?id=" + period.getPlayerId());
                return;
            }

            Player player = playersService.findById(period.getPlayerId());

            if (player == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            try {
                String endDateParameter = req.getParameter("endDate");
                LocalDate endDate = null;

                if (endDateParameter != null && !endDateParameter.isBlank()) {
                    endDate = LocalDate.parse(endDateParameter);
                }

                boolean closed = periodsService.closePeriod(appearanceId, endDate);

                if (!closed) {
                    resp.sendError(HttpServletResponse.SC_CONFLICT, "Период уже закрыт или удалён. Обновите страницу.");
                    return;
                }

                resp.sendRedirect("/player?id=" + period.getPlayerId());
                return;

            } catch (DateTimeParseException e) {
                req.setAttribute("message", "Укажите корректную дату окончания");

            } catch (IllegalArgumentException e) {
                req.setAttribute("message", e.getMessage());
            }

            req.setAttribute("period", period);
            req.setAttribute("player", player);

            req.getRequestDispatcher("/jsp/playerTeamPeriodClose.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось закрыть период выступлений", e);
        }
    }
}