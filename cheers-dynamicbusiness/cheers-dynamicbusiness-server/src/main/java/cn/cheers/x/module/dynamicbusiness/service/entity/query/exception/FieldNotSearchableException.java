package cn.cheers.x.module.dynamicbusiness.service.entity.query.exception;

import cn.cheers.x.module.dynamicbusiness.enums.ExtendFieldQueryErrorCodeConstants;

/**
 * 字段不可查询异常
 *
 * <p>当查询条件中包含未标记为可查询（is_searchable=false）的字段时抛出此异常。</p>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-QRY-001: 只有 is_searchable=true 的字段才能参与查询条件</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>
 * // 检查字段是否可查询
 * if (!fieldDefinition.getIsSearchable()) {
 *     throw new FieldNotSearchableException(fieldCode);
 * }
 * </pre>
 *
 * @author 扩展字段查询服务
 */
public class FieldNotSearchableException extends EntityQueryException {

    /**
     * 不可查询的字段编码
     */
    private final String fieldCode;

    /**
     * 构造字段不可查询异常
     *
     * @param fieldCode 字段编码
     */
    public FieldNotSearchableException(String fieldCode) {
        super(ExtendFieldQueryErrorCodeConstants.FIELD_NOT_SEARCHABLE, fieldCode);
        this.fieldCode = fieldCode;
    }

    /**
     * 构造字段不可查询异常（带原因）
     *
     * @param fieldCode 字段编码
     * @param cause 原因
     */
    public FieldNotSearchableException(String fieldCode, Throwable cause) {
        super(ExtendFieldQueryErrorCodeConstants.FIELD_NOT_SEARCHABLE, cause, fieldCode);
        this.fieldCode = fieldCode;
    }

    /**
     * 获取不可查询的字段编码
     */
    public String getFieldCode() {
        return fieldCode;
    }

    @Override
    public String toString() {
        return String.format("FieldNotSearchableException{fieldCode='%s', code=%d, message='%s'}", 
                fieldCode, getCode(), getMessage());
    }
}
