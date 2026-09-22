package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteRespDTO;

/**
 * 保存路线：校验 workItem 快照后写入总任务 {@code plannedRoute}（动态业务 ent_task / FLD-TSK-027）。
 * <p>不负责：旧表 inspection_route_plan 与 inspection_task 路线快照列（V10 已退役）。
 */
public interface PatrolSaveRouteService {

    PatrolSaveRouteRespDTO saveRoute(PatrolSaveRouteReqDTO request);
}
