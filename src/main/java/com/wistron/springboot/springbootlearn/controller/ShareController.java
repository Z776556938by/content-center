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
        // 定义一个sentinel保护的资源 名称时 test-sentinel-api
        String resourceName = "test-sentinel-api";
        ContextUtil.enter(resourceName, "test-service");
        Entry entity = null;
        try {

            entity = SphU.entry(resourceName);
            // 被保护的业务逻辑
            if (StringUtils.isBlank(a)) {
                throw new IllegalAccessException("a参数不可为空");
            }
            return a;
        } catch (BlockException e) {
            log.warn("限流或降级了！");
            return "限流或降级了！";
        } catch (IllegalAccessException exception) {
            Tracer.trace(exception);
            return "参数非法";
        } finally {
            if (entity != null) {
                entity.exit();
            }
            ContextUtil.exit();
        }
    }

    //这里直接装配一个桥 用来连接rocket 或者kafka
    private final StreamBridge streamBridge;

    @GetMapping("test-stream")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void testStream() {
        String message = UUID.randomUUID().toString();
        //这里说明一下这个 streamBridge.send 方法的参数 第一个参数是exchange或者topic 就是主题名称
        //默认的主题名称是通过
        //输入:    <方法名> + -in- + <index>
        //输出:    <方法名> + -out- + <index>
        //这里我们接收的时候就要用send方法 参数是consumer<String>接收  详情看8802的controller
        //consumer的参数类型是这里message的类型
        streamBridge.send("send-out-0", message);
        System.out.println("************发送了message："+message);
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
