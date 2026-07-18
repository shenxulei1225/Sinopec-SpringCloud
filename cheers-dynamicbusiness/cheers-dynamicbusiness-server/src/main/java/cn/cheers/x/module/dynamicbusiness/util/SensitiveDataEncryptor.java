package cn.cheers.x.module.dynamicbusiness.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 敏感数据加密工具类
 * 
 * 使用 AES-256-GCM 算法对敏感信息进行加密存储
 * 
 * 对应任务：T022d - 敏感信息加密方案
 * 
 * @author system
 */
@Slf4j
public class SensitiveDataEncryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final String KEY_PROPERTY_NAME = "yudao.sensitive-data.encrypt-key";
    private static final String DEFAULT_KEY = "default-sensitive-data-encrypt-key-32bytes!!"; // 32 bytes for AES-256

    private static final AtomicBoolean DEFAULT_KEY_WARNED = new AtomicBoolean(false);

    /**
     * 加密敏感数据
     * 
     * @param plainText 明文
     * @return Base64编码的密文（包含IV和加密数据）
     */
    public static String encrypt(String plainText) {
        if (StrUtil.isBlank(plainText)) {
            return plainText;
        }
        
        try {
            byte[] key = getEncryptionKey();
            byte[] iv = generateIV();
            
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
            
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // 将IV和加密数据组合：IV(12字节) + 加密数据
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            log.error("敏感数据加密失败", e);
            throw new RuntimeException("敏感数据加密失败", e);
        }
    }

    /**
     * 解密敏感数据
     * 
     * @param cipherText Base64编码的密文（包含IV和加密数据）
     * @return 明文
     */
    public static String decrypt(String cipherText) {
        if (StrUtil.isBlank(cipherText)) {
            return cipherText;
        }
        
        // 检查是否是加密格式（Base64编码，长度足够包含IV）
        if (!isEncrypted(cipherText)) {
            // 如果不是加密格式，可能是历史数据或未加密数据，直接返回
            return cipherText;
        }
        
        try {
            byte[] combined = Base64.getDecoder().decode(cipherText);
            
            // 提取IV和加密数据
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);
            
            byte[] key = getEncryptionKey();
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
            
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("敏感数据解密失败，可能是数据格式错误或密钥不匹配", e);
            // 解密失败时，返回原始数据（可能是历史未加密数据）
            return cipherText;
        }
    }

    /**
     * 判断字符串是否是加密格式
     * 
     * @param text 待检查的文本
     * @return true表示可能是加密格式
     */
    private static boolean isEncrypted(String text) {
        if (StrUtil.isBlank(text)) {
            return false;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(text);
            // 加密格式应该至少包含IV（12字节）+ 加密数据（至少16字节GCM tag）
            return decoded.length >= GCM_IV_LENGTH + GCM_TAG_LENGTH;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成随机IV（初始化向量）
     */
    private static byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new java.security.SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * 获取加密密钥
     * 优先从配置读取，如果没有配置则使用默认密钥（生产环境必须配置）
     */
    private static byte[] getEncryptionKey() {
        String keyStr = SpringUtil.getProperty(KEY_PROPERTY_NAME, DEFAULT_KEY);
        if (DEFAULT_KEY.equals(keyStr)) {
            if (DEFAULT_KEY_WARNED.compareAndSet(false, true)) {
                log.warn("使用默认加密密钥，生产环境请配置 yudao.sensitive-data.encrypt-key");
            }
        }
        // AES-256需要32字节密钥
        byte[] keyBytes = keyStr.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // 如果密钥长度不足32字节，使用SHA-256哈希扩展
            java.security.MessageDigest sha = null;
            try {
                sha = java.security.MessageDigest.getInstance("SHA-256");
                keyBytes = sha.digest(keyBytes);
            } catch (Exception e) {
                throw new RuntimeException("密钥处理失败", e);
            }
        } else if (keyBytes.length > 32) {
            // 如果密钥长度超过32字节，截取前32字节
            byte[] truncated = new byte[32];
            System.arraycopy(keyBytes, 0, truncated, 0, 32);
            keyBytes = truncated;
        }
        return keyBytes;
    }
}

