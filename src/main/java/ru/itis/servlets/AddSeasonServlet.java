package ru.itis.servlets;

import ru.itis.services.SeasonsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/seasons/add")
public class AddSeasonServlet extends HttpServlet {

    private SeasonsService seasonsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        seasonsService = (SeasonsService) config.getServletContext().getAttribute("seasonsService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.getRequestDispatcher("/jsp/seasonForm.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String yearParameter = req.getParameter("seasonYear");

        try {
            if (yearParameter == null || yearParameter.isBlank()) {
                throw new IllegalArgumentException("Введите год сезона");
            }

            int seasonYear = Integer.parseInt(yearParameter.trim());

            boolean added = seasonsService.save(seasonYear);

            if (added) {
                resp.sendRedirect("/home?seasonYear=" + seasonYear);
                return;
            }

            req.setAttribute("message", "Такой сезон уже существует");

        } catch (NumberFormatException e) {
            req.setAttribute("message", "Год должен быть целым числом");

        } catch (IllegalArgumentException e) {
            req.setAttribute("message", e.getMessage());

        } catch (SQLException e) {
            throw new ServletException("Не удалось добавить сезон", e);
        }

        req.getRequestDispatcher("/jsp/seasonForm.jsp").forward(req, resp);
    }
}
