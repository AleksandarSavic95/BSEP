package ftn.bsep9.model;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import java.util.Date;

@Role(Role.Type.EVENT) // Role.Type.FACT ???
@Timestamp("dateTime")
@Expires("1h") // TODO: IMPORTANT: Check if 1 hour is ok !!!!
public class LoginTry {
    private String username;
    private String ipAddress;
    private Date dateTime;

    public LoginTry() {
    }

    public LoginTry(String username, String ipAddress, Date dateTime) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.dateTime = dateTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}
