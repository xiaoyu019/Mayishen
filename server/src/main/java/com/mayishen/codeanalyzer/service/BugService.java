package com.mayishen.codeanalyzer.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mayishen.codeanalyzer.dto.BugDTO;
import com.mayishen.common.Constants;
import com.mayishen.codeanalyzer.entity.Bug;
import com.mayishen.exception.ServiceException;
import com.mayishen.codeanalyzer.mapper.BugMapper;
import com.mayishen.utils.DTOUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BugService {
    @Resource
    private BugMapper bugMapper;

    /***
     * 插入Bug
     * @param bugDTO bug的DTO实体
     */
    public void insert(BugDTO bugDTO) {
        Bug res = bugMapper.selectOne(Wrappers.<Bug>lambdaQuery().eq(Bug::getName, bugDTO.getName()));
        if (res == null) {
            Bug bug = new Bug();
            BeanUtil.copyProperties(bugDTO, bug);
            bugMapper.insert(bug);
            bugDTO.setId(bug.getId());
        } else {
            bugDTO.setId(res.getId());
        }
    }

    /***
     * 根据bug的ID更新Bug
     * @param bugDTO bug的DTO实体
     * @return 更新后的实体
     */
    public BugDTO updateById(BugDTO bugDTO) {
        Bug res = bugMapper.selectOne(Wrappers.<Bug>lambdaQuery().eq(Bug::getId, bugDTO.getId()));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        Bug bug = new Bug();
        BeanUtil.copyProperties(bugDTO, bug);
        bugMapper.updateById(bug);
        return bugDTO;
    }

    /***
     * 根据ID删除Bug
     * @param id Bug的ID
     */
    public void deleteById(Integer id) {
        Bug res = bugMapper.selectOne(Wrappers.<Bug>lambdaQuery().eq(Bug::getId, id));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        bugMapper.deleteById(id);
    }

    /***
     * 根据开源库ID检索Bug实体
     * @param id 开源库的ID
     * @return Bug的DTO实体列表
     */
    List<BugDTO> selectByOSLId(Integer id) {
        return DTOUtils.toBugDTOList(bugMapper.selectByOSLId(id));
    }

    /***
     * 将依赖库和漏洞表项连接
     * @param osl_id 依赖库ID
     * @param bug_id 漏洞ID
     */
    public void linkWithOpenSourceLibrary(Integer osl_id, Integer bug_id) {
        bugMapper.linkWithOpenSourceLibrary(osl_id, bug_id);
    }

    /***
     * 根据项目ID统计BUG数量
     * @param id 项目ID
     * @return 数量
     */
    public Integer countByProjectID(Integer id) {
        return bugMapper.countByProjectID(id);
    }
}
