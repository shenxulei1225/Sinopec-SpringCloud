package cn.cheers.x.workorder.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 现场作业标准状态
 */
@Getter
@AllArgsConstructor
public enum FieldWorkStandardStatusEnum {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布");

    private final Integer status;
    private final String name;

}
