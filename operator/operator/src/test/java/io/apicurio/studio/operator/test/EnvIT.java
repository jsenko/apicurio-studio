package io.apicurio.studio.operator.test;

import io.apicurio.studio.operator.api.v1.model.ApicurioStudioBuilder;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.apicurio.studio.operator.resource.ResourceFactory.APP_CONTAINER_NAME;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class EnvIT {

    private static final Logger log = LoggerFactory.getLogger(EnvIT.class);

    private static final OperatorTestExtension ext = OperatorTestExtension.builder().build();


    @BeforeAll
    static void beforeAll() {
        ext.start();
    }


    @Test
    void testEnv() {

        var env = range(1, 20)
                .mapToObj(i -> new EnvVarBuilder().withName("test__name" + i).withValue("value" + i).build())
                .toList();

        var name = "env";

        // @formatter:off
        var as1 = new ApicurioStudioBuilder()
                .withNewMetadata()
                    .withName(name)
                .endMetadata()
                .withNewSpec()
                    .withNewApp()
                        .withEnv(env)
                    .endApp()
                .endSpec()
                .build();
        // @formatter:on

        ext.create(as1);

        ext.expect(Deployment.class, name + "-app-deployment", r -> {
            var container = r.getSpec().getTemplate().getSpec().getContainers().stream().filter(c -> APP_CONTAINER_NAME.equals(c.getName())).findFirst().orElse(null);
            assertThat(container).isNotNull();
            assertThat(container.getEnv().stream().filter(e -> e.getName().startsWith("test__")).toList()).isEqualTo(env);
        });

        ext.delete(as1);
    }


    @AfterAll
    static void afterAll() {
        ext.stop();
    }
}
