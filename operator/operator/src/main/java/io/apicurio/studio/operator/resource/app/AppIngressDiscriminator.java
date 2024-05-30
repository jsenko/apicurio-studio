package io.apicurio.studio.operator.resource.app;

import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;
import io.apicurio.studio.operator.resource.LabelDiscriminator;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;

import java.util.Map;

import static io.apicurio.studio.operator.resource.ResourceFactory.COMPONENT_APP;

public class AppIngressDiscriminator extends LabelDiscriminator<Ingress> {

    public static final ResourceDiscriminator<Ingress, ApicurioStudio> INSTANCE = new AppIngressDiscriminator();


    public AppIngressDiscriminator() {
        super(Map.of(
                "app.kubernetes.io/name", "apicurio-studio",
                "app.kubernetes.io/component", COMPONENT_APP
        ));
    }
}
