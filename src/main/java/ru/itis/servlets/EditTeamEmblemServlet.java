package ru.itis.servlets;

import ru.itis.models.Team;
import ru.itis.services.FileService;
import ru.itis.services.TeamsService;

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

@WebServlet("/admin/teams/emblem")
@MultipartConfig(
        maxFileSize = 5L * 1024 * 1024,
        maxRequestSize = 6L * 1024 * 1024
)
public class EditTeamEmblemServlet extends HttpServlet {

    private TeamsService teamsService;
    private FileService fileService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        teamsService = (TeamsService) config.getServletContext()
                .getAttribute("teamsService");

        fileService = (FileService) config.getServletContext()
                .getAttribute("fileService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

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

            req.setAttribute("team", team);

            req.getRequestDispatcher("/jsp/teamEmblem.jsp")
                    .forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(
                    "Не удалось загрузить команду", e
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        Part emblem;

        try {
            emblem = req.getPart("emblem");
        } catch (IllegalStateException e) {
            resp.sendError(
                    HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                    "Файл превышает 5 МБ или запрос превышает 6 МБ"
            );
            return;
        }

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

        Team team;

        try {
            team = teamsService.findById(teamId);
        } catch (SQLException e) {
            throw new ServletException("Не удалось загрузить команду для замены эмблемы", e);
        }

        if (team == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("team", team);

        if (emblem == null
                || emblem.getSubmittedFileName() == null
                || emblem.getSubmittedFileName().isBlank()) {

            req.setAttribute("message", "Выберите новую эмблему");
            req.getRequestDispatcher("/jsp/teamEmblem.jsp").forward(req, resp);
            return;
        }

        Long oldEmblemId = team.getEmblemId();
        Long newEmblemId = null;
        boolean updated;

        try {
            try (InputStream inputStream = emblem.getInputStream()) {
                newEmblemId = fileService.saveFileToStorage(
                        inputStream,
                        emblem.getSubmittedFileName(),
                        emblem.getContentType(),
                        emblem.getSize()
                );
            }

            updated = teamsService.updateEmblem(teamId, newEmblemId);

        } catch (IllegalArgumentException e) {
            req.setAttribute("message", e.getMessage());
            req.getRequestDispatcher("/jsp/teamEmblem.jsp").forward(req, resp);
            return;

        } catch (IOException | SQLException e) {
            if (newEmblemId != null) {
                try {
                    fileService.deleteFile(newEmblemId);
                } catch (IOException | SQLException cleanupException) {
                    e.addSuppressed(cleanupException);
                }
            }

            throw new ServletException("Не удалось заменить эмблему", e);
        }

        if (!updated) {
            try {
                fileService.deleteFile(newEmblemId);
            } catch (IOException | SQLException e) {
                req.getServletContext().log("Команда не найдена, не удалось удалить новую эмблему", e);
            }

            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (oldEmblemId != null) {
            try {
                fileService.deleteFile(oldEmblemId);
            } catch (IOException | SQLException e) {
                req.getServletContext().log("Эмблема заменена, но не удалось удалить старый файл", e);
            }
        }

        resp.sendRedirect("/teams");
    }
}