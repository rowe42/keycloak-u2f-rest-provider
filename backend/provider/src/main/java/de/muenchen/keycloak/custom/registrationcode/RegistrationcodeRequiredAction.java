package de.muenchen.keycloak.custom.registrationcode;

import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.UserCredentialModel;

import javax.ws.rs.core.Response;

/**
 * @author rowe42
 * @version $Revision: 1 $
 */
public class RegistrationcodeRequiredAction implements RequiredActionProvider {
    public static final String PROVIDER_ID = "registrationcode_config";

    @Override
    public void evaluateTriggers(RequiredActionContext context) {

    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        Response challenge = context.form().createForm("registrationcode-config.ftl");
        context.challenge(challenge);

    }

    @Override
    public void processAction(RequiredActionContext context) {
        String answer = (context.getHttpRequest().getDecodedFormParameters().getFirst("secret_answer"));
        UserCredentialModel input = new UserCredentialModel();
        input.setType(RegistrationcodeCredentialProvider.REGISTRATIONCODE);
        input.setValue(answer);
        context.getSession().userCredentialManager().updateCredential(context.getRealm(), context.getUser(), input);
        context.success();
    }

    @Override
    public void close() {

    }
}
