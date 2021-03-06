package com.codingmaple.cache.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "generic-cache")
public class GenericCacheConfig {

    // 默认过期时间
    private Long defaultExpiryTime = 3600L;
    // 默认缓存前缀
    private String defaultCacheNamePrefix = "cached-";

    private String serializationType = "protostuff";

    private Boolean isSyncCache = true;

    private Integer maximumSize = 200;
    private Integer initialCapacity = 100;

    private List<String> cancelSyncList = new ArrayList<>();

    public List<String> getCancelSyncList() {
        return cancelSyncList;
    }

    public void setCancelSyncList(List<String> cancelSyncList) {
        this.cancelSyncList = cancelSyncList;
    }

    public Boolean getSyncCache() {
        return isSyncCache;
    }

    public void setSyncCache(Boolean syncCache) {
        isSyncCache = syncCache;
    }

    public Long getDefaultExpiryTime() {
        return defaultExpiryTime;
    }

    public void setDefaultExpiryTime(Long defaultExpiryTime) {
        this.defaultExpiryTime = defaultExpiryTime;
    }

    public String getDefaultCacheNamePrefix() {
        return defaultCacheNamePrefix;
    }

    public void setDefaultCacheNamePrefix(String defaultCacheNamePrefix) {
        this.defaultCacheNamePrefix = defaultCacheNamePrefix;
    }

    public String getSerializationType() {
        return serializationType;
    }

    public void setSerializationType(String serializationType) {
        this.serializationType = serializationType;
    }

    public Integer getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(Integer maximumSize) {
        this.maximumSize = maximumSize;
    }

    public Integer getInitialCapacity() {
        return initialCapacity;
    }

    public void setInitialCapacity(Integer initialCapacity) {
        this.initialCapacity = initialCapacity;
    }
}
