package io.apicurio.studio.operator.resource.postgresql;

import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;
import io.apicurio.studio.operator.resource.LabelDiscriminator;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;

import java.util.Map;

import static io.apicurio.studio.operator.resource.ResourceFactory.COMPONENT_POSTGRESQL;

public class PostgresqlDeploymentDiscriminator extends LabelDiscriminator<Deployment> {

    public static ResourceDiscriminator<Deployment, ApicurioStudio> INSTANCE = new PostgresqlDeploymentDiscriminator();


    public PostgresqlDeploymentDiscriminator() {
        super(Map.of(
                "app.kubernetes.io/name", "apicurio-studio",
                "app.kubernetes.io/component", COMPONENT_POSTGRESQL
        ));
    }
}
