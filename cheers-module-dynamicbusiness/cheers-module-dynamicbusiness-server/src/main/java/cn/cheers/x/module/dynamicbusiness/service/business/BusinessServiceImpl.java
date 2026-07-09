package cn.cheers.x.module.dynamicbusiness.service.business;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.*;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.business.BusinessDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.business.BusinessEntryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.business.BusinessEntryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.business.BusinessMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.business.BusinessEntryTypeEnum;
import cn.cheers.x.module.dynamicbusiness.enums.business.BusinessNodeKindEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Validated
public class BusinessServiceImpl implements BusinessService {

    @Resource
    private BusinessMapper businessMapper;
    @Resource
    private BusinessEntryMapper businessEntryMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(BusinessCreateReqVO reqVO) {
        if (businessMapper.existsByCode(reqVO.getCode())) {
            throw new ServiceException(400, "业务编码已存在");
        }
        if (reqVO.getParentId() != null && businessMapper.selectById(reqVO.getParentId()) == null) {
            throw new ServiceException(400, "父级业务不存在");
        }
        BusinessDO business = new BusinessDO();
        copyFromVo(business, reqVO);
        if (!StringUtils.hasText(business.getNodeKind())) {
            business.setNodeKind(BusinessNodeKindEnum.LEAF.getCode());
        }
        businessMapper.insert(business);
        return business.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BusinessUpdateReqVO reqVO) {
        BusinessDO existing = businessMapper.selectById(reqVO.getId());
        if (existing == null) {
            throw new ServiceException(404, "业务不存在");
        }
        if (!Objects.equals(existing.getCode(), reqVO.getCode())
                && businessMapper.existsByCodeExcludeId(reqVO.getCode(), reqVO.getId())) {
            throw new ServiceException(400, "业务编码已存在");
        }
        if (reqVO.getParentId() != null) {
            if (Objects.equals(reqVO.getParentId(), reqVO.getId())) {
                throw new ServiceException(400, "父级业务不能是自身");
            }
            if (businessMapper.selectById(reqVO.getParentId()) == null) {
                throw new ServiceException(400, "父级业务不存在");
            }
            if (isDescendant(reqVO.getParentId(), reqVO.getId())) {
                throw new ServiceException(400, "父级业务不能是当前业务的子级");
            }
        }
        copyFromVo(existing, reqVO);
        businessMapper.updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        BusinessDO business = businessMapper.selectById(id);
        if (business == null) {
            throw new ServiceException(404, "业务不存在");
        }
        if (businessMapper.selectCountByParentId(id) > 0) {
            throw new ServiceException(400, "请先删除子业务");
        }
        businessMapper.deleteById(id);
    }

    @Override
    public BusinessRespVO get(Long id) {
        BusinessDO business = businessMapper.selectById(id);
        if (business == null) {
            throw new ServiceException(404, "业务不存在");
        }
        BusinessRespVO vo = convertToVo(business);
        normalizeOrphanParent(vo);
        vo.setEntries(List.of());
        return vo;
    }

    @Override
    public BusinessRespVO getByCode(String code) {
        BusinessDO business = businessMapper.selectByCode(code);
        return business != null ? get(business.getId()) : null;
    }

    @Override
    public List<BusinessRespVO> listTree() {
        List<BusinessRespVO> list = businessMapper.selectAllList().stream().map(this::convertToVo).toList();
        normalizeOrphanParents(list);
        return buildTree(list, null);
    }

    @Override
    public List<BusinessRespVO> listAll() {
        List<BusinessRespVO> flattened = new ArrayList<>();
        flattenDfs(listTree(), flattened);
        return flattened;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        BusinessDO business = businessMapper.selectById(id);
        if (business == null) {
            throw new ServiceException(404, "业务不存在");
        }
        businessMapper.updateById(BusinessDO.builder().id(id).status(status).build());
    }

    private void copyFromVo(BusinessDO target, BusinessBaseVO reqVO) {
        target.setCode(reqVO.getCode());
        target.setName(reqVO.getName());
        target.setSort(reqVO.getSort());
        target.setStatus(reqVO.getStatus());
        target.setDescription(reqVO.getDescription());
        target.setIcon(reqVO.getIcon());
        target.setAlias(reqVO.getAlias());
        target.setParentId(reqVO.getParentId());
        if (StringUtils.hasText(reqVO.getNodeKind())) {
            target.setNodeKind(reqVO.getNodeKind());
        }
    }

    private BusinessRespVO convertToVo(BusinessDO business) {
        BusinessRespVO vo = new BusinessRespVO();
        vo.setId(business.getId());
        vo.setCode(business.getCode());
        vo.setName(business.getName());
        vo.setSort(business.getSort());
        vo.setStatus(business.getStatus());
        vo.setDescription(business.getDescription());
        vo.setIcon(business.getIcon());
        vo.setAlias(business.getAlias());
        vo.setParentId(business.getParentId());
        vo.setNodeKind(business.getNodeKind());
        vo.setCreateTime(business.getCreateTime());
        return vo;
    }

    private BusinessEntryRespVO convertEntryToVo(BusinessEntryDO entry) {
        BusinessEntryRespVO vo = new BusinessEntryRespVO();
        vo.setId(entry.getId());
        vo.setBusinessId(entry.getBusinessId());
        vo.setCode(entry.getCode());
        vo.setName(entry.getName());
        vo.setEntryType(entry.getEntryType());
        vo.setEntityTypeCode(entry.getEntityTypeCode());
        vo.setScopeConfig(entry.getScopeConfig());
        vo.setPageConfigId(entry.getPageConfigId());
        vo.setSort(entry.getSort());
        vo.setStatus(entry.getStatus());
        vo.setCreateTime(entry.getCreateTime());
        return vo;
    }

    private List<BusinessRespVO> buildTree(List<BusinessRespVO> list, Long parentId) {
        return list.stream()
                .filter(vo -> isTreeChildOf(vo.getParentId(), parentId))
                .sorted(Comparator
                        .comparing((BusinessRespVO vo) -> vo.getSort() == null ? Integer.MAX_VALUE : vo.getSort())
                        .thenComparing(vo -> vo.getId() == null ? Long.MAX_VALUE : vo.getId()))
                .map(vo -> {
                    BusinessRespVO node = copyVoShallow(vo);
                    node.setChildren(buildTree(list, node.getId()));
                    return node;
                })
                .toList();
    }

    private BusinessRespVO copyVoShallow(BusinessRespVO source) {
        BusinessRespVO target = new BusinessRespVO();
        target.setId(source.getId());
        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setSort(source.getSort());
        target.setStatus(source.getStatus());
        target.setDescription(source.getDescription());
        target.setIcon(source.getIcon());
        target.setAlias(source.getAlias());
        target.setParentId(source.getParentId());
        target.setNodeKind(source.getNodeKind());
        target.setCreateTime(source.getCreateTime());
        return target;
    }

    /** 判断 candidateParentId 是否为 businessId 的子孙节点（防环） */
    private boolean isDescendant(Long candidateParentId, Long businessId) {
        Long cursor = candidateParentId;
        Set<Long> visited = new HashSet<>();
        while (cursor != null && cursor != 0L) {
            if (Objects.equals(cursor, businessId)) {
                return true;
            }
            if (!visited.add(cursor)) {
                return false;
            }
            BusinessDO parent = businessMapper.selectById(cursor);
            if (parent == null) {
                return false;
            }
            cursor = parent.getParentId();
        }
        return false;
    }

    /** parent_id 为空或 0 视为根；建树前已将「父节点不存在」的孤儿提升为根 */
    private boolean isTreeChildOf(Long nodeParentId, Long expectedParentId) {
        if (expectedParentId == null) {
            return nodeParentId == null || nodeParentId == 0L;
        }
        return Objects.equals(nodeParentId, expectedParentId);
    }

    /** 单条查询：父业务不存在时按根业务返回 */
    private void normalizeOrphanParent(BusinessRespVO vo) {
        Long parentId = vo.getParentId();
        if (parentId == null || parentId == 0L) {
            return;
        }
        if (businessMapper.selectById(parentId) == null) {
            vo.setParentId(null);
        }
    }

    /** 父业务 id 在库中不存在时，按根业务参与门户树展示 */
    private void normalizeOrphanParents(List<BusinessRespVO> list) {
        Set<Long> ids = new HashSet<>();
        for (BusinessRespVO vo : list) {
            if (vo.getId() != null) {
                ids.add(vo.getId());
            }
        }
        for (BusinessRespVO vo : list) {
            Long parentId = vo.getParentId();
            if (parentId == null || parentId == 0L) {
                continue;
            }
            if (!ids.contains(parentId)) {
                vo.setParentId(null);
            }
        }
    }

    private void flattenDfs(List<BusinessRespVO> nodes, List<BusinessRespVO> out) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        for (BusinessRespVO node : nodes) {
            out.add(node);
            flattenDfs(node.getChildren(), out);
            node.setChildren(null);
        }
    }
}
