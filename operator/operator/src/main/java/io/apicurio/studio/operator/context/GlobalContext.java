package io.apicurio.studio.operator.context;

import io.apicurio.studio.operator.action.Action;
import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;
import io.apicurio.studio.operator.resource.ResourceKey;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.apicurio.studio.operator.utils.LogUtils.contextPrefix;

@ApplicationScoped
public class GlobalContext {

    private static final Logger log = LoggerFactory.getLogger(GlobalContext.class);


    @Inject
    Instance<Action<?>> actions;


    private final Map<ResourceID, CRContext> crContextMap = new ConcurrentHashMap<>();


    private List<Action<?>> getActions() {
        return actions.stream().sorted(Comparator.comparingInt(a -> a.ordering().getValue())).collect(Collectors.toList()); // TODO: Cache?
    }


    public <T> T withCRContextReturn(ApicurioStudio primary, Context<ApicurioStudio> context, Function<CRContext, T> run) {
        var crContext = crContextMap.computeIfAbsent(ResourceID.fromResource(primary), k -> new CRContext());
        try {
            // TODO: At the moment, we cannot concurrently run multiple reconcile processes for the same CR.
            crContext.LOCK.lock();
            if (!crContext.isInitialized) {
                crContext.initialize(getActions(), primary, context);
                crContext.isInitialized = true;
            }
            return run.apply(crContext);
        } finally {
            crContext.LOCK.unlock();
        }
    }


    public <T, R> T reconcileReturn(ResourceKey<R> key, ApicurioStudio primary, Context<ApicurioStudio> context, BiFunction<CRContext, R, T> postProcess) {
        log.info("{}Reconciling {}", contextPrefix(primary), key);
        return withCRContextReturn(primary, context, crContext -> {
            var r = crContext.runActions(getActions(), primary, context, key);
            var rval = postProcess.apply(crContext, r);
            crContext.reset();
            return rval;
        });
    }


    public void cleanup(ApicurioStudio primary) {
        crContextMap.remove(ResourceID.fromResource(primary));
        log.info("{}CR context deleted", contextPrefix(primary));
    }
}
