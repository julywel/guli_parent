package com.atguigu.guli.service.cms.feign.sentinel;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.cms.feign.OssFileFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OssFileFeignSentinel implements OssFileFeign {

    @Override
    public R test() {
        return R.error();
    }

    @Override
    public R removeFile(String url) {
        log.info("熔断保护");
        return R.error();
    }
}
