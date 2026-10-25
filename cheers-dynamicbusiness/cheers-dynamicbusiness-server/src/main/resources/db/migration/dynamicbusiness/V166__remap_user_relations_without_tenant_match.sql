-- 用户角色、岗位、社交绑定和授权同意不按租户列过滤。
-- 旧用户编号只要唯一对应一个用户目录编号，就把关系改到这个编号。
-- V165 要求关系租户与用户租户相同，漏掉了租户列为 0、或与用户租户不一致的关系。
-- 同一个旧编号对应多个用户目录编号时不改，避免把关系写到不确定的人身上。

UPDATE public.system_user_role relation
SET user_id = mapping.user_account_id,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
FROM public.system_user_account_id_map mapping
WHERE relation.deleted = false
  AND relation.user_id = mapping.old_system_user_id
  AND relation.user_id <> mapping.user_account_id
  AND (
      SELECT count(DISTINCT candidate.user_account_id)
      FROM public.system_user_account_id_map candidate
      WHERE candidate.old_system_user_id = relation.user_id
  ) = 1;

UPDATE public.system_user_post relation
SET user_id = mapping.user_account_id,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
FROM public.system_user_account_id_map mapping
WHERE relation.deleted = false
  AND relation.user_id = mapping.old_system_user_id
  AND relation.user_id <> mapping.user_account_id
  AND (
      SELECT count(DISTINCT candidate.user_account_id)
      FROM public.system_user_account_id_map candidate
      WHERE candidate.old_system_user_id = relation.user_id
  ) = 1;

UPDATE public.system_social_user_bind relation
SET user_id = mapping.user_account_id,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
FROM public.system_user_account_id_map mapping
WHERE relation.deleted = false
  AND relation.user_type = 1
  AND relation.user_id = mapping.old_system_user_id
  AND relation.user_id <> mapping.user_account_id
  AND (
      SELECT count(DISTINCT candidate.user_account_id)
      FROM public.system_user_account_id_map candidate
      WHERE candidate.old_system_user_id = relation.user_id
  ) = 1;

UPDATE public.system_oauth2_approve relation
SET user_id = mapping.user_account_id,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
FROM public.system_user_account_id_map mapping
WHERE relation.deleted = false
  AND relation.user_type = 1
  AND relation.user_id = mapping.old_system_user_id
  AND relation.user_id <> mapping.user_account_id
  AND (
      SELECT count(DISTINCT candidate.user_account_id)
      FROM public.system_user_account_id_map candidate
      WHERE candidate.old_system_user_id = relation.user_id
  ) = 1;
