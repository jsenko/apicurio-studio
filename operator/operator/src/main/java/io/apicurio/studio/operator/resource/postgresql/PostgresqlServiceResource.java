package io.apicurio.studio.operator.resource.postgresql;

import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;
import io.apicurio.studio.operator.context.GlobalContext;
import io.fabric8.kubernetes.api.model.Service;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import jakarta.inject.Inject;

import static io.apicurio.studio.operator.resource.ResourceFactory.COMPONENT_POSTGRESQL;
import static io.apicurio.studio.operator.resource.ResourceKey.POSTGRESQL_SERVICE_KEY;
import static io.apicurio.studio.operator.utils.FunctionalUtils.returnSecondArg;


@KubernetesDependent(
        labelSelector = "app.kubernetes.io/name=apicurio-studio,app.kubernetes.io/component=" + COMPONENT_POSTGRESQL,
        resourceDiscriminator = PostgresqlServiceDiscriminator.class
)
public class PostgresqlServiceResource extends CRUDKubernetesDependentResource<Service, ApicurioStudio> {


    @Inject
    GlobalContext globalContext;


    public PostgresqlServiceResource() {
        super(Service.class);
    }


    @Override
    protected Service desired(ApicurioStudio primary, Context<ApicurioStudio> context) {
        return globalContext.reconcileReturn(POSTGRESQL_SERVICE_KEY, primary, context, returnSecondArg());
    }
}
