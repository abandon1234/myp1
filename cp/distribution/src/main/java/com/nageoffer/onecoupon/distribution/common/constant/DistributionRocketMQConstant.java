package com.nageoffer.onecoupon.distribution.common.constant;

/**
 * 分发优惠券服务 RocketMQ 常量类
 */
public final class DistributionRocketMQConstant {

    /**
     * 优惠券模板推送执行 Topic Key
     * 负责扫描优惠券 Excel 并将里面的记录进行推送
     */
    public static final String TEMPLATE_TASK_EXECUTE_TOPIC_KEY = "one-coupon_distribution-service_coupon-task-execute_topic${unique-name:}";

    /**
     * 优惠券模板推送执行-执行消费者组 Key
     */
    public static final String TEMPLATE_TASK_EXECUTE_CG_KEY = "one-coupon_distribution-service_coupon-task-execute_cg${unique-name:}";

    /**
     * 优惠券模板推送执行 Topic Key
     * 负责执行将优惠券发放给具体用户逻辑
     */
    public static final String TEMPLATE_EXECUTE_DISTRIBUTION_TOPIC_KEY = "one-coupon_distribution-service_coupon-execute-distribution_topic${unique-name:}";

    /**
     * 优惠券模板推送执行-执行消费者组 Key
     */
    public static final String TEMPLATE_EXECUTE_DISTRIBUTION_CG_KEY = "one-coupon_distribution-service_coupon-execute-distribution_cg${unique-name:}";

    /**
     * 优惠券模板推送用户通知-执行消费者组 Key
     */
    public static final String TEMPLATE_EXECUTE_SEND_MESSAGE_CG_KEY = "one-coupon_distribution-service_coupon-execute-send-message_cg${unique-name:}";
}
