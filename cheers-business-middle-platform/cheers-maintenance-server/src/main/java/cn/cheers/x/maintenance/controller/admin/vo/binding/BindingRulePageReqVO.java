package cn.cheers.x.maintenance.controller.admin.vo.binding;
import cn.cheers.x.framework.common.pojo.PageParam;
import lombok.Data; import lombok.EqualsAndHashCode;
@Data @EqualsAndHashCode(callSuper = true)
public class BindingRulePageReqVO extends PageParam {
    private String code; private String scope; private Integer status;
}
