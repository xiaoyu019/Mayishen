package com.mayishen.core.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mayishen.common.Constants;
import com.mayishen.core.dto.CookieDTO;
import com.mayishen.core.dto.UserDTO;
import com.mayishen.core.entity.User;
import com.mayishen.exception.ServiceException;
import com.mayishen.core.mapper.UserMapper;
import com.mayishen.utils.DTOUtils;
import com.mayishen.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Resource
    UserMapper userMapper;

    /***
     * 根据用户的ID检索用户
     * @param id 用户的ID
     * @return 用户的实体
     */
    public User selectOne(Integer id) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, id));
    }

    /***
     * 登陆
     * @param user 用户的实体
     * @return Cookie的DTO实体
     */
    public CookieDTO login(UserDTO user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        User one;
        try {
            one = userMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        if (one != null) {
            CookieDTO cookie = new CookieDTO();
            BeanUtil.copyProperties(one, cookie);
            cookie.setToken(TokenUtils.getToken(one));
            return cookie;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }
    }

    /***
     * 根据用户的ID更新用户
     * @param userDTO 用户的DTO实体
     * @return 用户的DTO实体
     */
    public UserDTO updateById(UserDTO userDTO) {
        User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, userDTO.getId()).eq(User::getRole, userDTO.getRole()));
        if (res == null)
            throw new ServiceException(Constants.CODE_401, "参数错误");
        if (TokenUtils.getUserId() == userDTO.getId() || TokenUtils.getUser().getRole() == 0) {
            User user = new User();
            BeanUtil.copyProperties(userDTO, user);
            userMapper.updateById(user);
            return userDTO;
        } else {
            throw new ServiceException(Constants.CODE_400, "权限错误");
        }
    }

    /***
     * 根据用户的ID删除用户
     * @param id 用户的ID
     */
    public void deleteById(Integer id) {
        if (TokenUtils.getUser().getRole() != 0) {
            throw new ServiceException(Constants.CODE_400, "权限错误");
        }
        userMapper.deleteById(id);
    }

    /***
     * 分页检索用户
     * @param pageNum 当页码
     * @param pageSize 分页大小
     * @param search 用户名的模糊搜索词
     * @return 用户的DTO对象的列表
     */
    public Page<UserDTO> selectPage(Integer pageNum, Integer pageSize, String search) {
        Page<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), Wrappers.<User>lambdaQuery().eq(User::getRole, 1).like(User::getName, search));
        Page<UserDTO> DTOPage = new Page<>();
        BeanUtil.copyProperties(page, DTOPage);
        List<UserDTO> users = new ArrayList<>();
        for (User user : page.getRecords()) {
            user.setPassword("");
            users.add(DTOUtils.toDTO(user));
        }
        DTOPage.setRecords(users);
        return DTOPage;
    }

    /***
     * 插入用户
     * @param userDTO 用户的DTO对象
     */
    public void insert(UserDTO userDTO) {
        if (TokenUtils.getUser().getRole() != 0) {
            throw new ServiceException(Constants.CODE_400, "权限错误");
        }
        User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, userDTO.getUsername()));
        if (res != null) {
            throw new ServiceException(Constants.CODE_401, "用户名重复");
        }
        if (userDTO.getRole() == null) {
            userDTO.setRole(1);
        }
        User user = new User();
        BeanUtil.copyProperties(userDTO, user);
        userMapper.insert(user);
    }
}
