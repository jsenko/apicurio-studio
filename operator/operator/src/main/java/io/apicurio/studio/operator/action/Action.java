package io.apicurio.studio.operator.action;

import io.apicurio.studio.operator.context.CRContext;
import io.apicurio.studio.operator.resource.ResourceKey;
import io.apicurio.studio.operator.state.State;

import java.util.List;

public interface Action<STATE extends State> {


    List<ResourceKey<?>> supports();


    ActionOrder ordering();


    Class<STATE> getStateClass();


    STATE initialize(CRContext crContext);


    boolean shouldRun(STATE state, CRContext crContext);


    void run(STATE state, CRContext crContext);
}
