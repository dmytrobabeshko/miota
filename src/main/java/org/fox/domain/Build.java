package org.fox.domain;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

public class Build implements Comparable<Build> {

    @NotNull
    private final Integer build;

    @NotNull
    private final String error;


    public Build(@NotNull Integer build) {
        this.build = build;
        this.error = "";
    }

    public Build(@NotNull Integer build, @NotNull String error) {
        this.build = build;
        this.error = error;
    }


    public Integer getBuild() {
        return build;
    }

    public String getError() {
        return error;
    }

    public boolean hasError() {
        return !StringUtils.isEmpty(error);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Build that = (Build) o;
        return build.equals(that.build) &&
                error.equals(that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(build, error);
    }


    @Override
    public int compareTo(@NotNull Build o) {
        return build.compareTo(o.build);
    }

    @Override
    public String toString() {
        return build + (StringUtils.isEmpty(error) ? "" : "(" + error + ")");
    }
}
