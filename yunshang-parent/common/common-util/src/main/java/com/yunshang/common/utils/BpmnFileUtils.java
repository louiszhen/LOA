package com.yunshang.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * BPMN 文件工具类
 * 用于处理 BPMN 文件的编码问题，特别是移除 BOM 标记
 */
@Slf4j
public class BpmnFileUtils {

    /**
     * UTF-8 BOM 标记
     */
    private static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    /**
     * 检查文件是否包含 BOM 标记
     *
     * @param file 需要检查的文件
     * @return true 如果包含 BOM，否则 false
     */
    public static boolean hasBom(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bom = new byte[3];
            int read = fis.read(bom);
            if (read >= 3) {
                return bom[0] == UTF8_BOM[0] && bom[1] == UTF8_BOM[1] && bom[2] == UTF8_BOM[2];
            }
        } catch (IOException e) {
            log.error("检查 BOM 失败: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 移除文件中的 BOM 标记
     *
     * @param inputFile  包含 BOM 的文件
     * @param outputFile 输出文件（移除 BOM 后）
     * @return true 如果成功移除 BOM，否则 false
     */
    public static boolean removeBom(File inputFile, File outputFile) {
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            // 检查并跳过 BOM
            byte[] firstBytes = new byte[3];
            int read = fis.read(firstBytes);

            // 如果没有 BOM，直接复制
            if (read < 3 || !(firstBytes[0] == UTF8_BOM[0] &&
                          firstBytes[1] == UTF8_BOM[1] &&
                          firstBytes[2] == UTF8_BOM[2])) {
                // 没有 BOM，写入原始数据
                fos.write(firstBytes, 0, read);
            } else {
                log.info("检测到 BOM 标记，已移除");
            }

            // 复制剩余内容
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            return true;
        } catch (IOException e) {
            log.error("移除 BOM 失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 就地移除 BOM（备份原文件）
     *
     * @param file 需要处理的文件
     * @return true 如果成功，否则 false
     */
    public static boolean removeBomInPlace(File file) {
        if (!hasBom(file)) {
            log.info("文件 {} 不包含 BOM 标记，无需处理", file.getName());
            return true;
        }

        // 创建备份
        File backupFile = new File(file.getAbsolutePath() + ".bak");
        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(backupFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            log.info("已创建备份文件: {}", backupFile.getName());
        } catch (IOException e) {
            log.error("创建备份失败: {}", e.getMessage());
            return false;
        }

        // 移除 BOM
        boolean success = removeBom(backupFile, file);

        if (success) {
            log.info("已成功移除 BOM: {}", file.getName());
        } else {
            log.error("移除 BOM 失败，尝试恢复备份");
            try (FileInputStream fis = new FileInputStream(backupFile);
                 FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                log.error("恢复备份失败: {}", e.getMessage());
            }
        }

        // 删除备份
        boolean deleted = backupFile.delete();
        if (!deleted) {
            log.warn("备份文件删除失败: {}", backupFile.getName());
        }

        return success;
    }

    /**
     * 验证 BPMN 文件是否为有效的 XML
     *
     * @param file 需要验证的文件
     * @return true 如果是有效的 XML，否则 false
     */
    public static boolean isValidXml(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            // 检查 BOM
            byte[] firstBytes = new byte[3];
            int read = fis.read(firstBytes);

            InputStream is = fis;
            // 如果有 BOM，跳过 BOM 后再验证
            if (read == 3 &&
                firstBytes[0] == UTF8_BOM[0] &&
                firstBytes[1] == UTF8_BOM[1] &&
                firstBytes[2] == UTF8_BOM[2]) {
                is = new BufferedInputStream(new FileInputStream(file));
                ((BufferedInputStream) is).skip(3);
            }

            // 简单验证：检查是否以 <?xml 开头
            byte[] xmlHeader = new byte[5];
            int headerRead = is.read(xmlHeader);
            if (headerRead == 5) {
                String header = new String(xmlHeader, "UTF-8");
                return header.equals("<?xml");
            }

            return false;
        } catch (IOException e) {
            log.error("验证 XML 失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 打印文件的前 50 个字节（十六进制），用于调试
     *
     * @param file 需要检查的文件
     */
    public static void printFileHeader(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[50];
            int read = fis.read(header);

            StringBuilder hex = new StringBuilder();
            StringBuilder ascii = new StringBuilder();

            for (int i = 0; i < read; i++) {
                hex.append(String.format("%02X ", header[i]));

                if (header[i] >= 32 && header[i] <= 126) {
                    ascii.append((char) header[i]);
                } else {
                    ascii.append(".");
                }

                if ((i + 1) % 16 == 0) {
                    log.info("HEX: {} | ASCII: {}", hex.toString(), ascii.toString());
                    hex = new StringBuilder();
                    ascii = new StringBuilder();
                }
            }

            if (hex.length() > 0) {
                log.info("HEX: {} | ASCII: {}", hex.toString(), ascii.toString());
            }

        } catch (IOException e) {
            log.error("读取文件头失败: {}", e.getMessage());
        }
    }
}
