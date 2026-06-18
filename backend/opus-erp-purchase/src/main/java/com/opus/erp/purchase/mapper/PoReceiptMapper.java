package com.opus.erp.purchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opus.erp.purchase.entity.PoReceipt;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购入库单 Mapper 接口
 */
@Mapper
public interface PoReceiptMapper extends BaseMapper<PoReceipt> {
}
