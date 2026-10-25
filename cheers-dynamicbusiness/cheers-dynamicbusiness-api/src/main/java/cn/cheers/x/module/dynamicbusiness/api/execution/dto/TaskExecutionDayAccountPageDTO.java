package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import lombok.Data;

import java.util.List;

/**
 * 巡检图层某一天的一页名单。total 是这一天的总条数，不是本页条数。
 */
@Data
public class TaskExecutionDayAccountPageDTO {

    private List<TaskExecutionDayAccountDTO> list;

    private Long total;
}
