package org.fox.domain;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


@Component
@PropertySource("classpath:version.properties")
public class MiotaVersionProvider {

    private String version;


    @NotNull
    public String getVersion() {
        return version;
    }

    @Value("${miota.version:}")
    public void setVersion(@NotNull String version) {
        version = StringUtils.deleteAny(version, "'");
        Assert.isTrue(!StringUtils.isEmpty(version), "miota.version is not defined");
        this.version = version;
    }
}
