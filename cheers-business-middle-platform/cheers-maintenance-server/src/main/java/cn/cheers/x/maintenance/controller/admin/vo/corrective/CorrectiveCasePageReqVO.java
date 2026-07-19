package cn.cheers.x.maintenance.controller.admin.vo.corrective;
import cn.cheers.x.framework.common.pojo.PageParam;
import lombok.Data; import lombok.EqualsAndHashCode;
@Data @EqualsAndHashCode(callSuper = true)
public class CorrectiveCasePageReqVO extends PageParam {
    private String title; private String status;
}
