package io.apicurio.studio.operator.action.impl.ui;

import io.apicurio.studio.operator.action.AbstractAction;
import io.apicurio.studio.operator.action.ActionOrder;
import io.apicurio.studio.operator.context.CRContext;
import io.apicurio.studio.operator.resource.ResourceKey;
import io.apicurio.studio.operator.state.impl.EnvCachePriority;
import io.apicurio.studio.operator.state.impl.UIEnvCache;
import io.fabric8.kubernetes.api.model.EnvVar;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import static io.apicurio.studio.operator.action.ActionOrder.ORDERING_EARLY;
import static io.apicurio.studio.operator.resource.ResourceKey.UI_DEPLOYMENT_KEY;
import static io.apicurio.studio.operator.utils.TraverseUtils.with;

@ApplicationScoped
public class UISpecEnvAction extends AbstractAction<UIEnvCache> {


    @Override
    public List<ResourceKey<?>> supports() {
        return List.of(UI_DEPLOYMENT_KEY);
    }


    @Override
    public ActionOrder ordering() {
        return ORDERING_EARLY;
    }


    @Override
    public Class<UIEnvCache> getStateClass() {
        return UIEnvCache.class;
    }


    @Override
    public UIEnvCache initialize(CRContext crContext) {
        return new UIEnvCache();
    }


    @Override
    public void run(UIEnvCache state, CRContext crContext) {

        with(crContext.getPrimary().getSpec().getUi(), uiSpec -> {
            with(uiSpec.getEnv(), env -> {
                EnvVar last = null;
                for (EnvVar e : env) {
                    state.add(e, EnvCachePriority.SPEC_HIGH, last == null ? new String[]{} : new String[]{last.getName()});
                    last = e;
                }
            });
        });
    }
}
