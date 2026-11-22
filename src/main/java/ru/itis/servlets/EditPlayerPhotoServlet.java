package ru.itis.servlets;

import ru.itis.models.Player;
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
import java.sql.SQLException;

@WebServlet("/admin/players/photo")
@MultipartConfig(
        maxFileSize = 5L * 1024 * 1024,
        maxRequestSize = 6L * 1024 * 1024
)
public class EditPlayerPhotoServlet extends HttpServlet {

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

            req.getRequestDispatcher("/jsp/playerPhoto.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить игрока", e);
        }
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

            if (photo == null
                    || photo.getSubmittedFileName() == null
                    || photo.getSubmittedFileName().isBlank()) {

                req.setAttribute("message", "Выберите фотографию");

                req.getRequestDispatcher("/jsp/playerPhoto.jsp")
                        .forward(req, resp);
                return;
            }

            Long oldPhotoId = player.getPhotoId();
            Long newPhotoId = null;
            boolean updated = false;

            try {
                try (InputStream inputStream = photo.getInputStream()) {
                    newPhotoId = fileService.saveFileToStorage(
                            inputStream,
                            photo.getSubmittedFileName(),
                            photo.getContentType(),
                            photo.getSize()
                    );
                }

                updated = playersService.updatePhoto(playerId, newPhotoId);

                if (!updated) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

            } catch (IllegalArgumentException e) {
                req.setAttribute("message", e.getMessage());

            } catch (SQLException | IOException e) {
                throw new ServletException("Не удалось заменить фотографию игрока", e);

            } finally {
                if (newPhotoId != null && !updated) {
                    try {
                        fileService.deleteFile(newPhotoId);
                    } catch (SQLException | IOException e) {
                        req.getServletContext().log("Фотография игрока не обновлена, не удалось удалить новый файл", e);
                    }
                }
            }

            if (!updated) {
                req.getRequestDispatcher("/jsp/playerPhoto.jsp").forward(req, resp);
                return;
            }

            if (oldPhotoId != null) {
                try {
                    fileService.deleteFile(oldPhotoId);
                } catch (SQLException | IOException e) {
                    req.getServletContext().log("Фотография игрока заменена, не удалось удалить старый файл", e);
                }
            }

            resp.sendRedirect("/players");

        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить игрока", e);
        }
    }
}