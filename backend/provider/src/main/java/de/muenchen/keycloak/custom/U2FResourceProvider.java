package de.muenchen.keycloak.custom;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import org.jboss.logging.Logger;
import org.keycloak.services.managers.AuthenticationManager;
import com.yubico.u2f.data.DeviceRegistration;
import com.yubico.u2f.exceptions.U2fBadInputException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialManager;
import org.keycloak.models.UserModel;
import org.keycloak.services.resources.admin.AdminAuth;
import org.keycloak.services.resources.admin.AdminRoot;
import org.keycloak.services.resources.admin.permissions.AdminPermissions;


public class U2FResourceProvider  extends AdminRoot implements RealmResourceProvider {

    protected static final Logger LOG = Logger.getLogger(U2FResourceProvider.class);
    private AuthenticationManager.AuthResult auth;

    public U2FResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return this;
    }

    
    @GET
    @Produces("application/json")
    @Path("{userid}/u2f")
    public List<U2FDevice> readU2FDevices(@Context final HttpHeaders headers, @PathParam("userid") String userid) throws U2fBadInputException {
        LOG.info("Called read-u2f for user with id " + userid);
        
        KeycloakContext context = session.getContext();
        RealmModel targetRealm = context.getRealm(); //schon hier abholen; context.getRealm ändert sich nach Aufruf von authenticateRealmAdminRequest!
        
        //check if current user is authenticated and authorized (checks bearer token)        
        AdminAuth auth = authenticateRealmAdminRequest(headers);
        if (!AdminPermissions.realms(session, auth).isAdmin(targetRealm)) {
            LOG.error("User with given Access Token is not admin for realm " +targetRealm.getName());
            throw new ForbiddenException();
        }
        
        //get all registered U2F devices for user with userid
        List<U2FDevice> devices = getDeviceRegistrations(userid, targetRealm);
        
        return devices;
    }
    
    
    @DELETE
    @Path("{userid}/u2f/{credentialId}")
    public void removeU2FDevice(@Context final HttpHeaders headers, @PathParam("userid") String userid, @PathParam("credentialId") String credentialId) {
        LOG.info("Called remove-u2f for user with id " + userid + " and keyHandle " + credentialId);
        
        KeycloakContext context = session.getContext();
        RealmModel targetRealm = context.getRealm(); //schon hier abholen; context.getRealm ändert sich nach Aufruf von authenticateRealmAdminRequest!
        
        //check if current user is authenticated and authorized (checks bearer token)        
        AdminAuth auth = authenticateRealmAdminRequest(headers);
        if (!AdminPermissions.realms(session, auth).isAdmin(targetRealm)) {
            LOG.error("User with given Access Token is not admin for realm " +targetRealm.getName());
            throw new ForbiddenException();
        }
        
        removeDeviceRegistration(userid, credentialId, targetRealm);
        
    }

    @Override
    public void close() {
    }
    
    
    private List<U2FDevice> getDeviceRegistrations(String userid, RealmModel realmModel) throws U2fBadInputException {
        List<U2FDevice> registrations = new ArrayList<>();
        
        UserCredentialManager ucm = session.userCredentialManager();
        
        UserModel userModel = session.users().getUserById(userid, realmModel);
         
         if (userModel == null || realmModel == null || ucm == null) {
             LOG.warn("Called getDeviceRegistrations with userModel " +userModel+", realmModel "+realmModel+" ucm "+ucm);
             return null;
         }
        
        for (CredentialModel model : ucm.getStoredCredentialsByType(realmModel, userModel, "u2f")) {
            LOG.info("Found U2F CredentialModel: " + model.getValue());
            String credentialId = model.getId();
            U2FDevice u2fDevice = new U2FDevice();
            u2fDevice.setCredentialId(credentialId);
            u2fDevice.setDeviceRegistration(DeviceRegistration.fromJson(model.getValue()));
            u2fDevice.setCreatedDate(model.getCreatedDate());
            registrations.add(u2fDevice);
        }
        return registrations;
    }
    
    private boolean removeDeviceRegistration(String userid, String credetialId, RealmModel realmModel) {
        UserCredentialManager ucm = session.userCredentialManager();
        
        UserModel userModel = session.users().getUserById(userid, realmModel);
         
         if (userModel == null || realmModel == null || ucm == null) {
             LOG.warn("Called getDeviceRegistrations with userModel " +userModel+", realmModel "+realmModel+" ucm "+ucm);
             return false;
         }
        
        LOG.info("Removing U2F credential with ID " + credetialId + " for user with userid " + userid + " in Realm " + realmModel.getName());
        boolean success = ucm.removeStoredCredential(realmModel, userModel, credetialId);
        if (!success) {
            LOG.error("Could not remove U2F");
        }
        return success;
        
    }

}
