package io.apicurio.studio.operator.resource.app;

import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;
import io.apicurio.studio.operator.resource.LabelDiscriminator;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;

import java.util.Map;

import static io.apicurio.studio.operator.resource.ResourceFactory.COMPONENT_APP;


public class AppDeploymentDiscriminator extends LabelDiscriminator<Deployment> {

    public static final ResourceDiscriminator<Deployment, ApicurioStudio> INSTANCE = new AppDeploymentDiscriminator();


    public AppDeploymentDiscriminator() {
        super(Map.of(
                "app.kubernetes.io/name", "apicurio-studio",
                "app.kubernetes.io/component", COMPONENT_APP
        ));
    }
}
