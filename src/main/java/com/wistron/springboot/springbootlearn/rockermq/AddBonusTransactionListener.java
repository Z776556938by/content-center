package com.wistron.springboot.springbootlearn.rockermq;

import com.wistron.springboot.springbootlearn.dao.content.RocketmqTransactionLogMapper;
import com.wistron.springboot.springbootlearn.domain.dto.content.ShareAuditDTO;
import com.wistron.springboot.springbootlearn.domain.entity.content.RocketmqTransactionLog;
import com.wistron.springboot.springbootlearn.service.content.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

@RocketMQTransactionListener(txProducerGroup = "tx-add-bonus-group")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AddBonusTransactionListener implements RocketMQLocalTransactionListener {

    private final ShareService shareService;
    private final RocketmqTransactionLogMapper transactionLogMapper;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        MessageHeaders headers = message.getHeaders();

        // Object 转为 Integer 需要(String) 再用Integer.valueOf
        Integer shareId = Integer.valueOf((String) headers.get("share_id")) ;
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        try {
            // 本地事务运行成功 向Rocket提交成功
            this.shareService.auditByIdWithRocketMqLog(shareId, (ShareAuditDTO) arg, transactionId);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * RocketMQ 通过数据库表
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        MessageHeaders headers = message.getHeaders();
        String transactionId = (String) headers.get("RocketMQLocalTransactionState.ROLLBACK");

        //  select * from xxx where transaction_id = xxx
        RocketmqTransactionLog rocketmqTransactionLog = this.transactionLogMapper.selectOne(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
                        .build()
        );
        if (rocketmqTransactionLog != null) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
