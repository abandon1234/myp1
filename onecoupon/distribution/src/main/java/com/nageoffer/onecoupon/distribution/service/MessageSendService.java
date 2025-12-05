package com.nageoffer.onecoupon.distribution.service;

import com.nageoffer.onecoupon.distribution.dto.req.MessageSendReqDTO;
import com.nageoffer.onecoupon.distribution.dto.resp.MessageSendRespDTO;

/**
 * 消息发送接口
 * 正常来说这应该有个独立消息服务，因为消息通知不在牛券系统核心范畴，所以仅展示流程
 */
public interface MessageSendService {

    /**
     * 消息发送接口
     *
     * @param requestParam 消息发送请求参数
     * @return 消息发送结果
     */
    MessageSendRespDTO sendMessage(MessageSendReqDTO requestParam);
}
