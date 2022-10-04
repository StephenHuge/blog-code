package my.seckill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 登录权限校验Filter
 **/
@Component
@Slf4j
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Cookie[] cookies = request.getCookies();
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        log.info(LocalDateTime.now() + " 收到请求：" + requestMethod + " " + requestURI);
        // 静态资源不过滤
        if (requestURI.endsWith(".css") || requestURI.endsWith(".js") || requestURI.endsWith(".ico")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 如果cookie为空，证明是第一次请求，那么肯定没登录;
        // 如果登录成功后，会写入一个名叫login_user的cookie
        boolean isUserLoggedIn = cookies != null && Arrays.stream(cookies).anyMatch(x -> x.getName().equals("login_user"));
        if ("/exit".equals(requestURI)) {
            // 包含两种情况：已经登录 / 其它页面退出登录或者登录超时
            // 如果已经登录而且要退出登录的话，调用对应业务逻辑
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 如果未登录，而且页面不是登录页面就转到登录页面
        if (!isUserLoggedIn) {
            // 此处会有2种方法 GET & POST
            if ("/login".equals(requestURI)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                // forward和redirect的区别：
                //      servletRequest.getRequestDispatcher("/login").forward(servletRequest, servletResponse);
                ((HttpServletResponse) servletResponse).sendRedirect("/login");
            }
        } else {
            if ("/user".equals(requestURI)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                ((HttpServletResponse) servletResponse).sendRedirect("/user");
            }
        }
    }

}
