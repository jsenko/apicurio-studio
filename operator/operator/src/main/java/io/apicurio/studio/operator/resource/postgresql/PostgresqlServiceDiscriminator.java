package io.apicurio.studio.operator.resource.postgresql;

import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;
import io.apicurio.studio.operator.resource.LabelDiscriminator;
import io.fabric8.kubernetes.api.model.Service;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;

import java.util.Map;

import static io.apicurio.studio.operator.resource.ResourceFactory.COMPONENT_POSTGRESQL;

public class PostgresqlServiceDiscriminator extends LabelDiscriminator<Service> {

    public static ResourceDiscriminator<Service, ApicurioStudio> INSTANCE = new PostgresqlServiceDiscriminator();


    public PostgresqlServiceDiscriminator() {
        super(Map.of(
                "app.kubernetes.io/name", "apicurio-studio",
                "app.kubernetes.io/component", COMPONENT_POSTGRESQL
        ));
    }
}
