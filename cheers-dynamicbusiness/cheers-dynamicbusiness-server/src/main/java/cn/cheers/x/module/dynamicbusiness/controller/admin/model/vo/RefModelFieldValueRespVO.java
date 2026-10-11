package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 引用型号字段已经取好的值。
 * 每一行的 value 是可以直接显示的一句。调用方不再拆自定义字段。
 */
@Schema(description = "管理后台 - 引用型号字段取值")
@Data
public class RefModelFieldValueRespVO {

    @Schema(description = "按点名顺序排好的字段。没有取值时 value 为空")
    private List<Line> lines;

    @Schema(description = "这一次读不到型号时的说明。有说明时 lines 为空")
    private String notice;

    @Schema(description = "这次读到的型号")
    private Long modelId;

    @Schema(description = "一行已经取好的型号字段")
    @Data
    public static class Line {

        @Schema(description = "字段编号")
        private Long fieldId;

        @Schema(description = "字段名称")
        private String label;

        @Schema(description = "可以直接显示的取值。空表示这个字段还没填")
        private String value;
    }

    public static RefModelFieldValueRespVO ofLines(List<Line> lines) {
        RefModelFieldValueRespVO resp = new RefModelFieldValueRespVO();
        resp.setLines(lines);
        return resp;
    }

    public static RefModelFieldValueRespVO ofNotice(String notice) {
        RefModelFieldValueRespVO resp = new RefModelFieldValueRespVO();
        resp.setLines(List.of());
        resp.setNotice(notice);
        return resp;
    }
}
