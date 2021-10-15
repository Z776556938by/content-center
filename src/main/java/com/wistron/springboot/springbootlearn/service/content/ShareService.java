package com.wistron.springboot.springbootlearn.service.content;

import com.wistron.springboot.springbootlearn.dao.content.RocketmqTransactionLogMapper;
import com.wistron.springboot.springbootlearn.dao.content.ShareMapper;
import com.wistron.springboot.springbootlearn.domain.dto.content.ShareAuditDTO;
import com.wistron.springboot.springbootlearn.domain.dto.content.ShareDTO;
import com.wistron.springboot.springbootlearn.domain.dto.message.UserAddBonusMsgDTO;
import com.wistron.springboot.springbootlearn.domain.dto.user.UserDTO;
import com.wistron.springboot.springbootlearn.domain.entity.content.RocketmqTransactionLog;
import com.wistron.springboot.springbootlearn.domain.entity.content.Share;
import com.wistron.springboot.springbootlearn.domain.enums.AuditStatusEnum;
import com.wistron.springboot.springbootlearn.feignclient.UserCenterFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * @Author K21064736
 * @Description TODO
 * @Date 10:45 2021/8/24
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ShareService {
    private final ShareMapper shareMapper;
    private final UserCenterFeignClient userCenterFeignClient;
    //    private final RestTemplate restTemplate;   //引入 feign 进行解决Http 微服务请求
//    private final DiscoveryClient discoveryClient;  //引入Ribbon
    private final RocketMQTemplate rocketMQTemplate;
    private final RocketmqTransactionLogMapper transactionLogMapper;

    public ShareDTO findById(Integer id) {
        //获取分享的内容
        Share share = this.shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();
//        使用discoveryClient 进行服务发现 获取URL地址
//        List<ServiceInstance> instances = this.discoveryClient.getInstances("user-center");
//        String targetUrl = instances.stream()
//                .map(instance -> instance.getUri().toString() + "/users/{id}")
//                .findFirst()
//                .orElseThrow(()-> new IllegalArgumentException("当前没有实例"));

//        int ints = ThreadLocalRandom.current().nextInt(instances.size());  //获取一个随机数 ThreadLocalRandom线程安全

        /*使用Ribbon会自动将user-center替换 user-center在nacons中的实例 并负载均衡后的URL*/
//        UserDTO userDTO = this.restTemplate.getForObject(
//                "http://user-center/users/{id}",
//                UserDTO.class,
//                userId
//        );

        /*使用Feign 替代resttemplate */
        UserDTO userDTO = this.userCenterFeignClient.findById(userId);

        ShareDTO shareDTO = ShareDTO.builder().build();
        //进行前端所需信息装配
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }

    public Share auditById(Integer id, ShareAuditDTO shareAuditDTO) {
        // 检查share是否存在 不存在或当前audit_status != NOT_YET 抛出异常
        Share share = shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("参数非法！该分享不存在");
        }
        if (!Objects.equals("NOT_YET", share.getAuditStatus())) {
            throw new IllegalArgumentException("参数非法！该分享已通过审核或审核不通过");
        }

        // 3. 如果PASS 发消息给rocketmq,让用户中心去消费 为发布人添加积分 (写入数据如果比较 耗时 可以进行采用异步进行)
        if (AuditStatusEnum.PASS.equals(shareAuditDTO.getAuditStatusEnum())) {
            // 发送半消息  group  topic  message
            String transactionId = UUID.randomUUID().toString();
            this.rocketMQTemplate.sendMessageInTransaction(
                    "tx-add-bonus-group",
                    "add-bonus",
                    MessageBuilder.withPayload(
                                    UserAddBonusMsgDTO
                                            .builder()
                                            .userId(share.getUserId())
                                            .bonus(50)
                                            .build()
                            )
                            //  Header有妙用 实现RocketMQLocalTransactionListener后通过这获取对应的RocketMQ事务状态与获取变量
                            .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                            .setHeader("share_id", id)
                            .build(),
                    // arg用处 对应实现RocketMQLocalTransactionListener 中的 Object o
                    shareAuditDTO
            );
        } else {
            // 如果是 REJECT 直接更新数据库
            this.auditByIdInDB(id, shareAuditDTO);
        }
        return share;
    }

    /**
     * 审核资源 将状态设置为 PASS/REJECT 跟新share状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id, ShareAuditDTO shareAuditDTO) {
        Share share = Share.builder()
                .id(id)
                .auditStatus(shareAuditDTO.getAuditStatusEnum().toString())
                .reason(shareAuditDTO.getReason())
                .build();

        this.shareMapper.updateByPrimaryKeySelective(share);
        // 4. 把share 写入缓存
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id, ShareAuditDTO shareAuditDTO, String transactionId) {
        this.auditByIdInDB(id, shareAuditDTO);
        this.transactionLogMapper.insertSelective(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
                        .log("审核分享...")
                        .build()
        );
    }
}
