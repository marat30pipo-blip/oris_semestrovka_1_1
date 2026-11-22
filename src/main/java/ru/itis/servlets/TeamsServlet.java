package ru.itis.servlets;

import ru.itis.models.Team;
import ru.itis.services.TeamsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/teams")
public class TeamsServlet extends HttpServlet {

    private TeamsService teamsService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Team> teams = teamsService.findAll();
            req.setAttribute("teams", teams);

            req.getRequestDispatcher("/jsp/teams.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить команды", e);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        teamsService = (TeamsService) config.getServletContext().getAttribute("teamsService");
    }
}
