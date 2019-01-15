package de.muenchen.keycloak.custom.registrationcode;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.Produces;
import org.jboss.logging.Logger;
import com.yubico.u2f.exceptions.U2fBadInputException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialManager;
import org.keycloak.models.UserModel;
import org.keycloak.services.resources.admin.AdminAuth;
import org.keycloak.services.resources.admin.AdminRoot;
import org.keycloak.services.resources.admin.permissions.AdminPermissions;


public class RegistrationcodeResourceProvider  extends AdminRoot implements RealmResourceProvider {

    protected static final Logger LOG = Logger.getLogger(RegistrationcodeResourceProvider.class);

    public RegistrationcodeResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return this;
    }

    
    @POST
    @Produces("application/json")
    @Path("{userid}/registrationcode")
    public RegistrationcodeResponse addRegistrationcode(@Context final HttpHeaders headers, @PathParam("userid") String userid,
            RegistrationcodeRequest registrationcode) throws U2fBadInputException {
        LOG.info("Called addRegistrationcode for user with id " + userid);
        
        KeycloakContext context = session.getContext();
        RealmModel targetRealm = context.getRealm(); //schon hier abholen; context.getRealm ändert sich nach Aufruf von authenticateRealmAdminRequest!
        
        //check if current user is authenticated and authorized (checks bearer token)        
        AdminAuth auth = authenticateRealmAdminRequest(headers);
        if (!AdminPermissions.realms(session, auth).isAdmin(targetRealm)) {
            LOG.error("User with given Access Token is not admin for realm " +targetRealm.getName());
            throw new ForbiddenException();
        }
        
        //get all registered U2F devices for user with userid
        String id = registerRegistrationcode(userid, targetRealm, registrationcode.getRegistrationcode());
        RegistrationcodeResponse registrationcodeResponse = new RegistrationcodeResponse();
        registrationcodeResponse.setId(id);
        return registrationcodeResponse;
    }
    
    
    @Override
    public void close() {
    }
    
    
    private String registerRegistrationcode(String userid, RealmModel realmModel, String registrationcode) throws U2fBadInputException {
        
        UserCredentialManager ucm = session.userCredentialManager();
        
        UserModel userModel = session.users().getUserById(userid, realmModel);
         
        if (userModel == null || realmModel == null || ucm == null) {
            LOG.warn("Called registerRegistrationcode with userModel " +userModel+", realmModel "+realmModel+" ucm "+ucm);
            return null;
        }
        
        //first remove all existing registrationcodes (there can only be one!)
        for (CredentialModel model : ucm.getStoredCredentialsByType(realmModel, userModel, RegistrationcodeCredentialProvider.REGISTRATIONCODE)) {
            LOG.info("Removing old registration code with id "+ model.getId());
            ucm.removeStoredCredential(realmModel, userModel, model.getId());
        }
        
        //then add new one
        CredentialModel credentials = new CredentialModel();
        credentials.setType(RegistrationcodeCredentialProvider.REGISTRATIONCODE);
        credentials.setCreatedDate(Time.currentTimeMillis());
        credentials.setValue(registrationcode);
        session.userCredentialManager().createCredential(realmModel, userModel, credentials);
        LOG.info("Added registration code " + registrationcode);

        
        //find out id
        for (CredentialModel model : ucm.getStoredCredentialsByType(realmModel, userModel, RegistrationcodeCredentialProvider.REGISTRATIONCODE)) {
            LOG.info("Returning id for new credential: " + model.getId());
            return model.getId();
        }

        return null;
    }
    
}
