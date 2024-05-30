package io.apicurio.studio.operator.context;

import io.apicurio.studio.operator.OperatorException;
import io.apicurio.studio.operator.action.Action;
import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;
import io.apicurio.studio.operator.resource.ResourceKey;
import io.apicurio.studio.operator.state.NoState;
import io.apicurio.studio.operator.state.State;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static io.apicurio.studio.operator.resource.ResourceKey.STUDIO_KEY;
import static io.apicurio.studio.operator.utils.LogUtils.contextPrefix;
import static io.apicurio.studio.operator.utils.ResourceUtils.duplicate;
import static java.util.Objects.requireNonNull;


public class CRContext {

    private static final Logger log = LoggerFactory.getLogger(CRContext.class);

    final ReentrantLock LOCK = new ReentrantLock();

    boolean isInitialized;

    private final Map<Class<?>, State> actionStateMap = new HashMap<>();

    private final Map<String, Object> desired = new HashMap<>();

    @Getter
    private ApicurioStudio primary;

    @Getter
    private Context<ApicurioStudio> context;

    @Getter
    private boolean updatePrimary;

    @Getter
    private boolean updateStatus;

    @Getter
    private Duration reschedule;


    public void initialize(List<Action<?>> actions, ApicurioStudio primary, Context<ApicurioStudio> context) {
        log.info("{}Initializing new CR context", contextPrefix(primary));
        this.primary = primary;
        this.context = context;
        for (Action<?> action : actions) {
            log.debug("{}Initializing action {}", contextPrefix(primary), action.getClass());
            var key = action.getStateClass();
            var existing = actionStateMap.get(key);
            var state = action.initialize(this);
            if (state != null) {
                if (existing == null) {
                    actionStateMap.put(key, state);
                } else if (!NoState.class.equals(key)) {
                    log.warn("{}State {} has already been initialized.", contextPrefix(primary), key);
                }
            } else {
                if (existing == null) {
                    throw new OperatorException("State " + key + " initialization returned null.");
                }
            }
        }
    }


    public <R> R runActions(List<Action<?>> actions, ApicurioStudio primary, Context<ApicurioStudio> context, ResourceKey<R> key) {
        this.primary = primary;
        this.context = context;
        for (Action<?> action : actions) {
            var stateClass = action.getStateClass();
            var state = actionStateMap.get(stateClass);
            requireNonNull(state);
            var a = (Action) action;
            if (a.supports().contains(key)) {
                if (a.shouldRun(state, this)) {
                    log.debug("{}Running action {}", contextPrefix(primary), a.getClass());
                    a.run(state, this);
                } else {
                    log.trace("{}Skipping action {}", contextPrefix(primary), a.getClass());
                }
            } else {
                log.trace("{}Skipping action {}, because it does not support resource {}", contextPrefix(primary), a.getClass(), key);
            }
        }
        return getDesiredResource(key);
    }


    public <R> void withExistingResource(ResourceKey<R> key, Consumer<R> action) {
        if (STUDIO_KEY.equals(key)) {
            throw new OperatorException("Use CRContext::getPrimary() if you are not updating the CR.");
        } else {
            var r = context.getSecondaryResource(key.getKlass(), key.getDiscriminator());
            if (r.isPresent()) {
                action.accept(r.get());
            } else {
                log.debug("{}Existing resource {} not found.", contextPrefix(primary), key);
            }
        }
    }


    private <R> R getDesiredResource(ResourceKey<R> key) {
        var r = desired.get(key.getId());
        if (r == null) {
            if (STUDIO_KEY.equals(key)) {
                r = duplicate(primary, ApicurioStudio.class);
            } else {
                log.debug("{}Getting fresh {} resource from factory.", contextPrefix(primary), key);
                r = key.getFactory().apply(primary);
                requireNonNull(r);
            }
            desired.put(key.getId(), r);
        }
        return (R) r;
    }


    public <R> void withDesiredResource(ResourceKey<R> key, Consumer<R> action) {
        action.accept(getDesiredResource(key));
        if (STUDIO_KEY.equals(key)) {
            updatePrimary = true;
        }
    }


    public void rescheduleSeconds(int seconds) {
        var d = Duration.ofSeconds(seconds);
        if (reschedule == null || reschedule.compareTo(d) > 0) {
            reschedule = d;
        }
    }


    public void reschedule() {
        rescheduleSeconds(5);
    }


    public void reset() {
        primary = null;
        updatePrimary = false;
        updateStatus = false;
        desired.clear();
        reschedule = null;
    }
}
