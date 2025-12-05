package com.nageoffer.onecoupon.engine.dao.sharding;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.apache.shardingsphere.infra.util.exception.ShardingSpherePreconditions;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.apache.shardingsphere.sharding.exception.algorithm.sharding.ShardingAlgorithmInitializationException;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * 基于 HashMod 方式自定义分库算法
 */
public final class DBHashModShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    @Getter
    private Properties props;

    private int shardingCount;
    private static final String SHARDING_COUNT_KEY = "sharding-count";

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        long id = shardingValue.getValue();
        int dbSize = availableTargetNames.size();
        int mod = (int) hashShardingValue(id) % shardingCount / (shardingCount / dbSize);
        int index = 0;
        for (String targetName : availableTargetNames) {
            if (index == mod) {
                return targetName;
            }
            index++;
        }
        throw new IllegalArgumentException("No target found for value: " + id);
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> shardingValue) {
        // 暂无范围分片场景，默认返回空
        return List.of();
    }

    @Override
    public void init(Properties props) {// 初始化分片算法配置、读取 sharding-count 配置、将实例注册到单例容器中
        this.props = props;
        shardingCount = getShardingCount(props);
        Object businessTag = props.get("business-tag");
        if (Objects.nonNull(businessTag) && StrUtil.isNotBlank(businessTag.toString())) {
            Singleton.put(businessTag.toString(), this);
        }
    }

    private int getShardingCount(final Properties props) {//从配置属性中获取分片总数
        ShardingSpherePreconditions.checkState(props.containsKey(SHARDING_COUNT_KEY), () -> new ShardingAlgorithmInitializationException(getType(), "Sharding count cannot be null."));
        return Integer.parseInt(props.getProperty(SHARDING_COUNT_KEY));//返回值: int 类型的分片总数
    }

    public int getShardingMod(long id, int availableTargetSize) {//计算给定ID应该分配到哪个分片索引  即哪个库上 //id: 分片键值（如商家编号）
        return (int) hashShardingValue(id) % shardingCount / (shardingCount / availableTargetSize);
    }

    private long hashShardingValue(final Comparable<?> shardingValue) {
        return Math.abs((long) shardingValue.hashCode());
    }
}