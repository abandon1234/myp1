package com.nageoffer.onecoupon.merchant.admin.job;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nageoffer.onecoupon.framework.result.Result;
import com.nageoffer.onecoupon.framework.web.Results;
import com.nageoffer.onecoupon.merchant.admin.common.enums.CouponTaskStatusEnum;
import com.nageoffer.onecoupon.merchant.admin.dao.entity.CouponTaskDO;
import com.nageoffer.onecoupon.merchant.admin.dao.mapper.CouponTaskMapper;
import com.nageoffer.onecoupon.merchant.admin.mq.event.CouponTaskExecuteEvent;
import com.nageoffer.onecoupon.merchant.admin.mq.producer.CouponTaskActualExecuteProducer;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 优惠券推送任务扫描定时发送记录 XXL-Job 处理器
 */
@Component
@RequiredArgsConstructor
@RestController // 为了保障快速启动，可通过 Swagger 方式访问接口，可以减少一个中间件 XXL-Job
@Tag(name = "优惠券定时推送任务") // 为了保障快速启动，可通过 Swagger 方式访问接口，可以减少一个中间件 XXL-Job
public class CouponTaskJobHandler extends IJobHandler {

    private final CouponTaskMapper couponTaskMapper;
    private final CouponTaskActualExecuteProducer couponTaskActualExecuteProducer;

    private static final int MAX_LIMIT = 100;

    @SneakyThrows
    @Operation(summary = "执行优惠券定时推送") // 为了保障快速启动，可通过 Swagger 方式访问接口，可以减少一个中间件 XXL-Job
    @GetMapping("/api/merchant-admin/other/coupon-task/job") // 为了保障快速启动，可通过 Swagger 方式访问接口，可以减少一个中间件 XXL-Job
    public Result<Void> webExecute() {
        execute();
        return Results.success();
    }

    @XxlJob(value = "couponTemplateTask")
    public void execute() throws Exception {
        long initId = 0;
        Date now = new Date();

        while (true) {
            // 获取已到执行时间待执行的优惠券定时分发任务
            List<CouponTaskDO> couponTaskDOList = fetchPendingTasks(initId, now);

            if (CollUtil.isEmpty(couponTaskDOList)) {
                break;
            }

            // 调用分发服务对用户发送优惠券
            for (CouponTaskDO each : couponTaskDOList) {
                distributeCoupon(each);
            }

            // 查询出来的数据如果小于 MAX_LIMIT 意味着后面将不再有数据，返回即可
            if (couponTaskDOList.size() < MAX_LIMIT) {
                break;
            }

            // 更新 initId 为当前列表中最大 ID
            initId = couponTaskDOList.stream()
                    .mapToLong(CouponTaskDO::getId)
                    .max()
                    .orElse(initId);
        }
    }

    private void distributeCoupon(CouponTaskDO couponTask) {
        // 修改延时执行推送任务任务状态为执行中
        CouponTaskDO couponTaskDO = CouponTaskDO.builder()
                .id(couponTask.getId())
                .status(CouponTaskStatusEnum.IN_PROGRESS.getStatus())
                .build();
        couponTaskMapper.updateById(couponTaskDO);
        // 通过消息队列发送消息，由分发服务消费者消费该消息
        CouponTaskExecuteEvent couponTaskExecuteEvent = CouponTaskExecuteEvent.builder()
                .couponTaskId(couponTask.getId())
                .build();
        couponTaskActualExecuteProducer.sendMessage(couponTaskExecuteEvent);
    }

    private List<CouponTaskDO> fetchPendingTasks(long initId, Date now) {
        LambdaQueryWrapper<CouponTaskDO> queryWrapper = Wrappers.lambdaQuery(CouponTaskDO.class)
                .eq(CouponTaskDO::getStatus, CouponTaskStatusEnum.PENDING.getStatus())// 待执行
                .le(CouponTaskDO::getSendTime, now)// 小于等于当前时间
                .gt(CouponTaskDO::getId, initId)// 大于 initId
                .last("LIMIT " + MAX_LIMIT);// 限制最大查询数量
        return couponTaskMapper.selectList(queryWrapper);
    }
}
