package my.seckill;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 *
 **/
@Controller
@Slf4j
public class LoginController {
    @Resource
    private UserService userService;

    @GetMapping("/user")
    public ModelAndView userDetail(HttpSession session, HttpServletRequest request) {
        Optional<Cookie> loginUser = Arrays.stream(request.getCookies())
                .filter(x -> x.getName().equals("login_user")).findFirst();
        if (loginUser.isEmpty()) {
            return new ModelAndView("login");
        }
        String value = loginUser.get().getValue();
        String userSessionIdFromCookie = Base64Util.encode(value);
        String userSessionIdFromSession = (String) session.getAttribute("user_session_id");
        if (!Objects.equals(userSessionIdFromSession, userSessionIdFromCookie)) {
            log.warn("No session mapping to user {} found!", value);
            return new ModelAndView("login");
        }
        ModelAndView modelAndView = new ModelAndView("user");
        modelAndView.addObject("name", value);
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @DeleteMapping("exit")
    public @ResponseBody String exitLogin(HttpServletRequest request, HttpServletResponse response) {
        Cookie loginUserCookie = Arrays.stream(request.getCookies())
                .filter(x -> x.getName().equals("login_user")).findFirst().orElse(null);
        if (loginUserCookie == null) {
            return "Not logged in!";
        }
        // Cookie??????????????????????????????????????????????????????cookie
        loginUserCookie.setValue(null);
        loginUserCookie.setMaxAge(0);
        response.addCookie(loginUserCookie);
        return "Exit success";
    }

    @PostMapping("/login")
    public @ResponseBody String doLogin(@RequestBody LoginInfo loginInfo, HttpSession session, HttpServletResponse response) {
        if (loginInfo == null) {
            return "No login info found!";
        }
        boolean loginPassed = userService.checkUserAndPassword(loginInfo.getUser(), loginInfo.getPassword());
        if (!loginPassed) {
            return "Login failed!";
        }
        log.info(loginInfo.toString());
        session.setAttribute("user_session_id", Base64Util.encode(loginInfo.user));
        Cookie cookie = new Cookie("login_user", loginInfo.user);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        // ?????????????????????????????????????????????5min
        if (loginInfo.rememberMe) {
            cookie.setMaxAge(300);
        } else {
            // ????????????????????????
            cookie.setMaxAge(-1);
        }
        response.addCookie(cookie);
        return "Success";
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class LoginInfo {
        private String user;

        private String password;
        @JsonProperty("remember_me")
        private boolean rememberMe;

    }

}
