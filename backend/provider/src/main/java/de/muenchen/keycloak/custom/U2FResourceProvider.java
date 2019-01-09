/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.muenchen.keycloak.custom;

import java.util.List;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import org.jboss.logging.Logger;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import com.yubico.u2f.data.DeviceRegistration;
import com.yubico.u2f.exceptions.U2fBadInputException;
import java.util.LinkedList;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.QueryParam;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialManager;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;


public class U2FResourceProvider implements RealmResourceProvider {

    protected static final Logger logger = Logger.getLogger(U2FResourceProvider.class);
    private final KeycloakSession session;
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
    public DeviceRegistration getNew(@QueryParam("username") String username) throws U2fBadInputException {
        String name = session.getContext().getRealm().getDisplayName();
        if (name == null) {
            name = session.getContext().getRealm().getName();
        }
        logger.info("Realm " + session.getContext().getRealm());
        this.auth = new AppAuthManager().authenticateBearerToken(session, session.getContext().getRealm());

        
        List<DeviceRegistration> devices = getDeviceRegistrations(session.getContext(), username);
        
        if (devices == null) {
            return null;
        }
        
        //FIXME: nur das erste Device wird derzeit zurückgegeben
        for (DeviceRegistration reg : devices) {
            return reg;
        }
        return null;
    }

    @Override
    public void close() {
    }
    
    private List<DeviceRegistration> getDeviceRegistrations(KeycloakContext context, String username) throws U2fBadInputException {
        checkRealmAdmin();
        List<DeviceRegistration> registrations = new LinkedList<>();
        
        RealmModel realmModel = context.getRealm();
//        UserModel userModel = auth.getUser();
        UserCredentialManager ucm = session.userCredentialManager();
//        logger.info("RealmModel " + realmModel + " UserModel " + userModel + " ucm " + ucm + " username " + username);
        
         UserModel userModel = KeycloakModelUtils.findUserByNameOrEmail(session, realmModel, username);
         
         if (userModel == null || realmModel == null || ucm == null) {
             return null;
         }
        
        //was: auth.getUser()
        for (CredentialModel model : ucm.getStoredCredentialsByType(realmModel, userModel, "u2f")) {
            logger.info("Model " + model.getValue());
            registrations.add(DeviceRegistration.fromJson(model.getValue()));
        }
        return registrations;
    }
    
    private void checkRealmAdmin() {
        if (auth == null) {
            throw new NotAuthorizedException("Bearer");
        } 
//        else if (auth.getUser().getRealmRoleMappings() == null || !userHasRole("admin")) {
//            throw new ForbiddenException("Does not have realm admin role");
//        }
    }

    private boolean userHasRole(String role) {
        for (RoleModel roleModel : auth.getUser().getRealmRoleMappings()) {
            if (roleModel.getName().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
