package com.nageoffer.onecoupon.distribution.service.impl;

import com.nageoffer.onecoupon.distribution.common.enums.SendMessageMarkCovertEnum;
import com.nageoffer.onecoupon.distribution.dto.req.MessageSendReqDTO;
import com.nageoffer.onecoupon.distribution.dto.resp.MessageSendRespDTO;
import com.nageoffer.onecoupon.distribution.service.MessageSendService;
import com.nageoffer.onecoupon.distribution.service.basics.DistributionExecuteStrategy;
import org.springframework.stereotype.Service;

/**
 * 微信消息发送接口实现类
 * 正常来说这应该有个独立消息服务，因为消息通知不在牛券系统核心范畴，所以仅展示流程
 */
@Service
public class WeChatMessageSendServiceImpl implements MessageSendService, DistributionExecuteStrategy<MessageSendReqDTO, MessageSendRespDTO> {

    @Override
    public MessageSendRespDTO sendMessage(MessageSendReqDTO requestParam) {
        return null;
    }

    @Override
    public String mark() {
        return SendMessageMarkCovertEnum.WECHAT.name();
    }

    @Override
    public MessageSendRespDTO executeResp(MessageSendReqDTO requestParam) {
        return sendMessage(requestParam);
    }
}
//步骤概述：https://www.doubao.com/thread/wa338c88837fb8fa1
//
//1.注册微信公众号并获取必要的配置信息：AppID、AppSecret等。
//
//2.获取访问令牌（access_token），这是调用微信接口所必需的。
//
//3.获取用户OpenID，这是调用微信接口所必需的。//引导用户访问授权链接，通过返回的 code 换取 OpenID
//
//4.设置消息模板（在微信公众平台上设置），并获取模板ID。
//
//5.编写Spring Boot代码，调用微信的模板消息接口发送消息。
//
//注意：模板消息功能需要用户关注公众号，并且模板需要审核通过。