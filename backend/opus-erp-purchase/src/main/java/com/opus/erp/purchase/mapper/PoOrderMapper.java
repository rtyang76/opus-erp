package com.opus.erp.purchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opus.erp.purchase.entity.PoOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购订单 Mapper 接口
 */
@Mapper
public interface PoOrderMapper extends BaseMapper<PoOrder> {
}
