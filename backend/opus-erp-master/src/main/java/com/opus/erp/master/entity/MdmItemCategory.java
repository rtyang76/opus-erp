package com.opus.erp.master.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 物料分类实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mdm_item_category")
public class MdmItemCategory extends BaseEntity {

    /**
     * 父分类ID（0表示顶级分类）
     */
    private Long parentId;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类类型（RAW/SEMI/FINISHED/AUXILIARY）
     */
    private String categoryType;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态（1=启用，0=禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 子分类列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<MdmItemCategory> children;
}
