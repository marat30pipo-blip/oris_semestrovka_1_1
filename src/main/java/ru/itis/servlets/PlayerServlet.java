package ru.itis.servlets;

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

@WebServlet("/player")
public class PlayerServlet extends HttpServlet {

    private PlayersService playersService;
    private PlayerTeamPeriodsService periodsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        playersService = (PlayersService) config.getServletContext().getAttribute("playersService");
        periodsService = (PlayerTeamPeriodsService) config.getServletContext().getAttribute("playerTeamPeriodsService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int playerId;

        try {
            playerId = Integer.parseInt(req.getParameter("id"));
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
            req.setAttribute("periods", periodsService.findByPlayerId(playerId));

            req.getRequestDispatcher("/jsp/player.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить страницу игрока", e);
        }
    }
}
