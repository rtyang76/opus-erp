package com.opus.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.production.entity.PpBom;
import com.opus.erp.production.entity.PpBomDetail;
import com.opus.erp.production.mapper.PpBomDetailMapper;
import com.opus.erp.production.mapper.PpBomMapper;
import com.opus.erp.production.service.PpBomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * BOM 服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PpBomServiceImpl extends ServiceImpl<PpBomMapper, PpBom> implements PpBomService {

    private final PpBomDetailMapper bomDetailMapper;

    @Override
    public Page<PpBom> listBoms(int pageNum, int pageSize, String bomCode, Long itemId, Integer status) {
        Page<PpBom> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PpBom> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(bomCode)) {
            wrapper.like(PpBom::getBomCode, bomCode);
        }
        if (itemId != null) {
            wrapper.eq(PpBom::getItemId, itemId);
        }
        if (status != null) {
            wrapper.eq(PpBom::getStatus, status);
        }

        wrapper.orderByDesc(PpBom::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public PpBom getBomDetail(Long bomId) {
        PpBom bom = baseMapper.selectById(bomId);
        if (bom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "BOM不存在");
        }

        // 查询明细
        LambdaQueryWrapper<PpBomDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PpBomDetail::getBomId, bomId);
        bom.setDetails(bomDetailMapper.selectList(wrapper));

        return bom;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PpBom createBom(PpBom bom) {
        // 检查 BOM 编码是否已存在
        LambdaQueryWrapper<PpBom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PpBom::getBomCode, bom.getBomCode());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "BOM编码已存在");
        }

        // 保存主表
        baseMapper.insert(bom);

        // 保存明细
        if (bom.getDetails() != null) {
            for (PpBomDetail detail : bom.getDetails()) {
                detail.setBomId(bom.getId());
                bomDetailMapper.insert(detail);
            }
        }

        log.info("创建BOM成功: bomCode={}", bom.getBomCode());
        return bom;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PpBom updateBom(PpBom bom) {
        PpBom existingBom = baseMapper.selectById(bom.getId());
        if (existingBom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "BOM不存在");
        }

        // 更新主表
        baseMapper.updateById(bom);

        // 逻辑删除旧明细，插入新明细
        if (bom.getDetails() != null) {
            LambdaQueryWrapper<PpBomDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PpBomDetail::getBomId, bom.getId());
            // 使用逻辑删除（MyBatis-Plus @TableLogic 自动处理）
            bomDetailMapper.delete(wrapper);

            for (PpBomDetail detail : bom.getDetails()) {
                detail.setBomId(bom.getId());
                bomDetailMapper.insert(detail);
            }
        }

        log.info("更新BOM成功: bomId={}", bom.getId());
        return bom;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBom(Long bomId) {
        PpBom bom = baseMapper.selectById(bomId);
        if (bom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "BOM不存在");
        }

        // 逻辑删除明细（MyBatis-Plus @TableLogic 自动处理）
        LambdaQueryWrapper<PpBomDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PpBomDetail::getBomId, bomId);
        bomDetailMapper.delete(wrapper);

        // 逻辑删除主表
        baseMapper.deleteById(bomId);

        log.info("删除BOM成功: bomId={}", bomId);
    }
}
