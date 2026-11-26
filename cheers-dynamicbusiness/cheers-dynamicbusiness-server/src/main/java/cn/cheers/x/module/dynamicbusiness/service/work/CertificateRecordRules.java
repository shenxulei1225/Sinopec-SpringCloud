package cn.cheers.x.module.dynamicbusiness.service.work;

import java.time.LocalDate;
import java.util.Set;

/**
 * 人员资质和焊接记录保存前的核对。
 *
 * 管什么：证书类型是否已写准许作业、持证人是否只指向一种人、同一人同一种未过期证书是否重复、焊接记录有没有合格焊工证。
 * 不管什么：不查库，不写目录。许可五项条件的状态机不在这里。
 * 禁止：用组织用户编号代替外部施工人员编号，或把用户手填的作业范围当成准许作业。
 */
public final class CertificateRecordRules {

    public static final String GENERIC_MODEL = "MODEL-person-certificate";
    public static final String KIND_INTERNAL = "内部人员";
    public static final String KIND_EXTERNAL = "外部施工人员";
    public static final String WELDING = "焊接";
    public static final Set<String> ALLOWED_WORKS = Set.of("焊接", "动火", "高处", "受限空间", "吊装");

    private CertificateRecordRules() {
    }

    /**
     * 录入一张证书时能不能保存。
     *
     * @param modelCode 所选证书类型的型号编码。仍是旧的「证书」型号时拒绝
     * @param allowedWork 该型号上已经写好的准许作业。空着或不是五种作业之一时拒绝
     * @param holderKind 内部人员或外部施工人员
     * @param internalUserId 组织架构用户编号。外部人员时必须空
     * @param externalWorkerId 外部施工人员编号。内部人员时必须空
     * @param holderExists 所选的人在对应账上是否存在
     * @param duplicateUnexpired 同一人、同一种证书、还有未过期的另一条
     * @return 可以保存时返回 null。不能保存时返回给用户看的一句话
     */
    public static String certificateSaveError(
            String modelCode,
            String allowedWork,
            String holderKind,
            Long internalUserId,
            Long externalWorkerId,
            boolean holderExists,
            boolean duplicateUnexpired) {
        if (GENERIC_MODEL.equals(modelCode)) {
            return "请改选具体的证书类型";
        }
        if (allowedWork == null || !ALLOWED_WORKS.contains(allowedWork)) {
            return "这个证书类型没有写准许的作业";
        }
        String holderError = holderError(holderKind, internalUserId, externalWorkerId, holderExists, "持证人");
        if (holderError != null) {
            return holderError;
        }
        if (duplicateUnexpired) {
            return "这个人已经有一张未过期的同种证书。换证时先把旧证到期日改到已过期";
        }
        return null;
    }

    /**
     * 现场记录是焊接时，焊工证是否合格。
     *
     * @param recordKind 记录种类。不是焊接时不核对焊工
     * @param hasUnexpiredWeldCertificate 该焊工有一张准许焊接、到期日不早于当天的证书
     * @return 可以保存时返回 null
     */
    public static String weldingSaveError(
            String recordKind,
            String holderKind,
            Long internalUserId,
            Long externalWorkerId,
            boolean holderExists,
            boolean hasUnexpiredWeldCertificate) {
        if (!WELDING.equals(recordKind)) {
            return null;
        }
        String holderError = holderError(holderKind, internalUserId, externalWorkerId, holderExists, "焊工");
        if (holderError != null) {
            return holderError;
        }
        if (!hasUnexpiredWeldCertificate) {
            return "这名焊工没有未过期的焊工证";
        }
        return null;
    }

    /**
     * 到期日空着，或早于当天，都不算有效。
     */
    public static boolean isUnexpired(LocalDate validUntil, LocalDate today) {
        return validUntil != null && !validUntil.isBefore(today);
    }

    /**
     * 外部施工人员没有证件号时不能保存。
     *
     * @return 可以保存时返回 null
     */
    public static String externalWorkerSaveError(String idDocumentNo, boolean duplicateIdDocument) {
        if (idDocumentNo == null || idDocumentNo.isBlank()) {
            return "请填写证件号";
        }
        if (duplicateIdDocument) {
            return "这个证件号已经有一名外部施工人员";
        }
        return null;
    }

    private static String holderError(
            String holderKind,
            Long internalUserId,
            Long externalWorkerId,
            boolean holderExists,
            String roleName) {
        boolean internal = internalUserId != null;
        boolean external = externalWorkerId != null;
        if (KIND_INTERNAL.equals(holderKind)) {
            if (!internal || external) {
                return "内部人员只填写组织里的" + roleName;
            }
        } else if (KIND_EXTERNAL.equals(holderKind)) {
            if (!external || internal) {
                return "外部施工人员只填写外部施工人员名册里的" + roleName;
            }
        } else {
            return "请选择" + roleName + "是内部人员还是外部施工人员";
        }
        if (!holderExists) {
            return "所选" + roleName + "不存在";
        }
        return null;
    }
}
