package ru.itis.servlets;

import ru.itis.models.User;
import ru.itis.services.SignInService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/signIn")
public class SignInServlet extends HttpServlet {

    private SignInService signInService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        signInService = (SignInService) config.getServletContext().getAttribute("signInService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute("userId") != null) {
            resp.sendRedirect("/home");
            return;
        }

        req.getRequestDispatcher("/jsp/signIn.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || email.isBlank() || password == null
        || password.isEmpty()) {
            req.setAttribute("message", "Заполните почту и пароль");
        }
        else {
            email = email.trim();
            try {
                User user = signInService.signIn(email, password);
                if (user != null) {
                    HttpSession session = req.getSession();
                    req.changeSessionId();

                    session.setAttribute("userId", user.getUserId());
                    session.setAttribute("role", user.getRole());

                    resp.sendRedirect("/home");
                    return;

                }
                else {
                    req.setAttribute("message", "Неверный пароль или email");
                }


            } catch (SQLException e) {
                throw new ServletException("Не удалось выполнить запрос к базе данных", e);
            }

        }
        req.getRequestDispatcher("/jsp/signIn.jsp").forward(req, resp);
    }
}
