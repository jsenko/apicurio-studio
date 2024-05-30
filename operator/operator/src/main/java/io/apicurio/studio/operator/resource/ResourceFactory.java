package io.apicurio.studio.operator.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.apicurio.studio.operator.Mapper;
import io.apicurio.studio.operator.OperatorException;
import io.apicurio.studio.operator.api.v1.model.ApicurioStudio;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;

import java.nio.charset.Charset;
import java.util.Map;


public class ResourceFactory {

    public static final ResourceFactory INSTANCE = new ResourceFactory();

    public static final String COMPONENT_POSTGRESQL = "postgresql";
    public static final String COMPONENT_APP = "app";
    public static final String COMPONENT_UI = "ui";

    public static final String RESOURCE_TYPE_DEPLOYMENT = "deployment";
    public static final String RESOURCE_TYPE_SERVICE = "service";
    public static final String RESOURCE_TYPE_INGRESS = "ingress";

    public static final String APP_CONTAINER_NAME = "apicurio-studio-app";
    public static final String UI_CONTAINER_NAME = "apicurio-studio-ui";


    public Deployment getDefaultPostgresqlDeployment(ApicurioStudio primary) {
        var r = getDefaultResource(primary, Deployment.class, RESOURCE_TYPE_DEPLOYMENT, COMPONENT_POSTGRESQL);
        addDefaultLabels(r.getSpec().getTemplate().getMetadata().getLabels(), primary, COMPONENT_POSTGRESQL);
        addSelectorLabels(r.getSpec().getSelector().getMatchLabels(), primary, COMPONENT_POSTGRESQL);
        return r;
    }


    public Deployment getDefaultAppDeployment(ApicurioStudio primary) {
        var r = getDefaultResource(primary, Deployment.class, RESOURCE_TYPE_DEPLOYMENT, COMPONENT_APP);
        addDefaultLabels(r.getSpec().getTemplate().getMetadata().getLabels(), primary, COMPONENT_APP);
        addSelectorLabels(r.getSpec().getSelector().getMatchLabels(), primary, COMPONENT_APP);
        return r;
    }


    public Deployment getDefaultUIDeployment(ApicurioStudio primary) {
        var r = getDefaultResource(primary, Deployment.class, RESOURCE_TYPE_DEPLOYMENT, COMPONENT_UI);
        addDefaultLabels(r.getSpec().getTemplate().getMetadata().getLabels(), primary, COMPONENT_UI);
        addSelectorLabels(r.getSpec().getSelector().getMatchLabels(), primary, COMPONENT_UI);
        return r;
    }


    public Service getDefaultPostgresqlService(ApicurioStudio primary) {
        var r = getDefaultResource(primary, Service.class, RESOURCE_TYPE_SERVICE, COMPONENT_POSTGRESQL);
        addSelectorLabels(r.getSpec().getSelector(), primary, COMPONENT_POSTGRESQL);
        return r;
    }


    public Service getDefaultAppService(ApicurioStudio primary) {
        var r = getDefaultResource(primary, Service.class, RESOURCE_TYPE_SERVICE, COMPONENT_APP);
        addSelectorLabels(r.getSpec().getSelector(), primary, COMPONENT_APP);
        return r;
    }


    public Service getDefaultUIService(ApicurioStudio primary) {
        var r = getDefaultResource(primary, Service.class, RESOURCE_TYPE_SERVICE, COMPONENT_UI);
        addSelectorLabels(r.getSpec().getSelector(), primary, COMPONENT_UI);
        return r;
    }


    public Ingress getDefaultAppIngress(ApicurioStudio primary) {
        var r = getDefaultResource(primary, Ingress.class, RESOURCE_TYPE_INGRESS, COMPONENT_APP);
        r.getSpec().getRules().get(0).getHttp().getPaths().get(0).getBackend().getService()
                .setName(primary.getMetadata().getName() + "-" + COMPONENT_APP + "-" + RESOURCE_TYPE_SERVICE);
        return r;
    }


    public Ingress getDefaultUIIngress(ApicurioStudio primary) {
        var r = getDefaultResource(primary, Ingress.class, RESOURCE_TYPE_INGRESS, COMPONENT_UI);
        r.getSpec().getRules().get(0).getHttp().getPaths().get(0).getBackend().getService()
                .setName(primary.getMetadata().getName() + "-" + COMPONENT_UI + "-" + RESOURCE_TYPE_SERVICE);
        return r;
    }


    private <T extends HasMetadata> T getDefaultResource(ApicurioStudio primary, Class<T> klass, String resourceType, String component) {
        var r = deserialize("/k8s/default/" + component + "." + resourceType + ".yaml", klass);
        r.getMetadata().setNamespace(primary.getMetadata().getNamespace());
        r.getMetadata().setName(primary.getMetadata().getName() + "-" + component + "-" + resourceType);
        addDefaultLabels(r.getMetadata().getLabels(), primary, component);
        return r;
    }


    private void addDefaultLabels(Map<String, String> labels, ApicurioStudio primary, String component) {
        labels.putAll(Map.of(
                "app", primary.getMetadata().getName(),
                "app.kubernetes.io/name", "apicurio-studio",
                "app.kubernetes.io/component", component,
                "app.kubernetes.io/instance", primary.getMetadata().getName(),
                "app.kubernetes.io/version", "1.0.0", // TODO
                "app.kubernetes.io/part-of", "apicurio-studio"
        ));
    }


    private void addSelectorLabels(Map<String, String> labels, ApicurioStudio primary, String component) {
        labels.putAll(Map.of(
                "app", primary.getMetadata().getName(),
                "app.kubernetes.io/name", "apicurio-studio",
                "app.kubernetes.io/component", component,
                "app.kubernetes.io/instance", primary.getMetadata().getName(),
                "app.kubernetes.io/part-of", "apicurio-studio"
        ));
    }


    private <T extends HasMetadata> T deserialize(String path, Class<T> klass) {
        try {
            return Mapper.YAML_MAPPER.readValue(load(path), klass);
        } catch (JsonProcessingException ex) {
            throw new OperatorException("Could not deserialize default resource: " + path, ex);
        }
    }


    private String load(String path) {
        try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            return new String(stream.readAllBytes(), Charset.defaultCharset());
        } catch (Exception ex) {
            throw new OperatorException("Could not read default resource: " + path, ex);
        }
    }
}
