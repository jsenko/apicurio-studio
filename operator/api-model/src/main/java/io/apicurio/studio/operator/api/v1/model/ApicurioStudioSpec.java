package io.apicurio.studio.operator.api.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.sundr.builder.annotations.Buildable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@Buildable(
        editableEnabled = false,
        builderPackage = "io.fabric8.kubernetes.api.builder"
)
@JsonInclude(NON_NULL)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ApicurioStudioSpec {

    @JsonPropertyDescription("Configuration for Apicurio Studio application component")
    private AppSpec app;

    @JsonPropertyDescription("Configuration for Apicurio Studio web UI component")
    private UISpec ui;
}
