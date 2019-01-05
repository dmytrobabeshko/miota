package org.fox.http;

import static org.springframework.util.StringUtils.trimTrailingCharacter;
import static org.springframework.util.StringUtils.trimWhitespace;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.fox.domain.SearchInfoHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;


@Component
public class UriBuilder {

    @NotNull
    public URI buildURI(@NotNull SearchInfoHolder searchInfoHolder, @NotNull Integer currentBuild, @NotNull Integer otaBuild) {
        Map<String, String> uriVariables = getUriVariables(searchInfoHolder, currentBuild, otaBuild);
        return getUriComponentsBuilder(searchInfoHolder).buildAndExpand(uriVariables).toUri();
    }

    @NotNull
    private UriComponentsBuilder getUriComponentsBuilder(@NotNull SearchInfoHolder searchInfoHolder) {
        return UriComponentsBuilder.fromUriString(searchInfoHolder.getUpdateUriTemplate());
    }

    @NotNull
    private Map<String, String> getUriVariables(@NotNull SearchInfoHolder searchInfoHolder, @NotNull Integer currentBuild, @NotNull Integer otaBuild) {
        String device = trimWhitespace(searchInfoHolder.getDevice());
        String platformId = trimWhitespace(searchInfoHolder.getPlatformId());
        String androidVer = trimTrailingCharacter(trimWhitespace(searchInfoHolder.getAndroidVer()), '.');

        return Collections.unmodifiableMap(new HashMap<String, String>() {{
            put("device", device);
            put("platformId", platformId);
            put("androidVer", androidVer);
            put("currentBuild", currentBuild.toString());
            put("otaBuild", otaBuild.toString());
        }});
    }
}
