package io.apicurio.studio.operator.api.v1.model;

import io.fabric8.kubernetes.api.model.DefaultKubernetesResourceList;
import io.sundr.builder.annotations.Buildable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;


@Buildable(
        editableEnabled = false,
        builderPackage = "io.fabric8.kubernetes.api.builder"
)
@Getter
@Setter
@ToString(callSuper = true)
public class ApicurioStudioList extends DefaultKubernetesResourceList<ApicurioStudio> {

    public ApicurioStudioList() {
        setApiVersion("studio.apicur.io/v1");
        setKind("ApicurioStudios");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApicurioStudioList other = (ApicurioStudioList) o;

        return Objects.equals(getApiVersion(), other.getApiVersion()) &&
                Objects.equals(getKind(), other.getKind()) &&
                Objects.equals(getMetadata(), other.getMetadata()) &&
                Objects.equals(getItems(), other.getItems());
    }


    @Override
    public int hashCode() {
        return Objects.hash(getApiVersion(), getKind(), getMetadata(), getItems());
    }
}
