package com.wistron.springboot.springbootlearn.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.nacos.common.utils.StringUtils;
import com.wistron.springboot.springbootlearn.domain.dto.content.ShareDTO;
import com.wistron.springboot.springbootlearn.domain.dto.user.UserDTO;
import com.wistron.springboot.springbootlearn.feignclient.BaiduFeignClient;
import com.wistron.springboot.springbootlearn.feignclient.UserCenterFeignClientTest;
import com.wistron.springboot.springbootlearn.service.content.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @Author K21064736
 * @Description TODO
 * @Date 08:23 2021/8/24
 */

@RestController
@RequestMapping("/shares")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareController {

    private final ShareService shareService;
    private final DiscoveryClient discoveryClient;

    @GetMapping("/{id}")
    @SentinelResource("hot")
    public ShareDTO findById(@PathVariable Integer id) {
        return this.shareService.findById(id);
    }

    @GetMapping("test-get")
    public List<ServiceInstance> getInstances() {
        return this.discoveryClient.getInstances("user-center");
    }

    private final UserCenterFeignClientTest userCenterFeignClientTest;

    @GetMapping("test")
    public UserDTO query(UserDTO userDTO) {
        return userCenterFeignClientTest.query(userDTO);
    }

    private final BaiduFeignClient baiduFeignClient;

    @GetMapping("baidu")
    public String baiduIndex() {
        return baiduFeignClient.index();
    }

    @GetMapping("/test-sentinel-api")
    public String testSentinelAPI(@RequestParam(required = false) String a) throws IllegalAccessException {
        // ????????????sentinel??????????????? ????????? test-sentinel-api
        String resourceName = "test-sentinel-api";
        ContextUtil.enter(resourceName, "test-service");
        Entry entity = null;
        try {

            entity = SphU.entry(resourceName);
            // ????????????????????????
            if (StringUtils.isBlank(a)) {
                throw new IllegalAccessException("a??????????????????");
            }
            return a;
        } catch (BlockException e) {
            log.warn("?????????????????????");
            return "?????????????????????";
        } catch (IllegalAccessException exception) {
            Tracer.trace(exception);
            return "????????????";
        } finally {
            if (entity != null) {
                entity.exit();
            }
            ContextUtil.exit();
        }
    }

    //??????????????????????????? ????????????rocket ??????kafka
    private final StreamBridge streamBridge;

    @GetMapping("test-stream")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void testStream() {
        String message = UUID.randomUUID().toString();
        //???????????????????????? streamBridge.send ??????????????? ??????????????????exchange??????topic ??????????????????
        //??????????????????????????????
        //??????:    <?????????> + -in- + <index>
        //??????:    <?????????> + -out- + <index>
        //????????????????????????????????????send?????? ?????????consumer<String>??????  ?????????8802???controller
        //consumer????????????????????????message?????????
        streamBridge.send("send-out-0", message);
        System.out.println("************?????????message???"+message);
    }

    //    @GetMapping("/share")
//    public List<Share> testInsert() {
//        Share share = Share.builder()
//                .userId(123)
//                .title("2")
//                .author("zyb")
//                .buyCount(12)
//                .auditStatus("")
//                .cover("")
//                .createTime(new Date())
//                .downloadUrl("")
//                .isOriginal(true)
//                .price(50)
//                .reason("")
//                .updateTime(new Date())
//                .summary("")
//                .showFlag(true)
//                .build();
//
//        shareMapper.insert(share);
//
//        List<Share> shares = shareMapper.selectAll();
//
//        return shares;
//    }
}
