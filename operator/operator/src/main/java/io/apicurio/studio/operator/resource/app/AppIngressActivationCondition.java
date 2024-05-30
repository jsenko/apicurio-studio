package io.apicurio.studio.operator.resource.app;

import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition;

import static java.lang.Boolean.TRUE;


public class AppIngressActivationCondition implements Condition<Ingress, ApicurioStudio> {


    @Override
    public boolean isMet(DependentResource<Ingress, ApicurioStudio> resource, ApicurioStudio primary, Context<ApicurioStudio> context) {

        var disabled = primary.getSpec().getApp() != null &&
                primary.getSpec().getApp().getFeatures() != null &&
                primary.getSpec().getApp().getFeatures().getIngress() != null &&
                TRUE.equals(primary.getSpec().getApp().getFeatures().getIngress().getDisabled());

        if (disabled) {
            ((AppIngressResource) resource).delete(primary, context);
        }

        return !disabled;
    }
}
