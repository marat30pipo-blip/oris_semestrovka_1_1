package ru.itis.servlets;

import ru.itis.services.GoalsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/matches/goals/delete")
public class DeleteGoalServlet extends HttpServlet {

    private GoalsService goalsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        goalsService = (GoalsService) config.getServletContext().getAttribute("goalsService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int goalId;
        int matchId;

        try {
            goalId = Integer.parseInt(req.getParameter("goalId"));
            matchId = Integer.parseInt(req.getParameter("matchId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (goalId <= 0 || matchId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            boolean deleted = goalsService.delete(goalId, matchId);

            if (!deleted) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            resp.sendRedirect("/match?id=" + matchId);

        } catch (SQLException e) {
            throw new ServletException("Не удалось удалить запись о голе", e);
        }
    }
}
