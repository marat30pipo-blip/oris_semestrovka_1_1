package ru.itis.servlets;

import ru.itis.models.FileInfo;
import ru.itis.services.FileService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.sql.SQLException;

@WebServlet("/files")
public class FilesServlet extends HttpServlet {

    private FileService fileService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParameter = req.getParameter("id");

        if (idParameter == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        long fileId;

        try {
            fileId = Long.parseLong(idParameter);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (fileId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            FileInfo fileInfo = fileService.getFileInfo(fileId);

            if (fileInfo == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            resp.setContentType(fileInfo.getType());
            resp.setContentLengthLong(fileInfo.getSize());

            fileService.writeFileFromStorage(fileInfo, resp.getOutputStream());
        } catch (SQLException e) {
            throw new ServletException("Не удалось получить сведения о файле", e);
        } catch (NoSuchFileException e) {
            if (resp.isCommitted()) {
                throw e;
            }
            resp.reset();
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }


    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        fileService = (FileService) config.getServletContext().getAttribute("fileService");
    }
}
