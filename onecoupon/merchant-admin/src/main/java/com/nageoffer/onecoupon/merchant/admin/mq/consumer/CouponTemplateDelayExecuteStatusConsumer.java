package com.nageoffer.onecoupon.merchant.admin.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nageoffer.onecoupon.merchant.admin.common.constant.MerchantAdminRocketMQConstant;
import com.nageoffer.onecoupon.merchant.admin.common.enums.CouponTemplateStatusEnum;
import com.nageoffer.onecoupon.merchant.admin.dao.entity.CouponTemplateDO;
import com.nageoffer.onecoupon.merchant.admin.mq.base.MessageWrapper;
import com.nageoffer.onecoupon.merchant.admin.mq.event.CouponTemplateDelayEvent;
import com.nageoffer.onecoupon.merchant.admin.service.CouponTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 优惠券模板推送延迟执行-变更记录状态消费者
 */
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MerchantAdminRocketMQConstant.TEMPLATE_DELAY_TOPIC_KEY,
        consumerGroup = MerchantAdminRocketMQConstant.TEMPLATE_DELAY_STATUS_CG_KEY
)
@Slf4j(topic = "CouponTemplateDelayExecuteStatusConsumer")
public class CouponTemplateDelayExecuteStatusConsumer implements RocketMQListener<MessageWrapper<CouponTemplateDelayEvent>> {

    private final CouponTemplateService couponTemplateService;

    @Override
    public void onMessage(MessageWrapper<CouponTemplateDelayEvent> messageWrapper) {
        // 开头打印日志，平常可 Debug 看任务参数，线上可报平安（比如消息是否消费，重新投递时获取参数等）
        log.info("[消费者] 优惠券模板定时执行@变更模板表状态 - 执行消费逻辑，消息体：{}", JSON.toJSONString(messageWrapper));

        // 修改指定优惠券模板状态为已结束
        CouponTemplateDelayEvent message = messageWrapper.getMessage();
        LambdaUpdateWrapper<CouponTemplateDO> updateWrapper = Wrappers.lambdaUpdate(CouponTemplateDO.class)
                .eq(CouponTemplateDO::getShopNumber, message.getShopNumber())
                .eq(CouponTemplateDO::getId, message.getCouponTemplateId());
        CouponTemplateDO couponTemplateDO = CouponTemplateDO.builder()
                .status(CouponTemplateStatusEnum.ENDED.getStatus())
                .build();

        // 底层实现自动填充时，需要获取到实例对象对应的表结构信息，使用update(wrapper<T> xxx)方法时表结构信息为空，所以直接跳过了填充逻辑
        couponTemplateService.update(couponTemplateDO, updateWrapper);
    }
}
