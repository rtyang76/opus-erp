package com.opus.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 领料单明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pp_material_issue_detail")
public class PpMaterialIssueDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 领料单ID
     */
    private Long issueId;

    /**
     * 物料ID
     */
    private Long itemId;

    /**
     * 批次号
     */
    private String lotNo;

    /**
     * 领料/退料数量
     */
    private BigDecimal quantity;

    /**
     * 备注
     */
    private String remark;

    // ========== 关联查询字段 ==========

    /**
     * 物料编码
     */
    @TableField(exist = false)
    private String itemCode;

    /**
     * 物料名称
     */
    @TableField(exist = false)
    private String itemName;

    /**
     * 规格型号
     */
    @TableField(exist = false)
    private String specification;

    /**
     * 单位名称
     */
    @TableField(exist = false)
    private String unitName;
}
