package com.mayishen.common.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mayishen.common.Constants;
import com.mayishen.core.entity.User;
import com.mayishen.exception.ServiceException;
import com.mayishen.core.mapper.UserMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JWTInterceptor implements HandlerInterceptor {

    private static UserMapper userMapper;

    @Resource
    public void setUserMapper(UserMapper userMapper) {
        JWTInterceptor.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");
        if (!(handler instanceof HandlerMethod))
            return true;
        if (!StringUtils.hasText(token))
            throw new ServiceException(Constants.CODE_402, "无token，请重新登陆");
        String userID;
        try {
            userID = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new ServiceException(Constants.CODE_402, "token验证失败");
        }
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, userID));
        if (user == null)
            throw new ServiceException(Constants.CODE_402, "用户不存在，请重新登陆");
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new ServiceException(Constants.CODE_402, "token验证失败");
        }
        return true;
    }
}
