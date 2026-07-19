package cn.cheers.x.maintenance.controller.admin.vo.handbook;

import cn.cheers.x.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HandbookPageReqVO extends PageParam {
    private String code;
    private String name;
    private String scope;
    private Integer status;
}
