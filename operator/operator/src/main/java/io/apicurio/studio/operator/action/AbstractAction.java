package io.apicurio.studio.operator.action;

import io.apicurio.studio.operator.context.CRContext;
import io.apicurio.studio.operator.resource.ResourceKey;
import io.apicurio.studio.operator.state.State;

import java.util.List;

import static io.apicurio.studio.operator.action.ActionOrder.ORDERING_DEFAULT;
import static io.apicurio.studio.operator.resource.ResourceKey.STUDIO_KEY;

public abstract class AbstractAction<STATE extends State> implements Action<STATE> {


    @Override
    public List<ResourceKey<?>> supports() {
        return List.of(STUDIO_KEY);
    }


    @Override
    public ActionOrder ordering() {
        return ORDERING_DEFAULT;
    }


    @Override
    public STATE initialize(CRContext crContext) {
        return null;
    }


    @Override
    public boolean shouldRun(STATE state, CRContext crContext) {
        return true;
    }
}
