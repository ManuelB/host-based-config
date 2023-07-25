package de.health.service.keycloak;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Host based config resolver.
 *
 */
@ConditionalOnProperty(prefix = "keycloak.config", name = "resolver", havingValue = "host")
public class HostBasedConfigResolver implements KeycloakConfigResolver {

    private final ConcurrentHashMap<String, KeycloakDeployment> cache = new ConcurrentHashMap<>();
    
    @SuppressWarnings("unused")
    private static AdapterConfig adapterConfig;

    @Override
    public KeycloakDeployment resolve(HttpFacade.Request facade) {
        String host = facade.getHeader("X-Forwarded-Host");
        if(host == null) {
            try {
                host = new URI(facade.getURI()).getHost();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (!cache.containsKey(host)) {
            String fileName = "/" + host + "-keycloak.json";
            InputStream is = getClass().getResourceAsStream(fileName);
            if(is == null) {
                throw new IllegalArgumentException("Could not find "+fileName+" for host " + host + " in classpath");
            }
            cache.put(host, KeycloakDeploymentBuilder.build(is));
        }
        
        return cache.get(host);
    }

    static void setAdapterConfig(AdapterConfig adapterConfig) {
        HostBasedConfigResolver.adapterConfig = adapterConfig;
    }
 
}
