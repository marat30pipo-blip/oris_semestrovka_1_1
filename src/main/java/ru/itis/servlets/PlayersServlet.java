package ru.itis.servlets;

import ru.itis.services.PlayersService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/players")
public class PlayersServlet extends HttpServlet {

    private PlayersService playersService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        playersService = (PlayersService) config.getServletContext().getAttribute("playersService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            req.setAttribute("players", playersService.findAll());
        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить игроков", e);
        }

        req.getRequestDispatcher("/jsp/players.jsp").forward(req, resp);
    }
}