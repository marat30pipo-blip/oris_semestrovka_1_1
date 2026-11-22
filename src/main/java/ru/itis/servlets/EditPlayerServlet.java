package ru.itis.servlets;

import ru.itis.dto.PlayerForm;
import ru.itis.models.Player;
import ru.itis.services.PlayersService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/admin/players/edit")
public class EditPlayerServlet extends HttpServlet {

    private PlayersService playersService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        playersService = (PlayersService) config.getServletContext().getAttribute("playersService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

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

            req.getRequestDispatcher("/jsp/playerEdit.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить игрока для редактирования", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

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

            try {
                String birthDateParameter = req.getParameter("birthDate");
                String weightParameter = req.getParameter("weightKg");

                LocalDate birthDate = null;
                BigDecimal weightKg = null;

                if (birthDateParameter != null && !birthDateParameter.isBlank()) {
                    birthDate = LocalDate.parse(birthDateParameter);
                }

                if (weightParameter != null && !weightParameter.isBlank()) {
                    weightKg = new BigDecimal(weightParameter.trim());
                }

                PlayerForm form = PlayerForm.builder()
                        .lastName(req.getParameter("lastName"))
                        .name(req.getParameter("name"))
                        .middleName(req.getParameter("middleName"))
                        .birthDate(birthDate)
                        .heightCm(parseOptionalInteger(req.getParameter("heightCm")))
                        .weightKg(weightKg)
                        .position(req.getParameter("position"))
                        .shirtNumber(parseOptionalInteger(req.getParameter("shirtNumber")))
                        .build();

                boolean updated = playersService.update(playerId, form);

                if (!updated) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                resp.sendRedirect("/players");
                return;

            } catch (NumberFormatException e) {
                req.setAttribute("message", "Рост должен быть целым числом, а вес — числом");

            } catch (DateTimeParseException e) {
                req.setAttribute("message", "Укажите корректную дату рождения");

            } catch (IllegalArgumentException e) {
                req.setAttribute("message", e.getMessage());
            }

            req.setAttribute("player", player);

            req.getRequestDispatcher("/jsp/playerEdit.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось обновить игрока", e);
        }
    }

    private Integer parseOptionalInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Integer.parseInt(value.trim());
    }
}