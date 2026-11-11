package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantmenu.vo;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** 整份保存当前租户的菜单。空列表表示清掉已保存的菜单，之后顶栏回到按已开通业务生成的样子。 */
@Data
public class TenantPortalMenuSaveReqVO {

    @Valid
    private List<TenantPortalMenuNodeVO> nodes = new ArrayList<>();
}
