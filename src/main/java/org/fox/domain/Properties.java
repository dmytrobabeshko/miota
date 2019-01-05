package org.fox.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Component
@PropertySource("file:miota.properties")
public class Properties {

    private String device;
    private String platformId;
    private String androidVer;
    private Integer currentBuild;
    private Integer buildDelta;

    private String updateUriTemplate;
    private Integer workers;
    private Integer connectionErrorRetry;
    private Integer httpNotFoundRetry;
    private Integer connectionErrorRetryDelay;
    private Integer httpNotFoundRetryDelay;

    private Integer screenRefreshInterval;


    public String getDevice() {
        return device;
    }

    @Value("${miota.device:}")
    private void setDevice(String device) {
        Assert.isTrue(!StringUtils.isEmpty(device), "device is not specified");
        this.device = device;
    }

    public String getPlatformId() {
        return platformId;
    }

    @Value("${miota.platformId:}")
    private void setPlatformId(String platformId) {
        Assert.isTrue(!StringUtils.isEmpty(platformId), "paltformId is not specified");
        this.platformId = platformId;
    }

    public String getAndroidVer() {
        return androidVer;
    }

    @Value("${miota.androidVer:}")
    private void setAndroidVer(String androidVer) {
        Assert.isTrue(!StringUtils.isEmpty(androidVer), "androidVer is not specified");
        this.androidVer = androidVer;
    }

    public Integer getCurrentBuild() {
        return currentBuild;
    }

    @Value("${miota.currentBuild:}")
    private void setCurrentBuild(String currentBuild) {
        Assert.isTrue(!StringUtils.isEmpty(currentBuild), "currentBuild is not specified");
        int value = Integer.parseInt(currentBuild);
        Assert.isTrue(value > 0, "can't be negative");

        this.currentBuild = value;
    }

    public Integer getBuildDelta() {
        return buildDelta;
    }

    @Value("${miota.buildDelta:1000}")
    private void setBuildDelta(String buildDelta) {
        this.buildDelta = parseIntWithRange(buildDelta, 10, 5000);
    }

    public String getUpdateUriTemplate() {
        return updateUriTemplate;
    }

    @Value("${miota.updateUriTemplate}")
    private void setUpdateUriTemplate(String updateUriTemplate) {
        Assert.isTrue(!StringUtils.isEmpty(updateUriTemplate), "updateUriTemplate is not defined");
        this.updateUriTemplate = updateUriTemplate;
    }

    public Integer getWorkers() {
        return workers;
    }

    @Value("${miota.workers:200}")
    private void setWorkers(String workers) {
        this.workers = parseIntWithRange(workers, 10, 1000);
    }

    public Integer getConnectionErrorRetry() {
        return connectionErrorRetry;
    }

    @Value("${miota.connectionErrorRetry:10}")
    private void setConnectionErrorRetry(String connectionErrorRetry) {
        this.connectionErrorRetry = parseIntWithRange(connectionErrorRetry, 1, 50);
    }

    public Integer getHttpNotFoundRetry() {
        return httpNotFoundRetry;
    }

    @Value("${miota.httpNotFoundRetry:2}")
    public void setHttpNotFoundRetry(String httpNotFoundRetry) {
        this.httpNotFoundRetry = parseIntWithRange(httpNotFoundRetry, 1, 50);
    }

    public Integer getConnectionErrorRetryDelay() {
        return connectionErrorRetryDelay;
    }

    @Value("${miota.connectionErrorRetryDelay:100}")
    private void setConnectionErrorRetryDelay(String connectionErrorRetryDelay) {
        this.connectionErrorRetryDelay = parseIntWithRange(connectionErrorRetryDelay, 0, 5000);
    }

    public Integer getHttpNotFoundRetryDelay() {
        return httpNotFoundRetryDelay;
    }

    @Value("${miota.httpNotFoundRetryDelay:100}")
    private void setHttpNotFoundRetryDelay(String httpNotFoundRetryDelay) {
        this.httpNotFoundRetryDelay = parseIntWithRange(httpNotFoundRetryDelay, 0, 5000);
    }

    public Integer getScreenRefreshInterval() {
        return screenRefreshInterval;
    }

    @Value("${miota.screenRefreshInterval:1000}")
    public void setScreenRefreshInterval(String screenRefreshInterval) {
        this.screenRefreshInterval = parseIntWithRange(screenRefreshInterval, 500, 5000);
    }

    private Integer parseIntWithRange(String string, int min, int max) {
        int value = Integer.parseInt(string);

        if (value > max) {
            value = 1000;
        }
        if (min < 10) {
            value = 10;
        }

        return value;
    }
}
