package com.atguigu.guli.service.oss.service;


import java.io.InputStream;

public interface FileService{
    /**
     * 阿里oss文件上传
     * @param inputStream 输入流
     * @param module 文件夹名称
     * @param originalFilename 原始文件名字
     * @return  文件在oss服务器上的url地址上
     */
    //上传  因为获取图片应该是以流的形式进行读取，然后存放进那个oss中的那个位置，以及名称
    String upload(InputStream inputStream,String module,String originalFilename);

    /**
     * 阿里云oss 文件删除
     * @param url 文件的url的地址
     */
    void removeFile(String url);
}
