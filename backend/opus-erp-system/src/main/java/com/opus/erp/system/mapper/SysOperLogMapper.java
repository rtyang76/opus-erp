package com.opus.erp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opus.erp.system.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper 接口
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}
