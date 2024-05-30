package io.apicurio.studio.operator.api.v1.model;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.BuildableReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Group("studio.apicur.io")
@Version("v1")
@ShortNames("studio")
@Plural("ApicurioStudios")
@Buildable(
        editableEnabled = false,
        builderPackage = "io.fabric8.kubernetes.api.builder",
        refs = {
                @BuildableReference(ObjectMeta.class)
        }
)
@Getter
@Setter
@ToString(callSuper = true)
public class ApicurioStudio extends CustomResource<ApicurioStudioSpec, ApicurioStudioStatus> implements Namespaced {

}
