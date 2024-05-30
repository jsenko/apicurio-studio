package io.apicurio.studio.operator.api.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.BuildableReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@Buildable(
        editableEnabled = false,
        builderPackage = "io.fabric8.kubernetes.api.builder",
        refs = {
                @BuildableReference(PodTemplateSpec.class)
        }
)
@JsonInclude(NON_NULL)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UISpec {

    private List<EnvVar> env;

    private PodTemplateSpec podTemplate;

    private String host;
}
