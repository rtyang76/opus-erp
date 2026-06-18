package com.opus.erp.master.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 计量单位实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mdm_unit")
public class MdmUnit extends BaseEntity {

    /**
     * 单位编码
     */
    private String unitCode;

    /**
     * 单位名称
     */
    private String unitName;

    /**
     * 状态（1=启用，0=禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
