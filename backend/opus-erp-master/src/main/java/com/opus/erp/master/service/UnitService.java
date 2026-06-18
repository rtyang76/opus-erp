package com.opus.erp.master.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.master.entity.MdmUnit;

import java.util.List;

/**
 * 计量单位服务接口
 */
public interface UnitService extends IService<MdmUnit> {

    /**
     * 查询所有单位（不分页）
     * @return 单位列表
     */
    List<MdmUnit> listAllUnits();

    /**
     * 分页查询单位列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param unitCode 单位编码（模糊查询）
     * @param unitName 单位名称（模糊查询）
     * @param status 状态
     * @return 分页结果
     */
    Page<MdmUnit> listUnits(int pageNum, int pageSize, String unitCode, String unitName, Integer status);

    /**
     * 创建单位
     * @param unit 单位信息
     * @return 创建的单位
     */
    MdmUnit createUnit(MdmUnit unit);

    /**
     * 更新单位
     * @param unit 单位信息
     * @return 更新的单位
     */
    MdmUnit updateUnit(MdmUnit unit);

    /**
     * 删除单位（逻辑删除）
     * @param unitId 单位ID
     */
    void deleteUnit(Long unitId);
}
