package com.atguigu.guli.service.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.atguigu.guli.service.oss.service.FileService;
import com.atguigu.guli.service.oss.util.OssProperties;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private OssProperties ossProperties;

    @Override
    public String upload(InputStream inputStream, String module, String originalFilename) {

        //读取文件配置信息
        String endPoint = ossProperties.getEndPoint();
        String keyid = ossProperties.getKeyid();
        String keysecret = ossProperties.getKeysecret();
        String bucketname = ossProperties.getBucketname();

        //创建ossClient实例
        OSS ossClient = new OSSClientBuilder().build(endPoint, keyid, keysecret);
        //判断bucket是否存在
        boolean exist = ossClient.doesBucketExist(bucketname);
        if(!exist){
            // 创建存储空间。
            ossClient.createBucket(bucketname);
            // 设置bucket权限为公共读
            ossClient.setBucketAcl(bucketname, CannedAccessControlList.PublicRead);
        }
        //使用日期策略
        //构架ObjectName ： 文件路径(avatar/2022/11/9/default.jpg)
        String floder = new DateTime().toString("yyyy/MM/dd");
        //因为担心文件会进行重名，所以在这里使用UUID防止重名
        String fileName = UUID.randomUUID().toString();
        //获取文件的扩展名
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String key = module+"/"+floder+"/"+fileName+fileExtension;
        //上传文件流
        ossClient.putObject(bucketname,key,inputStream);

        //关闭OSSClient
        ossClient.shutdown();

        //返回url  https://guli-file-tjs.oss-cn-hangzhou.aliyuncs.com/avatar/default.jpg
        String url = "https://"+bucketname+"."+endPoint+"/"+key;


        return url;
    }



    //因为又删除数据，所以oss里面的数据也会被删除掉
    //因此 edu —> <-oss ->  <-edu 这种情况在微服务中后期对分库分表会产生影响
    //所以，我们就可以 edu -> <- edu -> <- oss
    //也就是说先使用edu微服务调用oss中的删除文件方法，然后oss微服务 会返回给edu一个结果
    //edu微服务(一般实现在controller)再去使用openfeign跨微服务oss删除头像的edu微服务中的方法(一般实现在service)
    //根据具体业务进行思考
    @Override
    public void removeFile(String url) {
        //读取文件配置信息
        String endPoint = ossProperties.getEndPoint();
        String keyid = ossProperties.getKeyid();
        String keysecret = ossProperties.getKeysecret();
        String bucketname = ossProperties.getBucketname();

        //创建ossClient实例
        OSS ossClient = new OSSClientBuilder().build(endPoint, keyid, keysecret);

        //传过来的:https://guli-file-tjs.oss-cn-hangzhou.aliyuncs.com/avatar/2022/11/10/1a936cd6-5bab-4c75-b155-9921e0a63cdf.jpg
        //实际使用的:avatar/2022/11/10/1a936cd6-5bab-4c75-b155-9921e0a63cdf.jpg
        // 删除文件或目录。如果要删除目录，目录必须为空。
        String host= "https://"+bucketname+"."+endPoint+"/";
        String objectName = url.substring(host.length());//左开右闭
        ossClient.deleteObject(bucketname, objectName);

        //关闭OSSClient
    }
}
