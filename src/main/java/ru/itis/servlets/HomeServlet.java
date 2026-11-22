package ru.itis.servlets;

import ru.itis.dto.MatchDto;
import ru.itis.models.Tournament;
import ru.itis.services.MatchesService;
import ru.itis.services.SeasonsService;
import ru.itis.services.TournamentsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private MatchesService matchesService;
    private SeasonsService seasonsService;
    private TournamentsService tournamentsService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            List<Integer> years = seasonsService.findAllYears();
            req.setAttribute("years", years);

            List<MatchDto> matches = new ArrayList<>();
            List<Tournament> tournaments = new ArrayList<>();

            if (!years.isEmpty()) {
                int seasonYear = years.get(0);

                HttpSession session = req.getSession();
                String yearParameter = req.getParameter("seasonYear");

                if (yearParameter != null) {
                    try {
                        seasonYear = Integer.parseInt(yearParameter);
                    } catch (NumberFormatException e) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }

                    if (!years.contains(seasonYear)) {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                } else {
                    Integer savedSeasonYear = (Integer) session.getAttribute("selectedSeasonYear");

                    if (savedSeasonYear != null && years.contains(savedSeasonYear)) {
                        seasonYear = savedSeasonYear;
                    }
                }

                tournaments = tournamentsService.findBySeasonYear(seasonYear);
                matches = matchesService.findBySeasonYear(seasonYear);

                session.setAttribute("selectedSeasonYear", seasonYear);
                req.setAttribute("seasonYear", seasonYear);
            }

            List<MatchDto> results = matchesService.findResults(matches);

            req.setAttribute("matches", matches);
            req.setAttribute("results", results);
            req.setAttribute("tournaments", tournaments);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить данные этой страницы", e);
        }
        req.getRequestDispatcher("/jsp/home.jsp").forward(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        matchesService = (MatchesService) config.getServletContext().getAttribute("matchesService");
        seasonsService = (SeasonsService) config.getServletContext().getAttribute("seasonsService");
        tournamentsService = (TournamentsService) config.getServletContext().getAttribute("tournamentsService");
    }
}