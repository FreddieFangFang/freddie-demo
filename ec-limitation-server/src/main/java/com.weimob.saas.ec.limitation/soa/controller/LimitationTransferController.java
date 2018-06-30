package com.weimob.saas.ec.limitation.soa.controller;

import com.weimob.saas.ec.common.response.HttpResponse;
import com.weimob.saas.ec.limitation.model.request.LimitationTransferRequestVo;
import com.weimob.saas.ec.limitation.model.response.LimitationTransferResponseVo;
import com.weimob.saas.ec.limitation.thread.LimitationTransferThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lujialin
 * @description 限购数据迁移
 * @date 2018/6/29 17:22
 */
@Controller
@RequestMapping("/ec/mgr/limitation")
public class LimitationTransferController {

    @Autowired
    private ThreadPoolTaskExecutor threadExecutor;

    @RequestMapping(value = "/limitationTransfer")
    @ResponseBody
    public HttpResponse<LimitationTransferResponseVo> limitationTransfer(@RequestBody LimitationTransferRequestVo requestVo) {
        HttpResponse<LimitationTransferResponseVo> httpResponse = new HttpResponse<>();
        LimitationTransferResponseVo responseVo = new LimitationTransferResponseVo();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        responseVo.setStartTime(simpleDateFormat.format(new Date()));
        for (int i = 0; i < 8; i++) {
            threadExecutor.execute(new LimitationTransferThread(i));
        }
        responseVo.setEndTime(simpleDateFormat.format(new Date()));
        responseVo.setStatus(true);
        httpResponse.setData(responseVo);
        return httpResponse;
    }
}
