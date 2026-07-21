package cn.cheers.x.bpm.api.task;

import cn.cheers.x.bpm.api.task.dto.BpmActivityNodeRespDTO;
import cn.cheers.x.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.cheers.x.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.cheers.x.bpm.controller.admin.task.vo.task.BpmTaskApproveReqVO;
import cn.cheers.x.bpm.service.task.BpmProcessInstanceService;
import cn.cheers.x.bpm.service.task.BpmTaskService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * Flowable 流程实例 Api 实现类
 *
 * @author jason
 */
@RestController
@Validated
public class BpmProcessInstanceApiImpl implements BpmProcessInstanceApi {

    @Resource
    private BpmProcessInstanceService processInstanceService;
    @Resource
    private BpmTaskService bpmTaskService;
    @Resource
    private RuntimeService runtimeService;

    @Override
    public CommonResult<String> createProcessInstance(Long userId, @Valid BpmProcessInstanceCreateReqDTO reqDTO) {
        return success(processInstanceService.createProcessInstance(userId, reqDTO));
    }

    @Override
    public CommonResult<List<BpmActivityNodeRespDTO>> getRunningTasksByBusinessKey(String businessKey) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        if (processInstance == null) {
            return success(Collections.emptyList());
        }
        List<Task> tasks = bpmTaskService.getRunningTaskListByProcessInstanceId(
                processInstance.getId(), null, null);
        List<BpmActivityNodeRespDTO> nodes = tasks.stream().map(task -> {
            BpmActivityNodeRespDTO dto = new BpmActivityNodeRespDTO();
            dto.setTaskId(task.getId());
            dto.setTaskDefinitionKey(task.getTaskDefinitionKey());
            dto.setName(task.getName());
            dto.setProcessInstanceId(task.getProcessInstanceId());
            return dto;
        }).collect(Collectors.toList());
        return success(nodes);
    }

    @Override
    public CommonResult<Boolean> approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqDTO) {
        BpmTaskApproveReqVO reqVO = BeanUtils.toBean(reqDTO, BpmTaskApproveReqVO.class);
        bpmTaskService.approveTask(userId, reqVO);
        return success(Boolean.TRUE);
    }

}
