package cn.cheers.x.module.dynamicbusiness.service.drag;

import cn.cheers.x.module.dynamicbusiness.controller.admin.drag.vo.DragExecuteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.drag.vo.DragExecuteRespVO;

public interface DragService {

    DragExecuteRespVO execute(DragExecuteReqVO reqVO);
}
