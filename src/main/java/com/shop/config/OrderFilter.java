package com.shop.config;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter(urlPatterns = "/*", filterName = "orderFilter")
public class OrderFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        response.setHeader("X-Frame-OPTIONS", "ALLOW-FROM *");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE");
//        response.setHeader("Access-Control-Max-Age", "7200");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader("Access-Control-Allow-Headers","Authorization");
        //转到页面
        filterChain.doFilter(servletRequest,response);

    }

    @Override
    public void destroy() {

    }

}
