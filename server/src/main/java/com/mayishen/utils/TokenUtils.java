package com.mayishen.utils;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mayishen.core.dto.UserDTO;
import com.mayishen.core.entity.User;
import com.mayishen.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenUtils {
    @Autowired
    private UserService remoteUserService;
    private static UserService userService;

    @PostConstruct
    public void init() {
        userService = remoteUserService;
    }

    /***
     * 获取用户TOKEN
     * @param user 用户实体
     * @return 用户的TOKEN
     */
    public static String getToken(User user) {
        String token = JWT.create().withAudience(String.valueOf(user.getId()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000))
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }

    /***
     * 获取用户TOKEN
     * @param userDTO 用户的DTO实体
     * @return 用户的TOKEN
     */
    public static String getToken(UserDTO userDTO) {
        String token = JWT.create().withAudience(String.valueOf(userDTO.getId()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000))
                .sign(Algorithm.HMAC256(userDTO.getPassword()));
        return token;
    }

    /***
     * 根据用户的TOKEN获取用户的ID
     * @return 用户的ID
     */
    public static Integer getUserId() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (!StrUtil.isBlank(token)) {
                String userID = JWT.decode(token).getAudience().get(0);
                return Integer.valueOf(userID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /***
     * 根据用户的TOKEN获取用户实体
     * @return 用户实体
     */
    public static User getUser() {
        return userService.selectOne(getUserId());
    }
}
