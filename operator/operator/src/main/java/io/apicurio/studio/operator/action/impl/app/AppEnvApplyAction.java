package io.apicurio.studio.operator.action.impl.app;

import io.apicurio.studio.operator.action.AbstractAction;
import io.apicurio.studio.operator.action.ActionOrder;
import io.apicurio.studio.operator.context.CRContext;
import io.apicurio.studio.operator.resource.ResourceKey;
import io.apicurio.studio.operator.state.impl.AppEnvCache;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import static io.apicurio.studio.operator.action.ActionOrder.ORDERING_LATE;
import static io.apicurio.studio.operator.resource.ResourceFactory.APP_CONTAINER_NAME;
import static io.apicurio.studio.operator.resource.ResourceKey.APP_DEPLOYMENT_KEY;
import static io.apicurio.studio.operator.resource.ResourceKey.POSTGRESQL_SERVICE_KEY;
import static io.apicurio.studio.operator.state.impl.EnvCachePriority.OPERATOR_LOW;
import static io.apicurio.studio.operator.utils.TraverseUtils.where;


@ApplicationScoped
public class AppEnvApplyAction extends AbstractAction<AppEnvCache> {


    @Override
    public List<ResourceKey<?>> supports() {
        return List.of(APP_DEPLOYMENT_KEY);
    }


    @Override
    public ActionOrder ordering() {
        return ORDERING_LATE;
    }


    @Override
    public Class<AppEnvCache> getStateClass() {
        return AppEnvCache.class;
    }


    @Override
    public void run(AppEnvCache state, CRContext crContext) {

        state.add("QUARKUS_PROFILE", "prod", OPERATOR_LOW)
                .add("APICURIO_LOG_LEVEL", "debug", OPERATOR_LOW)
                .add("QUARKUS_HTTP_ACCESS_LOG_ENABLED", "true", OPERATOR_LOW)
                .add("QUARKUS_HTTP_CORS_ORIGINS", "*", OPERATOR_LOW);

        crContext.withExistingResource(POSTGRESQL_SERVICE_KEY, s -> {
            state.add("APICURIO_STORAGE_DB_KIND", "postgresql", OPERATOR_LOW)
                    .add("APICURIO_DATASOURCE_USERNAME", "apicurio-studio", OPERATOR_LOW)
                    .add("APICURIO_DATASOURCE_PASSWORD", "password", OPERATOR_LOW)
                    .add("APICURIO_DATASOURCE_URL", "jdbc:postgresql://%s.%s.svc.cluster.local:5432/apicurio-studio"
                            .formatted(s.getMetadata().getName(), s.getMetadata().getNamespace()), OPERATOR_LOW);
        });

        crContext.withDesiredResource(APP_DEPLOYMENT_KEY, d -> {
            where(d.getSpec().getTemplate().getSpec().getContainers(), c -> APP_CONTAINER_NAME.equals(c.getName()), c -> {
                c.setEnv(state.getEnvAndReset());
            });
        });
    }
}
