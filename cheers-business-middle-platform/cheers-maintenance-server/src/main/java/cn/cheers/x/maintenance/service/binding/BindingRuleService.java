package cn.cheers.x.maintenance.service.binding;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.maintenance.api.dto.BindingResolveReqDTO;
import cn.cheers.x.maintenance.api.dto.BindingResolveRespDTO;
import cn.cheers.x.maintenance.controller.admin.vo.binding.*;

public interface BindingRuleService {
    Long create(BindingRuleCreateReqVO reqVO);
    void update(Long id, BindingRuleUpdateReqVO reqVO);
    BindingRuleRespVO get(Long id);
    PageResult<BindingRuleRespVO> page(BindingRulePageReqVO reqVO);
    Long publish(Long id);
    BindingResolveRespDTO resolve(BindingResolveReqDTO req);
}
