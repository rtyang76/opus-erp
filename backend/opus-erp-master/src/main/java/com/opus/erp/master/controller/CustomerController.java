package com.opus.erp.master.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.master.dto.CustomerDTO;
import com.opus.erp.master.entity.MdmCustomer;
import com.opus.erp.master.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户管理控制器
 */
@RestController
@RequestMapping("/api/master/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 分页查询客户列表
     */
    @GetMapping
    public R<Page<MdmCustomer>> listCustomers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) Integer status) {
        Page<MdmCustomer> page = customerService.listCustomers(pageNum, pageSize,
                customerCode, customerName, rating, status);
        return R.ok(page);
    }

    /**
     * 查询客户详情
     */
    @GetMapping("/{id}")
    public R<MdmCustomer> getCustomer(@PathVariable Long id) {
        MdmCustomer customer = customerService.getById(id);
        if (customer == null) {
            return R.notFound("客户不存在");
        }
        return R.ok(customer);
    }

    /**
     * 创建客户
     */
    @PostMapping
    public R<MdmCustomer> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        MdmCustomer createdCustomer = customerService.createFromDTO(customerDTO);
        return R.ok("创建成功", createdCustomer);
    }

    /**
     * 更新客户
     */
    @PutMapping("/{id}")
    public R<MdmCustomer> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDTO) {
        MdmCustomer updatedCustomer = customerService.updateFromDTO(id, customerDTO);
        return R.ok("更新成功", updatedCustomer);
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return R.okMsg("删除成功");
    }
}
