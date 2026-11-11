package cn.cheers.x.module.dynamicbusiness.service.workorder;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogCompleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogRespVO;

/**
 * 工单目录的写入和读取。
 *
 * 管什么：数据目录 work_order 上的一条工单。型号「维修」的这一条就是这次维修记录。
 * 不管什么：报修受理、作业标准正文、独立工单表 wo_work_order。
 * 禁止：派工再写一份到 wo_work_order；用场站编号填设备引用。
 */
public interface WorkOrderCatalogService {

    Long create(WorkOrderCatalogCreateReqVO reqVO);

    WorkOrderCatalogRespVO get(Long id);

    PageResult<WorkOrderCatalogRespVO> page(WorkOrderCatalogPageReqVO reqVO);

    void start(Long id);

    void complete(WorkOrderCatalogCompleteReqVO reqVO);

    void claim(Long id);

    void assign(Long id, Long assigneeUserId);

    /** 执行人退回。清空执行人。班组和处理时刻不动。 */
    void release(Long id);
}
