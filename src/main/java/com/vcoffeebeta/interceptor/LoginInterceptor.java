package com.vcoffeebeta.interceptor;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author zhangshenming
 * @date 2022/3/23 6:20
 * @version 1.0
 */
@Component("lxj")
public class LoginInterceptor implements HandlerInterceptor {

   @Override
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
       response.setCharacterEncoding("UTF-8");
       response.setContentType("application/json");
       //是否支持cookie跨域
       response.setHeader("Access-Control-Allow-Credentials","true");
       //支持跨域请求
       response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
       response.setHeader("Access-Control-Allow-Headers", "Authorization,Access-Token,Origin, X-Requested-With, Content-Type, Accept");
       response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");
       return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
    }
}
