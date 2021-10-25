package ch.puzzle.strimzi.k8s;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@Singleton
public class KubernetesClientProducer {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesClientProducer.class);

    @ConfigProperty(name = "kubernetes.client.master.url", defaultValue="https://kubernetes.default.svc")
    String masterUrl;

    @ConfigProperty(name = "kubernetes.client.namespace", defaultValue="default")
    String namespace;

    @ConfigProperty(name = "kubernetes.client.token")
    String token;

    @Produces
    public KubernetesClient kubernetesClient() {
        // here you would create a custom client
        Config config = new ConfigBuilder()
                .withMasterUrl(masterUrl)
                .withDisableHostnameVerification(true)
                .withNamespace(namespace)
                .withRequestTimeout(10000)
                .withConnectionTimeout(10000)
                .withOauthToken(token)
                .build();

        logger.info("Creating KubernetesClient for Namespace '{}' on '{}'", namespace, masterUrl);
        return new DefaultKubernetesClient(config);
    }
}