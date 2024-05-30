package io.apicurio.studio.operator.action.impl;

import io.apicurio.studio.operator.action.AbstractBasicAction;
import io.apicurio.studio.operator.action.ActionOrder;
import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;
import io.apicurio.studio.operator.api.v1.model.AppSpec;
import io.apicurio.studio.operator.api.v1.model.UISpec;
import io.apicurio.studio.operator.context.CRContext;
import io.apicurio.studio.operator.state.NoState;
import io.apicurio.studio.operator.state.impl.ClusterInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static io.apicurio.studio.operator.action.ActionOrder.ORDERING_EARLY;
import static io.apicurio.studio.operator.resource.ResourceFactory.COMPONENT_APP;
import static io.apicurio.studio.operator.resource.ResourceFactory.COMPONENT_UI;
import static io.apicurio.studio.operator.resource.ResourceKey.STUDIO_KEY;
import static io.apicurio.studio.operator.utils.TraverseUtils.isEmpty;


@ApplicationScoped
public class HostInitAction extends AbstractBasicAction {


    @Inject
    ClusterInfo clusterInfo;


    @Override
    public ActionOrder ordering() {
        return ORDERING_EARLY;
    }


    @Override
    public void run(NoState state, CRContext crContext) {

        crContext.withDesiredResource(STUDIO_KEY, p -> {

            if (p.getSpec().getApp() == null) {
                p.getSpec().setApp(new AppSpec());
            }

            if (isEmpty(p.getSpec().getApp().getHost()) && clusterInfo.getCanonicalHost() != null) {
                p.getSpec().getApp().setHost(getHost(COMPONENT_APP, p));
            }

            if (p.getSpec().getUi() == null) {
                p.getSpec().setUi(new UISpec());
            }

            if (isEmpty(p.getSpec().getUi().getHost()) && clusterInfo.getCanonicalHost() != null) {
                p.getSpec().getUi().setHost(getHost(COMPONENT_UI, p));
            }
        });
    }


    private String getHost(String component, ApicurioStudio p) {
        var prefix = p.getMetadata().getName() + "-" + component + "." + p.getMetadata().getNamespace();
        String host;
        if (clusterInfo.getCanonicalHost().isPresent()) {
            host = prefix + "." + clusterInfo.getCanonicalHost().get();
        } else {
            host = prefix + ".cluster.example";
        }
        return host;
    }
}
