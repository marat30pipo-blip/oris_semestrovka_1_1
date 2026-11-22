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

@WebServlet("/admin/teams/edit")
public class EditTeamServlet extends HttpServlet {

    private TeamsService teamsService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String idParameter = req.getParameter("id");

        if (idParameter == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int teamId;

        try {
            teamId = Integer.parseInt(idParameter);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (teamId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Team team = teamsService.findById(teamId);

            if (team == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            req.setAttribute("teamId", team.getTeamId());
            req.setAttribute("name", team.getName());
            req.setAttribute("description", team.getDescription());

            req.getRequestDispatcher("/jsp/teamEdit.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить команду для редактирования", e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String idParameter = req.getParameter("id");

        if (idParameter == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int teamId;

        try {
            teamId = Integer.parseInt(idParameter);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (teamId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String name = req.getParameter("name");
        String description = req.getParameter("description");

        req.setAttribute("teamId", teamId);
        req.setAttribute("name", name);
        req.setAttribute("description", description);

        try {
            boolean updated = teamsService.update(teamId, name, description);
            if (!updated) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.sendRedirect("/teams");
            return;
        } catch (IllegalArgumentException e) {
            req.setAttribute("message", e.getMessage());

        } catch (SQLException e) {
            throw new ServletException(
                    "Не удалось обновить команду", e
            );
        }

        req.getRequestDispatcher("/jsp/teamEdit.jsp").forward(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        teamsService = (TeamsService) config.getServletContext().getAttribute("teamsService");
    }
}
