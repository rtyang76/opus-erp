package com.opus.erp.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opus.erp.inventory.entity.InvTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存交易流水 Mapper 接口
 * 注意：流水表只新增不修改，不提供 update/delete 方法
 */
@Mapper
public interface InvTransactionMapper extends BaseMapper<InvTransaction> {
}
