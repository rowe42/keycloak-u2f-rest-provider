package de.muenchen.keycloak.custom;

import com.yubico.u2f.data.DeviceRegistration;

/**
 *
 * @author roland
 */
public class U2FDevice {
    private String credentialId;
    private Long createdDate;
    private DeviceRegistration deviceRegistration;

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public DeviceRegistration getDeviceRegistration() {
        return deviceRegistration;
    }

    public void setDeviceRegistration(DeviceRegistration deviceRegistration) {
        this.deviceRegistration = deviceRegistration;
    }
}
