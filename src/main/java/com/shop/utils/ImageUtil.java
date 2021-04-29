package com.shop.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 图片压缩工具类
 *
 * @author lnj
 * createTime 2018-10-19 15:31
 **/
public class ImageUtil {

    // 图片默认缩放比率
    private static final double DEFAULT_SCALE = 1d;

    // 缩略图后缀
    private static final String SUFFIX = "-thumbnail";


    /**
     * 压缩指定目录下的图片
     */
    public static void generateThumbnail2Directory(String pathname) throws IOException {
        File[] files = new File(pathname).listFiles();
        Thumbnails.of(files)
                .scale(DEFAULT_SCALE)
                .outputQuality(0.2f)
                .toFiles(new File(pathname), Rename.NO_CHANGE);
    }


    /**
     * 根据文件扩展名判断文件是否图片格式
     */
    public static boolean isImage(String extension) {
        String[] imageExtension = new String[]{"jpeg", "jpg", "gif", "bmp", "png"};

        for (String e : imageExtension) if (extension.toLowerCase().equals(e)) return true;

        return false;
    }

    public static MultipartFile compressFile(MultipartFile file, String absolutePath, float size) throws IOException {
        //记录原MultipartFile，如果压缩异常就用原来的MultipartFile
        MultipartFile oldMultipartFile = file;
        FileInputStream fileInputStream = null;
        try {
            String fileName = file.getName();
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();
            File tempFile = new File(absolutePath);
            //压缩
            Thumbnails.of(file.getInputStream())
                    .scale(1f)
                    .outputQuality(size)
                    .toFile(tempFile);
            fileInputStream = new FileInputStream(tempFile);
            file = new MockMultipartFile(fileName, originalFilename, contentType, fileInputStream);
            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
            file = oldMultipartFile;
        } finally {
            fileInputStream.close();
        }
        return file;
    }
}