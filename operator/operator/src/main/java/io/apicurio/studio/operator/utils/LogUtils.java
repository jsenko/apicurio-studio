package io.apicurio.studio.operator.utils;

import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;

public class LogUtils {

    public static String contextPrefix(ApicurioStudio primary) {
        return "[%s:%s] ".formatted(primary.getMetadata().getNamespace(), primary.getMetadata().getName());
    }
}
