package com.opus.erp.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opus.erp.inventory.entity.InvStocktake;
import org.apache.ibatis.annotations.Mapper;

/**
 * 盘点单 Mapper 接口
 */
@Mapper
public interface InvStocktakeMapper extends BaseMapper<InvStocktake> {
}
