package com.opus.erp.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opus.erp.production.entity.PpWorkOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 生产工单 Mapper 接口
 */
@Mapper
public interface PpWorkOrderMapper extends BaseMapper<PpWorkOrder> {
}
