package com.opus.erp.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opus.erp.inventory.entity.InvStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * 即时库存 Mapper 接口
 */
@Mapper
public interface InvStockMapper extends BaseMapper<InvStock> {

    /**
     * 更新库存数量（移动加权平均法）
     * @param itemId 物料ID
     * @param warehouseId 仓库ID
     * @param binId 库位ID（可空）
     * @param lotNo 批次号（可空）
     * @param quantity 变动数量（正数入库，负数出库）
     * @param unitCost 本次单位成本
     * @return 影响行数
     */
    @Update("UPDATE inv_stock SET " +
            "quantity = quantity + #{quantity}, " +
            "avg_cost = CASE " +
            "  WHEN #{quantity} > 0 AND (quantity + #{quantity}) > 0 " +
            "  THEN (quantity * avg_cost + #{quantity} * #{unitCost}) / (quantity + #{quantity}) " +
            "  ELSE avg_cost " +
            "END, " +
            "updated_at = GETDATE() " +
            "WHERE item_id = #{itemId} AND warehouse_id = #{warehouseId} " +
            "AND (bin_id = #{binId} OR (bin_id IS NULL AND #{binId} IS NULL)) " +
            "AND (lot_no = #{lotNo} OR (lot_no IS NULL AND #{lotNo} IS NULL))")
    int updateQuantity(@Param("itemId") Long itemId,
                       @Param("warehouseId") Long warehouseId,
                       @Param("binId") Long binId,
                       @Param("lotNo") String lotNo,
                       @Param("quantity") BigDecimal quantity,
                       @Param("unitCost") BigDecimal unitCost);

    /**
     * 出库更新库存（带负数保护）
     * 只有当可用库存 >= 出库数量时才允许更新
     * @param itemId 物料ID
     * @param warehouseId 仓库ID
     * @param binId 库位ID（可空）
     * @param lotNo 批次号（可空）
     * @param quantity 出库数量（正数）
     * @param unitCost 单位成本
     * @return 影响行数
     */
    @Update("UPDATE inv_stock SET " +
            "quantity = quantity - #{quantity}, " +
            "updated_at = GETDATE() " +
            "WHERE item_id = #{itemId} AND warehouse_id = #{warehouseId} " +
            "AND (bin_id = #{binId} OR (bin_id IS NULL AND #{binId} IS NULL)) " +
            "AND (lot_no = #{lotNo} OR (lot_no IS NULL AND #{lotNo} IS NULL)) " +
            "AND (quantity - locked_quantity) >= #{quantity}")
    int updateQuantityForIssue(@Param("itemId") Long itemId,
                               @Param("warehouseId") Long warehouseId,
                               @Param("binId") Long binId,
                               @Param("lotNo") String lotNo,
                               @Param("quantity") BigDecimal quantity,
                               @Param("unitCost") BigDecimal unitCost);

    /**
     * 锁定库存（待出库）
     */
    @Update("UPDATE inv_stock SET " +
            "locked_quantity = locked_quantity + #{lockQuantity}, " +
            "updated_at = GETDATE() " +
            "WHERE item_id = #{itemId} AND warehouse_id = #{warehouseId} " +
            "AND (bin_id = #{binId} OR (bin_id IS NULL AND #{binId} IS NULL)) " +
            "AND (lot_no = #{lotNo} OR (lot_no IS NULL AND #{lotNo} IS NULL)) " +
            "AND (quantity - locked_quantity) >= #{lockQuantity}")
    int lockStock(@Param("itemId") Long itemId,
                  @Param("warehouseId") Long warehouseId,
                  @Param("binId") Long binId,
                  @Param("lotNo") String lotNo,
                  @Param("lockQuantity") BigDecimal lockQuantity);

    /**
     * 解锁库存
     */
    @Update("UPDATE inv_stock SET " +
            "locked_quantity = locked_quantity - #{lockQuantity}, " +
            "updated_at = GETDATE() " +
            "WHERE item_id = #{itemId} AND warehouse_id = #{warehouseId} " +
            "AND (bin_id = #{binId} OR (bin_id IS NULL AND #{binId} IS NULL)) " +
            "AND (lot_no = #{lotNo} OR (lot_no IS NULL AND #{lotNo} IS NULL))")
    int unlockStock(@Param("itemId") Long itemId,
                    @Param("warehouseId") Long warehouseId,
                    @Param("binId") Long binId,
                    @Param("lotNo") String lotNo,
                    @Param("lockQuantity") BigDecimal lockQuantity);

    /**
     * 查询可用库存数量
     */
    @Select("SELECT ISNULL(quantity - locked_quantity, 0) FROM inv_stock " +
            "WHERE item_id = #{itemId} AND warehouse_id = #{warehouseId} " +
            "AND (bin_id = #{binId} OR (bin_id IS NULL AND #{binId} IS NULL)) " +
            "AND (lot_no = #{lotNo} OR (lot_no IS NULL AND #{lotNo} IS NULL))")
    BigDecimal getAvailableQuantity(@Param("itemId") Long itemId,
                                    @Param("warehouseId") Long warehouseId,
                                    @Param("binId") Long binId,
                                    @Param("lotNo") String lotNo);
}
