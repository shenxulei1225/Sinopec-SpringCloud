package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 关键词多列 OR 条件：MyBatis-Plus wrapper 与原生 SQL 共用语义。
 */
public final class KeywordSearchSql {

    private static final Pattern SAFE_PHYSICAL_COLUMN = Pattern.compile("^[a-z][a-z0-9_]*$");

    private KeywordSearchSql() {
    }

    /**
     * 应用到实体表 wrapper。keyword 为空则不追加；spec 为空则退化为 name LIKE（历史行为）。
     */
    public static void applyToWrapper(LambdaQueryWrapperX<EntityDO> wrapper,
                                      String keyword,
                                      KeywordSearchSpec spec) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }
        String k = keyword.trim();
        KeywordSearchSpec effective = spec != null ? spec : KeywordSearchSpec.nameOnly();
        Long idValue = effective.matchId() ? tryParseLong(k) : null;
        List<String> cols = sanitizeLikeColumns(effective.likeColumns());

        if (idValue == null && cols.isEmpty()) {
            wrapper.like(EntityDO::getName, k);
            return;
        }

        wrapper.and(w -> {
            boolean first = true;
            if (idValue != null) {
                w.eq(EntityDO::getId, idValue);
                first = false;
            }
            for (String col : cols) {
                if (!first) {
                    w.or();
                }
                first = false;
                switch (col) {
                    case "name" -> w.like(EntityDO::getName, k);
                    case "code" -> w.like(EntityDO::getCode, k);
                    case "status" -> w.apply("CAST(status AS TEXT) ILIKE {0}", "%" + k + "%");
                    default -> w.apply(col + "::text ILIKE {0}", "%" + k + "%");
                }
            }
        });
    }

    /**
     * 追加 {@code AND ( ... OR ... )} 到原生 SQL（表别名 {@code e}）。
     *
     * @return 是否追加了条件
     */
    public static boolean appendToNativeWhere(StringBuilder where,
                                              List<Object> args,
                                              String keyword,
                                              KeywordSearchSpec spec) {
        if (!StringUtils.hasText(keyword)) {
            return false;
        }
        String k = keyword.trim();
        KeywordSearchSpec effective = spec != null ? spec : KeywordSearchSpec.nameOnly();
        Long idValue = effective.matchId() ? tryParseLong(k) : null;
        List<String> cols = sanitizeLikeColumns(effective.likeColumns());

        if (idValue == null && cols.isEmpty()) {
            where.append(" AND e.name ILIKE ?");
            args.add("%" + k + "%");
            return true;
        }

        List<String> parts = new ArrayList<>();
        if (idValue != null) {
            parts.add("e.id = ?");
            args.add(idValue);
        }
        for (String col : cols) {
            if ("name".equals(col) || "code".equals(col)) {
                parts.add("e." + col + " ILIKE ?");
                args.add("%" + k + "%");
            } else if ("status".equals(col)) {
                parts.add("CAST(e.status AS TEXT) ILIKE ?");
                args.add("%" + k + "%");
            } else {
                parts.add("e." + col + "::text ILIKE ?");
                args.add("%" + k + "%");
            }
        }
        if (parts.isEmpty()) {
            return false;
        }
        where.append(" AND (").append(String.join(" OR ", parts)).append(")");
        return true;
    }

    private static List<String> sanitizeLikeColumns(List<String> likeColumns) {
        if (likeColumns == null || likeColumns.isEmpty()) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (String raw : likeColumns) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            String col = raw.trim().toLowerCase(Locale.ROOT);
            if ("name".equals(col) || "code".equals(col) || "status".equals(col)
                    || SAFE_PHYSICAL_COLUMN.matcher(col).matches()) {
                if (!out.contains(col)) {
                    out.add(col);
                }
            }
        }
        return out;
    }

    private static Long tryParseLong(String raw) {
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
