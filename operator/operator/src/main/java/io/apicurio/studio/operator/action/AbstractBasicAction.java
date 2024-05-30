package io.apicurio.studio.operator.action;

import io.apicurio.studio.operator.context.CRContext;
import io.apicurio.studio.operator.state.NoState;

import static io.apicurio.studio.operator.state.NoState.INSTANCE;

public abstract class AbstractBasicAction extends AbstractAction<NoState> {


    @Override
    public Class<NoState> getStateClass() {
        return NoState.class;
    }


    @Override
    public NoState initialize(CRContext crContext) {
        return INSTANCE;
    }
}
