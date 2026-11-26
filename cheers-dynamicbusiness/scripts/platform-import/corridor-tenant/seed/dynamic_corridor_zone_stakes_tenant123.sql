-- 图上和防火区名称写在一起的桩号区间。
-- 神墩五路 1# 旁边是 SW0+282.1～SW0+482.1，2# 旁边是 SW0+482.1～SW0+681。
-- 纵断面同一句写了 2# 的范围 SW0+482.1～SW0+681。
-- 托月路 4# 的 150 米只对上 TY0+191.3～TY0+341.3。5# 的 132.7 米只对上 TY0+341.3～TY0+474。
-- 其余区间的长度对不上图上的米数，不写。
SET search_path TO dynamicbusiness;

UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object('FLD-LOC-006', v.s, 'FLD-LOC-007', v.e),
    updater = 'corridor-zone-stakes',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
    (67, 'SW0+282.1', 'SW0+482.1'),
    (68, 'SW0+482.1', 'SW0+681'),
    (402, 'TY0+191.3', 'TY0+341.3'),
    (404, 'TY0+341.3', 'TY0+474')
) AS v(id, s, e)
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.id = v.id;
