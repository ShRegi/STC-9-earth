package ru.innopolis.stc9.earth_stc9.controllers;

import ru.innopolis.stc9.earth_stc9.controllers.users.Roles;
import ru.innopolis.stc9.earth_stc9.pojo.Role;
import ru.innopolis.stc9.earth_stc9.services.AuthService;
import ru.innopolis.stc9.earth_stc9.services.IAuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller for login operations
 */
@WebServlet("/login")
public class LoginController extends AbstractController {

    private IAuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("doGet" + this.getClass().getName());
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String errorMsg = req.getParameter("errorMsg");
        if (errorMsg != null && errorMsg.equals("noAccess")) {
            req.setAttribute("message", "У Вас нет доступа к этой странице.");
        }

        if (errorMsg != null && errorMsg.equals("authErr")) {
            req.setAttribute("errorMsg", "String");
        }
        req.getRequestDispatcher("/pages/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.info("doPost" + this.getClass().getName());
        String action = req.getParameter("exit");
        if (action != null) {
            req.getSession().invalidate();
            resp.sendRedirect("/");
        } else {
            String login = req.getParameter("userName");
            String password = req.getParameter("userPassword");
            if (authService.checkAuth(login, password)) {
                Role role = authService.getRoleByUserLogin(login);
                req.getSession().setAttribute("role", role.getId());
                req.getSession().setAttribute("login", login);

                switch (role.getId()) {
                    case Roles.ADMIN_ROLE_ID:
                    case Roles.STUDENT_ROLE_ID:
                    case Roles.TEACHER_ROLE_ID:
                        resp.sendRedirect("/dashboard");
                        break;
                    default:
                        resp.sendRedirect(req.getContextPath() + "/login?errorMsg=roleNull");
                        break;
                }

            } else {
                resp.sendRedirect(req.getContextPath() + "/login?errorMsg=authErr");
            }
        }
    }
}
