package ru.itis.servlets;

import ru.itis.dto.MatchForm;
import ru.itis.models.Tournament;
import ru.itis.services.MatchesService;
import ru.itis.services.TeamsService;
import ru.itis.services.TournamentsService;
import ru.itis.services.VenuesService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@WebServlet("/admin/matches/edit")
public class EditMatchServlet extends HttpServlet {

    private MatchesService matchesService;
    private TeamsService teamsService;
    private TournamentsService tournamentsService;
    private VenuesService venuesService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        matchesService = (MatchesService) config.getServletContext().getAttribute("matchesService");
        teamsService = (TeamsService) config.getServletContext().getAttribute("teamsService");
        tournamentsService = (TournamentsService) config.getServletContext().getAttribute("tournamentsService");
        venuesService = (VenuesService) config.getServletContext().getAttribute("venuesService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int matchId;

        try {
            matchId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (matchId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            MatchForm form = matchesService.findById(matchId);

            if (form == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Tournament tournament = tournamentsService.findById(form.getTournamentId());

            if (tournament == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            req.setAttribute("matchId", matchId);
            req.setAttribute("form", form);
            req.setAttribute("tournament", tournament);
            req.setAttribute("teams", teamsService.findByTournamentId(form.getTournamentId()));
            req.setAttribute("venues", venuesService.findAll());

            req.getRequestDispatcher("/jsp/matchEdit.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить матч для редактирования", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        int matchId;

        try {
            matchId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (matchId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            MatchForm existingMatch = matchesService.findById(matchId);

            if (existingMatch == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            int tournamentId = existingMatch.getTournamentId();

            Tournament tournament =
                    tournamentsService.findById(tournamentId);

            if (tournament == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            try {
                int team1Id = Integer.parseInt(
                        req.getParameter("team1Id")
                );

                int team2Id = Integer.parseInt(
                        req.getParameter("team2Id")
                );

                String dateParameter = req.getParameter("matchDate");
                String timeParameter = req.getParameter("startTime");

                LocalDate matchDate = null;
                LocalTime startTime = null;

                if (dateParameter != null && !dateParameter.isBlank()) {
                    matchDate = LocalDate.parse(dateParameter);
                }

                if (timeParameter != null && !timeParameter.isBlank()) {
                    startTime = LocalTime.parse(timeParameter);
                }

                String stage = req.getParameter("stage");

                if (stage != null) {
                    stage = stage.trim();
                }

                MatchForm form = MatchForm.builder()
                        .tournamentId(tournamentId)
                        .team1Id(team1Id)
                        .team2Id(team2Id)
                        .venueId(parseOptionalInteger(req.getParameter("venueId")))
                        .matchDate(matchDate)
                        .startTime(startTime)
                        .roundNumber(parseOptionalInteger(req.getParameter("roundNumber")))
                        .status(req.getParameter("status"))
                        .score1(parseOptionalInteger(req.getParameter("score1")))
                        .score2(parseOptionalInteger(req.getParameter("score2")))
                        .penalty1(parseOptionalInteger(req.getParameter("penalty1")))
                        .penalty2(parseOptionalInteger(req.getParameter("penalty2")))
                        .stage(stage)
                        .build();

                boolean updated = matchesService.update(matchId, form);

                if (!updated) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                resp.sendRedirect("/tournament?id=" + tournamentId);
                return;

            } catch (NumberFormatException e) {
                req.setAttribute("message", "Выберите две команды и проверьте числовые поля");

            } catch (DateTimeParseException e) {
                req.setAttribute("message", "Укажите корректные дату и время");

            } catch (IllegalArgumentException e) {
                req.setAttribute("message", e.getMessage());
            }

            req.setAttribute("matchId", matchId);
            req.setAttribute("form", existingMatch);
            req.setAttribute("tournament", tournament);

            req.setAttribute("teams", teamsService.findByTournamentId(tournamentId));

            req.setAttribute("venues", venuesService.findAll());

            req.getRequestDispatcher("/jsp/matchEdit.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось обновить матч", e);
        }
    }

    private Integer parseOptionalInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Integer.parseInt(value);
    }
}
