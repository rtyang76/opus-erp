package com.opus.erp.master.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 客户档案实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mdm_customer")
public class MdmCustomer extends BaseEntity {

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 简称
     */
    private String shortName;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 税号
     */
    private String taxNo;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 收款条款
     */
    private String paymentTerms;

    /**
     * 信用额度
     */
    private BigDecimal creditLimit;

    /**
     * 评级（A/B/C）
     */
    private String rating;

    /**
     * 业务员ID
     */
    private Long salesmanId;

    /**
     * 状态（1=启用，0=禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    // ========== 非数据库字段 ==========

    /**
     * 业务员姓名
     */
    @TableField(exist = false)
    private String salesmanName;
}
