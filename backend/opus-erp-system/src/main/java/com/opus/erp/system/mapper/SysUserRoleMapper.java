package com.opus.erp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opus.erp.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关联 Mapper 接口
 * 注意：关联表使用物理删除，因为：
 * 1. 关联表本身没有业务含义，只是用户和角色的多对多关系
 * 2. 物理删除可以避免数据膨胀
 * 3. 主表（用户/角色）使用逻辑删除，保证数据可追溯
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 删除用户的所有角色关联（物理删除）
     * 说明：关联表允许物理删除，见类注释
     */
    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
}
