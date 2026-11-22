package ru.itis.servlets;

import ru.itis.dto.PlayerForm;
import ru.itis.services.FileService;
import ru.itis.services.PlayersService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/admin/players/add")
@MultipartConfig(
        maxFileSize = 5L * 1024 * 1024,
        maxRequestSize = 6L * 1024 * 1024
)
public class AddPlayerServlet extends HttpServlet {

    private PlayersService playersService;
    private FileService fileService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        playersService = (PlayersService) config.getServletContext().getAttribute("playersService");
        fileService = (FileService) config.getServletContext().getAttribute("fileService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.getRequestDispatcher("/jsp/playerForm.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        Part photo;

        try {
            photo = req.getPart("photo");
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "Фотография должна быть не больше 5 МБ, а весь запрос — не больше 6 МБ");
            return;
        }

        Long photoId = null;
        boolean playerSaved = false;

        try {
            String birthDateParameter = req.getParameter("birthDate");
            String weightParameter = req.getParameter("weightKg");

            LocalDate birthDate = null;
            BigDecimal weightKg = null;

            if (birthDateParameter != null
                    && !birthDateParameter.isBlank()) {
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

            playersService.validate(form);

            if (photo != null
                    && photo.getSubmittedFileName() != null
                    && !photo.getSubmittedFileName().isBlank()) {

                try (InputStream inputStream = photo.getInputStream()) {
                    photoId = fileService.saveFileToStorage(
                            inputStream,
                            photo.getSubmittedFileName(),
                            photo.getContentType(),
                            photo.getSize()
                    );
                }
            }

            playersService.save(form, photoId);
            playerSaved = true;

        } catch (NumberFormatException e) {
            req.setAttribute("message", "Рост должен быть целым числом, а вес — числом");

        } catch (DateTimeParseException e) {
            req.setAttribute("message", "Укажите корректную дату рождения");

        } catch (IllegalArgumentException e) {
            req.setAttribute("message", e.getMessage());

        } catch (SQLException | IOException e) {
            throw new ServletException("Не удалось сохранить игрока или фотографию", e);

        } finally {
            if (photoId != null && !playerSaved) {
                try {
                    fileService.deleteFile(photoId);
                } catch (SQLException | IOException e) {
                    req.getServletContext().log("Игрок не сохранён, не удалось удалить загруженную фотографию", e);
                }
            }
        }

        if (playerSaved) {
            resp.sendRedirect("/players");
            return;
        }

        req.getRequestDispatcher("/jsp/playerForm.jsp").forward(req, resp);
    }

    private Integer parseOptionalInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Integer.parseInt(value.trim());
    }
}
