package com.opus.erp.master.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库档案实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mdm_warehouse")
public class MdmWarehouse extends BaseEntity {

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 仓库类型（RAW/SEMI/FINISHED/RETURN/DEFECTIVE）
     */
    private String warehouseType;

    /**
     * 地址
     */
    private String address;

    /**
     * 负责人
     */
    private String manager;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 状态（1=启用，0=禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
