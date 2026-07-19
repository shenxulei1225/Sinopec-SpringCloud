package cn.cheers.x.maintenance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HandbookStatusEnum {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布");

    private final Integer status;
    private final String name;
}
