package cn.cheers.x.module.dynamicbusiness.service.work;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateRecordRulesTest {

    @Test
    void 旧的证书型号不能再录入() {
        String error = CertificateRecordRules.certificateSaveError(
                CertificateRecordRules.GENERIC_MODEL, "焊接",
                CertificateRecordRules.KIND_INTERNAL, 1L, null, true, false);
        assertEquals("请改选具体的证书类型", error);
    }

    @Test
    void 没有准许作业的型号不能录入() {
        String error = CertificateRecordRules.certificateSaveError(
                "cert-welder", "  ",
                CertificateRecordRules.KIND_INTERNAL, 1L, null, true, false);
        assertEquals("这个证书类型没有写准许的作业", error);
    }

    @Test
    void 内部人员不能同时填外部编号() {
        String error = CertificateRecordRules.certificateSaveError(
                "cert-welder", "焊接",
                CertificateRecordRules.KIND_INTERNAL, 1L, 9L, true, false);
        assertEquals("内部人员只填写组织里的持证人", error);
    }

    @Test
    void 外部人员必须选到名册() {
        String error = CertificateRecordRules.certificateSaveError(
                "cert-hot-work", "动火",
                CertificateRecordRules.KIND_EXTERNAL, null, null, false, false);
        assertEquals("外部施工人员只填写外部施工人员名册里的持证人", error);
    }

    @Test
    void 同一人同种未过期证书拒绝再录() {
        String error = CertificateRecordRules.certificateSaveError(
                "cert-welder", "焊接",
                CertificateRecordRules.KIND_EXTERNAL, null, 4L, true, true);
        assertEquals("这个人已经有一张未过期的同种证书。换证时先把旧证到期日改到已过期", error);
    }

    @Test
    void 合格证书可以保存() {
        assertNull(CertificateRecordRules.certificateSaveError(
                "cert-height", "高处",
                CertificateRecordRules.KIND_INTERNAL, 3L, null, true, false));
    }

    @Test
    void 焊接记录没有焊工证时拒绝() {
        String error = CertificateRecordRules.weldingSaveError(
                "焊接", CertificateRecordRules.KIND_EXTERNAL, null, 8L, true, false);
        assertEquals("这名焊工没有未过期的焊工证", error);
    }

    @Test
    void 非焊接记录不核对焊工() {
        assertNull(CertificateRecordRules.weldingSaveError(
                "回填恢复", null, null, null, false, false));
    }

    @Test
    void 到期日空着或已过期都不算有效() {
        LocalDate today = LocalDate.of(2026, 11, 26);
        assertFalse(CertificateRecordRules.isUnexpired(null, today));
        assertFalse(CertificateRecordRules.isUnexpired(today.minusDays(1), today));
        assertTrue(CertificateRecordRules.isUnexpired(today, today));
    }

    @Test
    void 外部施工人员证件号不能空也不能重复() {
        assertEquals("请填写证件号", CertificateRecordRules.externalWorkerSaveError("  ", false));
        assertEquals("这个证件号已经有一名外部施工人员",
                CertificateRecordRules.externalWorkerSaveError("350100", true));
        assertNull(CertificateRecordRules.externalWorkerSaveError("350100", false));
    }
}
