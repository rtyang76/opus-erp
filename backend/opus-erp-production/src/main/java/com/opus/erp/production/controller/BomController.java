package com.opus.erp.production.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.production.dto.BomDTO;
import com.opus.erp.production.entity.PpBom;
import com.opus.erp.production.entity.PpBomDetail;
import com.opus.erp.production.service.PpBomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * BOM 管理控制器
 */
@RestController
@RequestMapping("/api/production/boms")
@RequiredArgsConstructor
public class BomController {

    private final PpBomService bomService;

    /**
     * 分页查询 BOM 列表
     */
    @GetMapping
    public R<Page<PpBom>> listBoms(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String bomCode,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Integer status) {
        Page<PpBom> page = bomService.listBoms(pageNum, pageSize, bomCode, itemId, status);
        return R.ok(page);
    }

    /**
     * 查询 BOM 详情
     */
    @GetMapping("/{id}")
    public R<PpBom> getBom(@PathVariable Long id) {
        PpBom bom = bomService.getBomDetail(id);
        return R.ok(bom);
    }

    /**
     * 创建 BOM
     * TODO: DTO-to-Entity 转换应移入 Service 层，Controller 只做参数校验和调用 Service
     * 当前实现：Controller 中完成转换，后续重构为 Service 接受 DTO 参数
     */
    @PostMapping
    public R<PpBom> createBom(@Valid @RequestBody BomDTO dto) {
        PpBom bom = new PpBom();
        BeanUtils.copyProperties(dto, bom);

        // 转换明细
        if (dto.getDetails() != null) {
            List<PpBomDetail> details = new ArrayList<>();
            for (BomDTO.BomDetailDTO detailDTO : dto.getDetails()) {
                PpBomDetail detail = new PpBomDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                details.add(detail);
            }
            bom.setDetails(details);
        }

        PpBom createdBom = bomService.createBom(bom);
        return R.ok("创建成功", createdBom);
    }

    /**
     * 更新 BOM
     */
    @PutMapping("/{id}")
    public R<PpBom> updateBom(@PathVariable Long id, @Valid @RequestBody BomDTO dto) {
        PpBom bom = new PpBom();
        BeanUtils.copyProperties(dto, bom);
        bom.setId(id);

        // 转换明细
        if (dto.getDetails() != null) {
            List<PpBomDetail> details = new ArrayList<>();
            for (BomDTO.BomDetailDTO detailDTO : dto.getDetails()) {
                PpBomDetail detail = new PpBomDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                details.add(detail);
            }
            bom.setDetails(details);
        }

        PpBom updatedBom = bomService.updateBom(bom);
        return R.ok("更新成功", updatedBom);
    }

    /**
     * 删除 BOM
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteBom(@PathVariable Long id) {
        bomService.deleteBom(id);
        return R.okMsg("删除成功");
    }
}
