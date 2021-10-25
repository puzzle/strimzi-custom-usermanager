package ch.puzzle.strimzi.usermanager.boundary;

import ch.puzzle.strimzi.usermanager.entity.KafkaUserDto;
import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/kafkausers")
public class KafkaUserResource {

    private static final Logger logger = LoggerFactory.getLogger(KafkaUserResource.class);

    @Inject
    KubernetesClient client;

    @ConfigProperty(name = "kafka.cluster.name", defaultValue = "default")
    String clusterName;

    @ConfigProperty(name = "kubernetes.client.namespace", defaultValue = "default")
    String namespace;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> list() {
        try {
            CustomResourceDefinitionContext context = new CustomResourceDefinitionContext.Builder()
                    .withVersion("v1beta2")
                    .withGroup("kafka.strimzi.io")
                    .withScope("Namespaced")
                    .withPlural("kafkausers")
                    .build();

            logger.info("KafkaUser resources in namespace: ");
            return client.genericKubernetesResources(context)
                    .inNamespace(namespace)
                    .list().getItems().stream()
                    .map(GenericKubernetesResource::getMetadata)
                    .map(ObjectMeta::getName)
                    .collect(Collectors.toList());
        } catch (KubernetesClientException e) {
            System.out.println("Error catched");
        }

        return new ArrayList<>();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public GenericKubernetesResource create(KafkaUserDto dto) {
        try {
            CustomResourceDefinitionContext context = new CustomResourceDefinitionContext.Builder()
                    .withVersion("v1beta2")
                    .withGroup("kafka.strimzi.io")
                    .withScope("Namespaced")
                    .withPlural("kafkausers")
                    .build();

            logger.info("Creating KafkaUser {}: ", dto.name);
            // Creating from Raw JSON String
            String kafkaUser = "{\n" +
                    "  \"apiVersion\": \"kafka.strimzi.io/v1beta2\",\n" +
                    "  \"kind\": \"KafkaUser\",\n" +
                    "  \"metadata\": {\n" +
                    "    \"annotations\": {\n" +
                    "      \"managed-by\": \"custom-user-manager\"\n" +
                    "    },\n" +
                    "    \"name\": \"" + dto.name + "\",\n" +
                    "    \"labels\": {\n" +
                    "      \"strimzi.io/cluster\": \"" + clusterName + "\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"spec\": {\n" +
                    "    \"authentication\": {\n" +
                    "      \"type\": \"tls\"\n" +
                    "    },\n" +
                    "    \"authorization\": {\n" +
                    "      \"type\": \"simple\",\n" +
                    "      \"acls\": [\n" +
                    "        {\n" +
                    "          \"resource\": {\n" +
                    "            \"type\": \"group\",\n" +
                    "            \"name\": \"test-\",\n" +
                    "            \"patternType\": \"prefix\"\n" +
                    "          },\n" +
                    "          \"operation\": \"Read\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            return client.genericKubernetesResources(context)
                    .inNamespace(namespace)
                    .load(new ByteArrayInputStream(kafkaUser.getBytes(StandardCharsets.UTF_8)))
                    .create();

        } catch (KubernetesClientException e) {
            logger.warn("Error catched", e);
        }

        return null;
    }
}