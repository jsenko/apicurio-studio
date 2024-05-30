package io.apicurio.studio.operator.resource.ui;

import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;
import io.apicurio.studio.operator.resource.LabelDiscriminator;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;

import java.util.Map;

import static io.apicurio.studio.operator.resource.ResourceFactory.COMPONENT_UI;

public class UIIngressDiscriminator extends LabelDiscriminator<Ingress> {

    public static ResourceDiscriminator<Ingress, ApicurioStudio> INSTANCE = new UIIngressDiscriminator();


    public UIIngressDiscriminator() {
        super(Map.of(
                "app.kubernetes.io/name", "apicurio-studio",
                "app.kubernetes.io/component", COMPONENT_UI
        ));
    }
}
