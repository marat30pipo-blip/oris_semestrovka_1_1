package ru.itis.servlets;

import ru.itis.dto.SignUpForm;
import ru.itis.services.SignUpService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;


@WebServlet("/signUp")
public class SignUpServlet extends HttpServlet {

    private SignUpService signUpService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/signUp.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String lastName = req.getParameter("lastName");
        String firstName = req.getParameter("firstName");
        String middleName = req.getParameter("middleName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (lastName == null || lastName.isBlank() || firstName == null || firstName.isBlank() ||
                middleName == null || middleName.isBlank() || email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            req.setAttribute("message", "Заполните все поля");
        }
        else {
            SignUpForm form = SignUpForm.builder()
                    .lastName(lastName)
                    .firstName(firstName)
                    .middleName(middleName)
                    .email(email)
                    .password(password)
                    .build();

            try {
                if (signUpService.signUp(form)) {
                    resp.sendRedirect("/signIn");
                    return;
                }

                req.setAttribute("message", "Этот email уже зарегистрирован");

            } catch (IllegalArgumentException e) {
                req.setAttribute("message", e.getMessage());

            } catch (SQLException e) {
                throw new ServletException("Не удалось зарегистрировать пользователя", e);
            }

        }
        req.getRequestDispatcher("/jsp/signUp.jsp").forward(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        signUpService = (SignUpService) config.getServletContext().getAttribute("signUpService");
    }
}
