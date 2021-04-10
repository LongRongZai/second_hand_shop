package com.shop.utils;

import com.shop.exceptions.UploadException;
import com.shop.model.PluploadModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DecimalFormat;

public class UploadFileTool {

    public static String getPrintSize(long size) {
        //获取到的size为：1705230
        int GB = 1024 * 1024 * 1024;//定义GB的计算常量
        int MB = 1024 * 1024;//定义MB的计算常量
        int KB = 1024;//定义KB的计算常量
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String resultSize = "";
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = df.format(size / (float) GB) + "GB   ";
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = df.format(size / (float) MB) + "MB   ";
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = df.format(size / (float) KB) + "KB   ";
        } else {
            resultSize = size + "B   ";
        }
        return resultSize;
    }

    public static long getByteSize(String size) {
        long resultSize = 0;
        if (size.contains("GB")) {
            resultSize = (long) (Double.parseDouble(size.substring(0, size.indexOf('G'))) * 1024 * 1024 * 1024);
        } else if (size.contains("MB")) {
            resultSize = (long) (Double.parseDouble(size.substring(0, size.indexOf('M'))) * 1024 * 1024);
        } else if (size.contains("KB")) {
            resultSize = (long) (Double.parseDouble(size.substring(0, size.indexOf('K'))) * 1024);
        } else {
            resultSize = (long) (Double.parseDouble(size.substring(0, size.indexOf('B'))));
        }
        return resultSize;
    }

    public static PluploadModel upload(MultipartFile multipartFile, String save, String view) throws Exception {
        //创建输入输出流
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            //获取上传时的附件名
            String fileName = multipartFile.getOriginalFilename();
            //获取附件类型
            String name = StringUtils.replace(fileName, " ", "");
            String fileType = name.substring(name.lastIndexOf(".") + 1);
            //将附件名设置为时间戳
            String timeStamp = System.currentTimeMillis() + "." + fileType;
            //限制上传的附件类型
            if (fileType.toLowerCase().equals("exe") || fileType.toLowerCase().equals("java") || fileType.toLowerCase().equals("sh"))
                throw new UploadException("此附件为限制上传类型");
            //获取附件的输入流
            inputStream = multipartFile.getInputStream();
            //获取附件大小
            long fileByteSize = multipartFile.getSize();
            String fileSize = UploadFileTool.getPrintSize(inputStream.available());
            //路径+附件名
            File targetFile = new File(save + timeStamp);
            //如果之前的 String path = "d:/upload/" 没有在最后加 / ，那就要在 path 后面 + "/"
            //判断附件父目录是否存在
            if (!targetFile.getParentFile().exists()) {
                //不存在就创建一个
                targetFile.getParentFile().mkdir();
            }
            //获取附件的输出流
            outputStream = new FileOutputStream(targetFile);
            //将附件信息保存
            PluploadModel pluploadModel = new PluploadModel();
            pluploadModel.setName(fileName);
            pluploadModel.setSize(fileSize);
            pluploadModel.setType(fileType);
            pluploadModel.setPath(view);
            String viewPath = view + timeStamp;
            pluploadModel.setViewPath(viewPath);//预览地址
            //使用资源访问器FileCopyUtils的copy方法拷贝附件
            FileCopyUtils.copy(inputStream, outputStream);
            return pluploadModel;
        } finally {
            //无论成功与否，都有关闭输入输出流
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
