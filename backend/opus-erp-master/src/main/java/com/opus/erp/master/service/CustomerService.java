package com.opus.erp.master.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.master.dto.CustomerDTO;
import com.opus.erp.master.entity.MdmCustomer;

/**
 * 客户服务接口
 */
public interface CustomerService extends IService<MdmCustomer> {

    /**
     * 分页查询客户列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param customerCode 客户编码（模糊查询）
     * @param customerName 客户名称（模糊查询）
     * @param rating 评级
     * @param status 状态
     * @return 分页结果
     */
    Page<MdmCustomer> listCustomers(int pageNum, int pageSize, String customerCode,
                                    String customerName, String rating, Integer status);

    /**
     * 创建客户
     * @param customer 客户信息
     * @return 创建的客户
     */
    MdmCustomer createCustomer(MdmCustomer customer);

    /**
     * 更新客户
     * @param customer 客户信息
     * @return 更新的客户
     */
    MdmCustomer updateCustomer(MdmCustomer customer);

    /**
     * 删除客户（逻辑删除）
     * @param customerId 客户ID
     */
    void deleteCustomer(Long customerId);

    /**
     * 根据 DTO 创建客户
     * @param dto 客户 DTO
     * @return 创建的客户
     */
    MdmCustomer createFromDTO(CustomerDTO dto);

    /**
     * 根据 DTO 更新客户
     * @param id 客户ID
     * @param dto 客户 DTO
     * @return 更新的客户
     */
    MdmCustomer updateFromDTO(Long id, CustomerDTO dto);
}
