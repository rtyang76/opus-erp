package com.opus.erp.master.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 供应商档案实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mdm_supplier")
public class MdmSupplier extends BaseEntity {

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

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
     * 付款条款
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
     * 状态（1=启用，0=禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
