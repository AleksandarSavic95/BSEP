package ftn.bsep9.service;

public interface RuleService {

    boolean ruleExists(String ruleContent);

    void updateRules();

    String getRuleName(String ruleString);
}
