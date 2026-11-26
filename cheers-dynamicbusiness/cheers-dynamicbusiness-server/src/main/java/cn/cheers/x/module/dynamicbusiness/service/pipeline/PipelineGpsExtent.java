package cn.cheers.x.module.dynamicbusiness.service.pipeline;

/**
 * 一座站上管线 GPS 的外框：西、南、东、北。
 * 实时监控打开卫星图时用它决定镜头对准哪一块地。
 */
public record PipelineGpsExtent(double west, double south, double east, double north) {
}
