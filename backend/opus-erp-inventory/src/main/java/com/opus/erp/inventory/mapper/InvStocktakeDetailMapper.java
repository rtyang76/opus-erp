package com.opus.erp.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opus.erp.inventory.entity.InvStocktakeDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 盘点单明细 Mapper 接口
 */
@Mapper
public interface InvStocktakeDetailMapper extends BaseMapper<InvStocktakeDetail> {
}
