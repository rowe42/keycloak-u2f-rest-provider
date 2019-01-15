package de.muenchen.keycloak.custom.registrationcode;

import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.util.ServerCookie;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import org.jboss.logging.Logger;
import org.keycloak.credential.CredentialModel;

/**
 * @author rowe42
 * @version $Revision: 1 $
 */
public class RegistrationcodeAuthenticator implements Authenticator {

    protected static final Logger LOG = Logger.getLogger(RegistrationcodeAuthenticator.class);
    public static final String CREDENTIAL_TYPE = "registrationcode";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        RealmModel realm = context.getRealm();
        UserModel user = context.getUser();
        KeycloakSession session = context.getSession();
        if (session.userCredentialManager().isConfiguredFor(realm, user, "u2f")) {
            context.success();
            return;
        }
        
        Response challenge = context.form().createForm("registrationcode.ftl");
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        if (formData.containsKey("cancel")) {
            context.cancelLogin();
            return;
        }
        boolean validated = validateAnswer(context);
        if (!validated) {
            Response challenge =  context.form()
                    .setError("badSecret")
                    .createForm("registrationcode.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }

        //if successful, remove registration code (can only be used once) - there should always only be one
        removeAllRegistrationCodes(context);
        context.success();
    }

    protected boolean validateAnswer(AuthenticationFlowContext context) {
        RealmModel realm = context.getRealm();
        UserModel user = context.getUser();
        KeycloakSession session = context.getSession();
        if (!session.userCredentialManager().isConfiguredFor(realm, user, RegistrationcodeCredentialProvider.REGISTRATIONCODE)) {
            LOG.info("No registration code registered for user " + user.getUsername());
            return false;
        }
        
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String secret = formData.getFirst("registrationcode");
        UserCredentialModel input = new UserCredentialModel();
        input.setType(RegistrationcodeCredentialProvider.REGISTRATIONCODE);
        input.setValue(secret);
        LOG.info("Checking secret " + secret + " for realm " + context.getRealm().getName() + " and user " + context.getUser().getId());
        return context.getSession().userCredentialManager().isValid(context.getRealm(), context.getUser(), input);
    }
    
    protected void removeAllRegistrationCodes(AuthenticationFlowContext context) {
        RealmModel realm = context.getRealm();
        UserModel user = context.getUser();
        List<CredentialModel> registrationCodesForUser = context.getSession().userCredentialManager().getStoredCredentialsByType(realm, user, RegistrationcodeCredentialProvider.REGISTRATIONCODE);
        for (CredentialModel registrationCode : registrationCodesForUser) {
            context.getSession().userCredentialManager().removeStoredCredential(realm, user, registrationCode.getId());
            LOG.info("Registration Code for realm " + realm.getName() + " and user " + user.getUsername() + " removed.");
        }
        
        
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        //should always be called - will block login if no registrationcode for this user existing
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        //no required actions for registrationcode available - can only be set though REST-API
    }

    @Override
    public void close() {

    }
}
