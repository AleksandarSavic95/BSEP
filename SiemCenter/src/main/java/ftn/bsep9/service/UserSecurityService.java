package ftn.bsep9.service;

public interface UserSecurityService {
    void saveLoginTry(String username, String ipAddress);

    boolean canUserTryToLogin(String username);

    boolean canIpTryToLogin(String ipAddress);
}
