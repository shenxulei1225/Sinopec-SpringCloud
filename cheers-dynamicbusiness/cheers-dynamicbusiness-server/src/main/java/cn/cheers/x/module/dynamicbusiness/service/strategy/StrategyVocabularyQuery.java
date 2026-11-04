package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.dataprocess.StrategyRecordFields;
import cn.cheers.x.strategy.StrategyPublication;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个域公布之后，详情里能勾的条件和动作。
 *
 * 管什么：按这条记录所属的域，列出条件目录和动作目录里已经有的记录。
 * 不管什么：不判定，不写记录。
 * 禁止：目录对不上时编一份告警清单。
 */
@Service
public class StrategyVocabularyQuery {

    @Resource
    private EntityRepository entityRepository;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private StrategyPublicationReader publicationReader;

    public Vocabulary load(Long entityId) {
        Vocabulary vocabulary = new Vocabulary();
        if (entityId == null) {
            return vocabulary;
        }
        EntityDO row = entityRepository.findById(entityId, StrategyRecordFields.DIRECTORY);
        if (row == null || row.getCode() == null) {
            return vocabulary;
        }
        String kind = text(row, publicationReader.fieldCode(StrategyRecordFields.KIND));
        String domainCode = StrategyRecordFields.KIND_DOMAIN.equals(kind)
                ? row.getCode()
                : text(row, publicationReader.fieldCode(StrategyRecordFields.DOMAIN_CODE));
        if (domainCode.isBlank() || !StrategyRecordFields.KIND_STRATEGY.equals(kind)) {
            return vocabulary;
        }
        StrategyPublication publication = publicationReader.load(domainCode);
        vocabulary.modes = publication.allowedActionModes().stream().map(mode -> {
            Choice choice = new Choice();
            choice.value = mode;
            choice.label = mode;
            return choice;
        }).toList();
        String conditionCatalog = text(domainRow(domainCode), publicationReader.fieldCode(StrategyRecordFields.CONDITION_CATALOG));
        String actionCatalog = text(domainRow(domainCode), publicationReader.fieldCode(StrategyRecordFields.ACTION_CATALOG));
        vocabulary.conditions = choices(conditionCatalog, domainCode, true);
        vocabulary.actions = choices(actionCatalog, domainCode, false);
        return vocabulary;
    }

    private EntityDO domainRow(String domainCode) {
        for (EntityDO row : rows(StrategyRecordFields.DIRECTORY)) {
            if (domainCode.equals(row.getCode())
                    && StrategyRecordFields.KIND_DOMAIN.equals(text(row, publicationReader.fieldCode(StrategyRecordFields.KIND)))) {
                return row;
            }
        }
        return null;
    }

    private List<Choice> choices(String catalog, String domainCode, boolean conditions) {
        List<Choice> choices = new ArrayList<>();
        if (catalog == null || catalog.isBlank()) {
            return choices;
        }
        for (EntityDO row : rows(catalog)) {
            if (row.getId() == null || !Integer.valueOf(1).equals(row.getStatus())) {
                continue;
            }
            if (conditions && StrategyRecordFields.DIRECTORY.equals(catalog)) {
                String kind = text(row, publicationReader.fieldCode(StrategyRecordFields.KIND));
                String domain = text(row, publicationReader.fieldCode(StrategyRecordFields.DOMAIN_CODE));
                if (!StrategyRecordFields.KIND_CONDITION.equals(kind) || !domainCode.equals(domain)) {
                    continue;
                }
            }
            Choice choice = new Choice();
            choice.value = String.valueOf(row.getId());
            choice.label = row.getName() == null ? choice.value : row.getName();
            choices.add(choice);
        }
        return choices;
    }

    private List<EntityDO> rows(String entityTypeCode) {
        List<Long> modelIds = new ArrayList<>();
        for (ModelDO model : modelMapper.selectByEntityTypeCode(entityTypeCode)) {
            if (model.getId() != null) {
                modelIds.add(model.getId());
            }
        }
        if (modelIds.isEmpty()) {
            return List.of();
        }
        return entityRepository.findByModelIds(modelIds, entityTypeCode);
    }

    private static String text(EntityDO row, String fieldCode) {
        if (row == null || row.getCustomFields() == null || fieldCode == null) {
            return "";
        }
        Object value = row.getCustomFields().get(fieldCode);
        return value == null ? "" : String.valueOf(value).trim();
    }

    public static class Vocabulary {
        private List<Choice> conditions = List.of();
        private List<Choice> actions = List.of();
        private List<Choice> modes = List.of();

        public List<Choice> getConditions() {
            return conditions;
        }

        public List<Choice> getActions() {
            return actions;
        }

        public List<Choice> getModes() {
            return modes;
        }
    }

    public static class Choice {
        private String value;
        private String label;

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }
}
