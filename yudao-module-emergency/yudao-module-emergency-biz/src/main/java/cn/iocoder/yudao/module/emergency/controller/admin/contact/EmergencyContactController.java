package cn.iocoder.yudao.module.emergency.controller.admin.contact;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.contact.vo.*;
import cn.iocoder.yudao.module.emergency.service.contact.EmergencyContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/emergency/contacts")
@Tag(name = "管理后台 - 应急联络通讯录")
public class EmergencyContactController {

    @Resource
    private EmergencyContactService contactService;

    @PostMapping
    @Operation(summary = "创建应急联络通讯录")
    public CommonResult<Long> createContact(@Valid @RequestBody ContactCreateReqVO createReqVO) {
        return success(contactService.createContact(createReqVO));
    }

    @PutMapping
    @Operation(summary = "更新应急联络通讯录")
    public CommonResult<Boolean> updateContact(@Valid @RequestBody ContactUpdateReqVO updateReqVO) {
        contactService.updateContact(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除应急联络通讯录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteContact(@PathVariable("id") Long id) {
        contactService.deleteContact(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应急联络通讯录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ContactRespVO> getContact(@RequestParam("id") Long id) {
        ContactRespVO contact = contactService.getContact(id);
        return success(contact);
    }

    @GetMapping("/page")
    @Operation(summary = "获得应急联络通讯录分页")
    public CommonResult<PageResult<ContactRespVO>> getContactPage(@Valid ContactPageReqVO pageReqVO) {
        PageResult<ContactRespVO> pageResult = contactService.getContactPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索应急联络通讯录")
    @Parameter(name = "keyword", description = "关键词", example = "张三")
    @Parameter(name = "contactType", description = "联系人类型", example = "internal")
    public CommonResult<PageResult<ContactRespVO>> searchContact(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "contactType", required = false) String contactType) {
        PageResult<ContactRespVO> pageResult = contactService.searchContact(keyword, contactType);
        return success(pageResult);
    }
}



