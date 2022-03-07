package com.vcoffeebeta.interceptor;

import com.vcoffeebeta.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截器
 * @author zhangshenming
 * @date 2022/3/6 23:01
 * @version 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        HttpSession session = request.getSession();
        String contextPath = session.getServletContext().getContextPath();
        String[]requireAuthPages = new String[]{"index"};
        String uri = request.getRequestURI();
        uri = uri.substring(contextPath.length()+2);
        String page= uri;
        if(beginWith(page,requireAuthPages)){
            User user = (User) session.getAttribute("user");
            if(user == null){
                response.sendRedirect("login");
                return false;
            }
        }
        return true;
    }
    /**
     * 比较连接中/后面的内容是否包含用户信息
     * @author zhangshenming
     * @date 2022/3/6 23:17
     * @param page, requireAuthPages
     * @return boolean
     */
    private boolean beginWith(String page, String[] requireAuthPages) {
        boolean flag = false;
        for(String requireAuthPage:requireAuthPages){
            if(page.equals(requireAuthPage)){
                flag = true;
                break;
            }
        }
        return flag;
    }
}
