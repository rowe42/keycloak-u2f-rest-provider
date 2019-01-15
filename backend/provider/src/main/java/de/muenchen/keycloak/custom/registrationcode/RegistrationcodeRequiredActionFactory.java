package de.muenchen.keycloak.custom.registrationcode;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegistrationcodeRequiredActionFactory implements RequiredActionFactory {

    private static final RegistrationcodeRequiredAction SINGLETON = new RegistrationcodeRequiredAction();

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return SINGLETON;
    }


    @Override
    public String getId() {
        return RegistrationcodeRequiredAction.PROVIDER_ID;
    }

    @Override
    public String getDisplayText() {
        return "Registration Code";
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

}
