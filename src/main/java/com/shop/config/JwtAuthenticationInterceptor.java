package com.shop.config;

import com.shop.anotation.PassToken;
import com.shop.bean.UserBean;
import com.shop.dao.mapperDao.UserMapper;
import com.shop.exceptions.TokenUnavailableException;
import com.shop.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Lehr
 * @create: 2020-02-03
 */
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Resource
    UserMapper userMapper;
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        // 从请求头中取出 token  这里需要和前端约定好把jwt放到请求头一个叫Authorization的地方
        String token = httpServletRequest.getHeader("Authorization");
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //检查是否有passToken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //默认全部检查
        else {
            // 执行认证
            if (token == null) {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }

            // 获取 token 中的 userNo
            String check = null;
            try {
                check = JwtUtils.getAudience(token);
            } catch (TokenUnavailableException e) {
                logger.error(e.getMessage());
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }

            //验证用户是否存在
            UserBean user = userMapper.queryUserByNo(check);
            if (user == null) {
                logger.error("此用户不存在");
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }

            // 验证 token
            try {
                JwtUtils.verifyToken(token, check);
            } catch (TokenUnavailableException e) {
                logger.error(e.getMessage());
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }

            //获取载荷内容
            String userName = JwtUtils.getClaimByName(token, "userName").asString();
            String userEmail = JwtUtils.getClaimByName(token, "userEmail").asString();
            String userNo = JwtUtils.getClaimByName(token, "userNo").asString();


            //放入attribute以便后面调用
            httpServletRequest.setAttribute("userName", userName);
            httpServletRequest.setAttribute("userEmail", userEmail);
            httpServletRequest.setAttribute("userNo", userNo);


            return true;

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }
}
