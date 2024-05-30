package io.apicurio.studio.operator;

import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;
import io.apicurio.studio.operator.context.GlobalContext;
import io.apicurio.studio.operator.resource.ResourceKey;
import io.apicurio.studio.operator.resource.app.AppDeploymentResource;
import io.apicurio.studio.operator.resource.app.AppIngressActivationCondition;
import io.apicurio.studio.operator.resource.app.AppIngressResource;
import io.apicurio.studio.operator.resource.app.AppServiceResource;
import io.apicurio.studio.operator.resource.postgresql.PostgresqlDeploymentResource;
import io.apicurio.studio.operator.resource.postgresql.PostgresqlServiceResource;
import io.apicurio.studio.operator.resource.ui.UIDeploymentResource;
import io.apicurio.studio.operator.resource.ui.UIIngressResource;
import io.apicurio.studio.operator.resource.ui.UIServiceResource;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import jakarta.inject.Inject;

import static io.apicurio.studio.operator.resource.ResourceKey.*;


@ControllerConfiguration(
        dependents = {
                @Dependent(
                        type = PostgresqlDeploymentResource.class,
                        name = ResourceKey.POSTGRESQL_DEPLOYMENT_ID
                ),
                @Dependent(
                        type = PostgresqlServiceResource.class,
                        name = ResourceKey.POSTGRESQL_SERVICE_ID,
                        dependsOn = {ResourceKey.POSTGRESQL_DEPLOYMENT_ID}
                ),
                @Dependent(
                        type = AppDeploymentResource.class,
                        name = ResourceKey.APP_DEPLOYMENT_ID,
                        dependsOn = {ResourceKey.POSTGRESQL_SERVICE_ID}
                ),
                @Dependent(
                        type = AppServiceResource.class,
                        name = ResourceKey.APP_SERVICE_ID,
                        dependsOn = {ResourceKey.APP_DEPLOYMENT_ID}
                ),
                @Dependent(
                        type = AppIngressResource.class,
                        name = ResourceKey.APP_INGRESS_ID,
                        dependsOn = {ResourceKey.APP_SERVICE_ID},
                        activationCondition = AppIngressActivationCondition.class
                ),
                @Dependent(
                        type = UIDeploymentResource.class,
                        name = ResourceKey.UI_DEPLOYMENT_ID,
                        dependsOn = {ResourceKey.APP_DEPLOYMENT_ID}
                ),
                @Dependent(
                        type = UIServiceResource.class,
                        name = ResourceKey.UI_SERVICE_ID,
                        dependsOn = {ResourceKey.UI_DEPLOYMENT_ID}
                ),
                @Dependent(
                        type = UIIngressResource.class,
                        name = UI_INGRESS_ID,
                        dependsOn = {UI_SERVICE_ID}
                )
        }
)
public class ApicurioStudioOperatorReconciler implements Reconciler<ApicurioStudio>, Cleaner<ApicurioStudio> {


    @Inject
    GlobalContext globalContext;


    public UpdateControl<ApicurioStudio> reconcile(ApicurioStudio primary, Context<ApicurioStudio> context) {

        return globalContext.reconcileReturn(STUDIO_KEY, primary, context, (crContext, p) -> {
            UpdateControl<ApicurioStudio> uc;
            if (crContext.isUpdatePrimary()) {
                // This should only happen rarely:
                uc = UpdateControl.updateResourceAndPatchStatus(p);
            } else if (crContext.isUpdateStatus()) {
                uc = UpdateControl.patchStatus(p);
            } else {
                uc = UpdateControl.noUpdate();
            }
            if (crContext.getReschedule() != null) {
                uc.rescheduleAfter(crContext.getReschedule());
            }
            return uc;
        });
    }


    @Override
    public DeleteControl cleanup(ApicurioStudio primary, Context<ApicurioStudio> context) {
        globalContext.cleanup(primary);
        return DeleteControl.defaultDelete();
    }
}
