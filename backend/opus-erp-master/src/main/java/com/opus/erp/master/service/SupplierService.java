package com.opus.erp.master.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.master.dto.SupplierDTO;
import com.opus.erp.master.entity.MdmSupplier;

/**
 * 供应商服务接口
 */
public interface SupplierService extends IService<MdmSupplier> {

    /**
     * 分页查询供应商列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param supplierCode 供应商编码（模糊查询）
     * @param supplierName 供应商名称（模糊查询）
     * @param rating 评级
     * @param status 状态
     * @return 分页结果
     */
    Page<MdmSupplier> listSuppliers(int pageNum, int pageSize, String supplierCode,
                                    String supplierName, String rating, Integer status);

    /**
     * 创建供应商
     * @param supplier 供应商信息
     * @return 创建的供应商
     */
    MdmSupplier createSupplier(MdmSupplier supplier);

    /**
     * 更新供应商
     * @param supplier 供应商信息
     * @return 更新的供应商
     */
    MdmSupplier updateSupplier(MdmSupplier supplier);

    /**
     * 删除供应商（逻辑删除）
     * @param supplierId 供应商ID
     */
    void deleteSupplier(Long supplierId);

    /**
     * 根据 DTO 创建供应商
     * @param dto 供应商 DTO
     * @return 创建的供应商
     */
    MdmSupplier createFromDTO(SupplierDTO dto);

    /**
     * 根据 DTO 更新供应商
     * @param id 供应商ID
     * @param dto 供应商 DTO
     * @return 更新的供应商
     */
    MdmSupplier updateFromDTO(Long id, SupplierDTO dto);
}
