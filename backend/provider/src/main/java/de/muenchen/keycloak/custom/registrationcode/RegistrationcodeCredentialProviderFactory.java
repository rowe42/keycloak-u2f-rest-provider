package de.muenchen.keycloak.custom.registrationcode;

import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.CredentialProviderFactory;
import org.keycloak.models.KeycloakSession;

/**
 * @author rowe42
 * @version $Revision: 1 $
 */
public class RegistrationcodeCredentialProviderFactory implements CredentialProviderFactory<RegistrationcodeCredentialProvider> {
    @Override
    public String getId() {
        return "registrationcode";
    }

    @Override
    public CredentialProvider create(KeycloakSession session) {
        return new RegistrationcodeCredentialProvider(session);
    }
}
