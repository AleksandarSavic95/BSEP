package ftn.bsep9.service.serviceImpl;

import ftn.bsep9.model.LoginTry;
import ftn.bsep9.service.UserSecurityService;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserSecurityServiceImpl implements UserSecurityService {
    private final String AGENDA_GROUP_NAME = "USER-SECURITY";
    private final String CAN_USER_LOGIN_QUERY = "can user login";
    private final String CAN_IP_LOGIN_QUERY = "can IP login";

    @Autowired
    private KieSession kieSession;

    @Override
    public void saveLoginTry(String username, String ipAddress) {
        kieSession.insert(new LoginTry(username, ipAddress, new Date()));
        kieSession.getAgenda().getAgendaGroup(AGENDA_GROUP_NAME).setFocus();
        // suppress running rules from default agenda group
        kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter(AGENDA_GROUP_NAME));
    }

    @Override
    public boolean canUserTryToLogin(String username) {
        QueryResults results = kieSession.getQueryResults(CAN_USER_LOGIN_QUERY, username);
        System.out.println("Username results: " + results.size());
        for (QueryResultsRow result : results) {
            System.out.println(result.get("$forbidden"));
        }
        return results.size() == 0;
    }

    @Override
    public boolean canIpTryToLogin(String ipAddress) {
        QueryResults results = kieSession.getQueryResults(CAN_IP_LOGIN_QUERY, ipAddress);
        System.out.println("IP results: " + results.size());
        return results.size() == 0;
    }
}
