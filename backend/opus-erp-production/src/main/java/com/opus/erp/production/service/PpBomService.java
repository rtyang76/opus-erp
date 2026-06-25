package com.opus.erp.production.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.production.dto.BomDTO;
import com.opus.erp.production.entity.PpBom;

/**
 * BOM 服务接口
 */
public interface PpBomService extends IService<PpBom> {

    /**
     * 分页查询 BOM 列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param bomCode BOM编码
     * @param itemId 物料ID
     * @param status 状态
     * @return 分页结果
     */
    Page<PpBom> listBoms(int pageNum, int pageSize, String bomCode, Long itemId, Integer status);

    /**
     * 查询 BOM 详情（包含明细）
     * @param bomId BOM ID
     * @return BOM 详情
     */
    PpBom getBomDetail(Long bomId);

    /**
     * 创建 BOM
     * @param bom BOM 信息
     * @return 创建的 BOM
     */
    PpBom createBom(PpBom bom);

    /**
     * 更新 BOM
     * @param bom BOM 信息
     * @return 更新的 BOM
     */
    PpBom updateBom(PpBom bom);

    /**
     * 删除 BOM
     * @param bomId BOM ID
     */
    void deleteBom(Long bomId);

    /**
     * 根据 DTO 创建 BOM
     * @param dto BOM DTO
     * @return 创建的 BOM
     */
    PpBom createFromDTO(BomDTO dto);

    /**
     * 根据 DTO 更新 BOM
     * @param id BOM ID
     * @param dto BOM DTO
     * @return 更新的 BOM
     */
    PpBom updateFromDTO(Long id, BomDTO dto);
}
