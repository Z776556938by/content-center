package com.wistron.springboot.springbootlearn.domain.entity.content;

import lombok.Builder;

import javax.persistence.*;
@Builder
@Table(name = "rocketmq_transaction_log")
public class RocketmqTransactionLog {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 事务id
     */
    @Column(name = "transaction_Id")
    private String transactionId;

    /**
     * 日志
     */
    private String log;

    /**
     * 获取id
     *
     * @return id - id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取事务id
     *
     * @return transaction_Id - 事务id
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * 设置事务id
     *
     * @param transactionId 事务id
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * 获取日志
     *
     * @return log - 日志
     */
    public String getLog() {
        return log;
    }

    /**
     * 设置日志
     *
     * @param log 日志
     */
    public void setLog(String log) {
        this.log = log;
    }
}