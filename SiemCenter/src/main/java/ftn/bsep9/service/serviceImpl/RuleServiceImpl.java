package ftn.bsep9.service.serviceImpl;

import ftn.bsep9.service.RuleService;
import org.apache.maven.cli.MavenCli;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuleServiceImpl implements RuleService {
    private static final String RULES_PACKAGE = "drools.spring.rules";

    @Autowired
    private KieSession kieSession;

    @Override
    public boolean ruleExists(String ruleContent) {
        return kieSession.getKieBase().getRule(RULES_PACKAGE, getRuleName(ruleContent)) != null;
    }

    @Override
    public void updateRules() {
        MavenCli cli = new MavenCli();
        cli.doMain(new String[]{"clean", "install"}, "drools-spring-kjar", System.out, System.out);
    }

    /**
     * Extracts the rule's name by finding a substring which starts after
     * string <code>rule "</code> and ends at the next <code>"</code> sign.
     *
     * @param ruleString string containing the rule definition
     * @return extracted name of the rule
     */
    @Override
    public String getRuleName(String ruleString) {
        int nameStartIndex = ruleString.indexOf("rule \"") + 6;
        //System.out.println("startIndex: " + nameStartIndex + " end index: " + ruleString.indexOf('"', nameStartIndex));
        //System.out.println("name: " + ruleString.substring(nameStartIndex, ruleString.indexOf('"', nameStartIndex)));
        return ruleString.substring(nameStartIndex, ruleString.indexOf('"', nameStartIndex));
    }
}
