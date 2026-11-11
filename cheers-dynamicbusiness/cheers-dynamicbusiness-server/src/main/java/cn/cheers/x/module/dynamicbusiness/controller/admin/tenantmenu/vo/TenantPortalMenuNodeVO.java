package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantmenu.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** 菜单树上的一项。一级可以不填 pageCode。二级必须填 pageCode，且不能再有 children。 */
@Data
public class TenantPortalMenuNodeVO {

    @Size(max = 32, message = "菜单名称不能超过 32 个字")
    private String title;

    @Size(max = 64)
    private String pageCode;

    /** 页面登记的中文名。只在读取时填，保存不看它。 */
    private String pageName;

    @Valid
    private List<TenantPortalMenuNodeVO> children = new ArrayList<>();
}
