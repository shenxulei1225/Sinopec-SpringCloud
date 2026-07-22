package cn.iocoder.yudao.module.emergency.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java 注释读取器
 *
 * 从 Java 源文件中读取字段注释，用于自动生成 Swagger 描述
 *
 * @author 系统生成
 */
@Component
@Slf4j
public class JavaDocCommentReader {

    /**
     * 读取类的字段注释
     *
     * @param clazz 要读取注释的类
     * @return 字段名 -> 注释内容的映射
     */
    public Map<String, String> readFieldComments(Class<?> clazz) {
        Map<String, String> fieldComments = new HashMap<>();

        try {
            // 获取源文件路径
            String className = clazz.getName();
            String sourcePath = className.replace('.', '/') + ".java";

            ClassPathResource resource = new ClassPathResource("META-INF/resources/" + sourcePath);
            if (!resource.exists()) {
                // 尝试其他可能的路径
                resource = new ClassPathResource(sourcePath);
            }

            if (resource.exists()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }

                    parseFieldComments(content.toString(), fieldComments);
                }
            } else {
                log.debug("找不到源文件: {}", sourcePath);
            }

        } catch (Exception e) {
            log.warn("读取类 {} 的注释失败: {}", clazz.getSimpleName(), e.getMessage());
        }

        return fieldComments;
    }

    /**
     * 解析字段注释
     *
     * @param sourceCode 源代码内容
     * @param fieldComments 用于存储解析结果的映射
     */
    private void parseFieldComments(String sourceCode, Map<String, String> fieldComments) {
        String[] lines = sourceCode.split("\n");
        StringBuilder currentComment = new StringBuilder();
        boolean inComment = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // 检查是否是注释开始
            if (line.startsWith("/**") && !inComment) {
                inComment = true;
                currentComment.setLength(0);
                // 移除开头的 /**
                if (line.length() > 3) {
                    currentComment.append(line.substring(3).trim());
                }
                continue;
            }

            // 检查是否在注释中
            if (inComment) {
                if (line.startsWith("*/")) {
                    // 注释结束，查找下一个字段
                    inComment = false;
                    String fieldName = findNextFieldName(lines, i + 1);
                    if (fieldName != null) {
                        String comment = cleanComment(currentComment.toString());
                        if (!comment.isEmpty()) {
                            fieldComments.put(fieldName, comment);
                        }
                    }
                } else if (line.startsWith("*")) {
                    // 注释内容行
                    String commentLine = line.substring(1).trim();
                    if (!commentLine.isEmpty()) {
                        if (currentComment.length() > 0) {
                            currentComment.append(" ");
                        }
                        currentComment.append(commentLine);
                    }
                }
                continue;
            }
        }
    }

    /**
     * 查找下一个字段名
     */
    private String findNextFieldName(String[] lines, int startIndex) {
        for (int i = startIndex; i < lines.length && i < startIndex + 5; i++) {
            String line = lines[i].trim();
            // 匹配字段声明
            Pattern fieldPattern = Pattern.compile("(?:private|public|protected)\\s+\\w+\\s+(\\w+)\\s*;");
            Matcher matcher = fieldPattern.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    /**
     * 清理注释文本
     */
    private String cleanComment(String comment) {
        if (comment == null) {
            return "";
        }

        // 移除常见的注释标记
        comment = comment.replaceAll("@.*$", "").trim(); // 移除 @author 等标签
        comment = comment.replaceAll("<[^>]+>", "").trim(); // 移除 HTML 标签

        // 如果注释以字段名开头，去掉它
        if (comment.contains(" - ")) {
            int dashIndex = comment.indexOf(" - ");
            if (dashIndex > 0) {
                comment = comment.substring(dashIndex + 3).trim();
            }
        }

        return comment;
    }

    /**
     * 获取字段注释
     *
     * @param clazz 要读取的类
     * @param fieldName 字段名
     * @return 字段注释，如果没有找到返回 null
     */
    public String getFieldComment(Class<?> clazz, String fieldName) {
        Map<String, String> comments = readFieldComments(clazz);
        return comments.get(fieldName);
    }
}
