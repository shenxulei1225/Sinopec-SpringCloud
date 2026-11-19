package cn.cheers.x.module.dynamicbusiness.service.organization;

/**
 * 组织机构目录里的一条记录（集团、省公司、分公司或作业区）。
 * modelId 是这条记录的组织类型，用来列出这一级适用的职务。
 */
public record OrganizationNodeRow(long entityId, String name, Long parentEntityId, Long modelId) {
}
