package com.mayishen.core.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mayishen.common.Constants;
import com.mayishen.common.Result;
import com.mayishen.core.dto.CookieDTO;
import com.mayishen.core.dto.UserDTO;
import com.mayishen.core.service.UserService;
import com.mayishen.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /***
     * 新增用户
     * @param userDTO 用户信息
     * @return 结果
     */
    @PostMapping
    public Result<?> save(@RequestBody UserDTO userDTO) {
        userService.insert(userDTO);
        return Result.success();
    }

    /***
     * 登陆
     * @param request Http请求
     * @param userDTO 请求体，为用户信息
     * @return 结果。若为成功结果，包含cookie
     */
    @PostMapping("/login")
    public Result<?> login(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        CookieDTO res = userService.login(userDTO);
        return Result.success(res);
    }

    /***
     * 更新用户信息
     * @param user 更新的用户信息
     * @return 结果。若为成功结果，包含cookie
     */
    @PutMapping
    public Result<?> update(@RequestBody UserDTO user) {
        if (user.getId() == null) {
            user.setId(TokenUtils.getUserId());
        }
        UserDTO userDto = userService.updateById(user);
        CookieDTO cookieDTO = new CookieDTO();
        BeanUtil.copyProperties(user, cookieDTO);
        cookieDTO.setToken(TokenUtils.getToken(userDto));
        return Result.success(cookieDTO);
    }

    /***
     * 删除用户
     * @param id 用户ID
     * @return 结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> update(@PathVariable Integer id) {
        if (id == 1) {
            return Result.error(Constants.CODE_400, "非法操作");
        }
        userService.deleteById(id);
        return Result.success();
    }

    /***
     * 分页检索用户信息
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param search 用户名的模糊搜索词
     * @return 分页结果
     */
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "") String search) {
        return Result.success(userService.selectPage(pageNum, pageSize, search));
    }
}
