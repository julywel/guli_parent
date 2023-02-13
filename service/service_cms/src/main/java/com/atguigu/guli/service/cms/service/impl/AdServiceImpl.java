package com.atguigu.guli.service.cms.service.impl;

import com.alibaba.nacos.client.utils.StringUtils;
import com.atguigu.guli.service.cms.entity.Ad;
import com.atguigu.guli.service.cms.entity.vo.AdVo;
import com.atguigu.guli.service.cms.feign.OssFileFeign;
import com.atguigu.guli.service.cms.mapper.AdMapper;
import com.atguigu.guli.service.cms.service.AdService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.atguigu.guli.common.base.result.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 广告推荐 服务实现类
 * </p>
 *
 * @author TJS
 * @since 2022-11-19
 */
@Service
public class AdServiceImpl extends ServiceImpl<AdMapper, Ad> implements AdService {
    @Autowired
    private OssFileFeign ossFileFeign;

    @Override
    public IPage<AdVo> selectPage(Long page, Long limit) {
        QueryWrapper<AdVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("a.type_id","a.sort");

        Page<AdVo> pageParam = new Page<>(page,limit);

        List<AdVo> records = baseMapper.selectPageByQueryWrapper(pageParam,queryWrapper);
        pageParam.setRecords(records);
        return pageParam;
    }

    @Override
    public boolean removeAdImageById(String id) {
        Ad ad = baseMapper.selectById(id);
        if(ad != null) {
            String imagesUrl = ad.getImageUrl();
            if(!StringUtils.isEmpty(imagesUrl)){
                //删除图片
                R r = ossFileFeign.removeFile(imagesUrl);
                return r.getSuccess();
            }
        }
        return false;
    }

    /**
     * 缓存
     * @param adTypeId
     * @return
     */
    @Cacheable(value = "index", key = "'selectByAdTypeId'")
    @Override
    public List<Ad> selectByAdTypeId(String adTypeId) {
        LambdaQueryWrapper<Ad> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Ad::getSort,Ad::getId);
        queryWrapper.eq(Ad::getTypeId,adTypeId);

        List<Ad> adList = baseMapper.selectList(queryWrapper);

        return adList;
    }
}
