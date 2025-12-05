package com.nageoffer.onecoupon.distribution.service.impl;

import com.nageoffer.onecoupon.distribution.common.enums.SendMessageMarkCovertEnum;
import com.nageoffer.onecoupon.distribution.dto.req.MessageSendReqDTO;
import com.nageoffer.onecoupon.distribution.dto.resp.MessageSendRespDTO;
import com.nageoffer.onecoupon.distribution.service.MessageSendService;
import com.nageoffer.onecoupon.distribution.service.basics.DistributionExecuteStrategy;
import org.springframework.stereotype.Service;

/**
 * 站内信消息发送接口实现类
 * 正常来说这应该有个独立消息服务，因为消息通知不在牛券系统核心范畴，所以仅展示流程
 */
@Service
public class SiteMessageSendServiceImpl implements MessageSendService, DistributionExecuteStrategy<MessageSendReqDTO, MessageSendRespDTO> {

    @Override
    public MessageSendRespDTO sendMessage(MessageSendReqDTO requestParam) {
        return null;
    }

    @Override
    public String mark() {
        return SendMessageMarkCovertEnum.SITE.name();
    }

    @Override
    public MessageSendRespDTO executeResp(MessageSendReqDTO requestParam) {
        return sendMessage(requestParam);
    }
}
//步骤：
//
//引入WebSocket依赖。
//
//配置WebSocket处理器和拦截器。
//
//使用Redis Pub/Sub进行消息分发（适用于集群环境）。
//
//实现心跳机制，定期检查连接有效性。
//
//记录连接状态，只向在线客户端发送消息。
