package ru.itis.servlets;

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

@WebServlet("/admin/teams/add")
@MultipartConfig(
        maxFileSize = 5L * 1024 * 1024,
        maxRequestSize = 6L * 1024 * 1024
)
public class AddTeamServlet extends HttpServlet {

    private TeamsService teamsService;

    private FileService fileService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/teamForm.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");


        Part emblem;

        try {
            emblem = req.getPart("emblem");
        } catch (IllegalStateException e) {
            resp.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);

            req.setAttribute(
                    "message",
                    "Файл превышает 5 МБ или общий размер запроса превышает 6 МБ"
            );

            req.getRequestDispatcher("/jsp/teamForm.jsp").forward(req, resp);
            return;
        }

        String name = req.getParameter("name");
        String description = req.getParameter("description");

        req.setAttribute("name", name);
        req.setAttribute("description", description);

        try {
            teamsService.validate(name, description);
            Long emblemId = null;

            if (emblem != null && emblem.getSubmittedFileName() != null
                    && !emblem.getSubmittedFileName().isBlank()) {
                try (InputStream inputStream = emblem.getInputStream()) {
                    emblemId = fileService.saveFileToStorage(
                            inputStream,
                            emblem.getSubmittedFileName(),
                            emblem.getContentType(),
                            emblem.getSize()
                    );
                }
            }

            try {
                teamsService.save(name, description, emblemId);
            } catch (SQLException e) {
                if (emblemId != null) {
                    try {
                        fileService.deleteFile(emblemId);
                    } catch (IOException | SQLException cleanupException) {
                        e.addSuppressed(cleanupException);
                    }
                }

                throw e;
            }

            resp.sendRedirect("/teams");
            return;
        }
        catch (IllegalArgumentException e) {
            req.setAttribute("message", e.getMessage());
        }
        catch (SQLException e) {
            throw new ServletException("Не удалось добавить команду" ,e);
        }
        req.getRequestDispatcher("/jsp/teamForm.jsp").forward(req, resp);

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        teamsService = (TeamsService) config.getServletContext().getAttribute("teamsService");
        fileService = (FileService) config.getServletContext().getAttribute("fileService");
    }
}
