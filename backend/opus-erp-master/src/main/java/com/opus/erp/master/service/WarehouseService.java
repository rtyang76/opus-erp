package com.opus.erp.master.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.master.dto.WarehouseDTO;
import com.opus.erp.master.entity.MdmWarehouse;

/**
 * 仓库服务接口
 */
public interface WarehouseService extends IService<MdmWarehouse> {

    /**
     * 分页查询仓库列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param warehouseCode 仓库编码（模糊查询）
     * @param warehouseName 仓库名称（模糊查询）
     * @param warehouseType 仓库类型
     * @param status 状态
     * @return 分页结果
     */
    Page<MdmWarehouse> listWarehouses(int pageNum, int pageSize, String warehouseCode,
                                      String warehouseName, String warehouseType, Integer status);

    /**
     * 创建仓库
     * @param warehouse 仓库信息
     * @return 创建的仓库
     */
    MdmWarehouse createWarehouse(MdmWarehouse warehouse);

    /**
     * 更新仓库
     * @param warehouse 仓库信息
     * @return 更新的仓库
     */
    MdmWarehouse updateWarehouse(MdmWarehouse warehouse);

    /**
     * 删除仓库（逻辑删除）
     * @param warehouseId 仓库ID
     */
    void deleteWarehouse(Long warehouseId);

    /**
     * 根据 DTO 创建仓库
     * @param dto 仓库 DTO
     * @return 创建的仓库
     */
    MdmWarehouse createFromDTO(WarehouseDTO dto);

    /**
     * 根据 DTO 更新仓库
     * @param id 仓库ID
     * @param dto 仓库 DTO
     * @return 更新的仓库
     */
    MdmWarehouse updateFromDTO(Long id, WarehouseDTO dto);
}
