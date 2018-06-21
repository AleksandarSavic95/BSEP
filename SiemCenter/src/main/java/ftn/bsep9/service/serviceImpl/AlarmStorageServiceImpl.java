package ftn.bsep9.service.serviceImpl;

import ftn.bsep9.model.AlarmFile;
import ftn.bsep9.service.AlarmStorageService;
import org.apache.maven.cli.MavenCli;
import org.drools.core.io.impl.ReaderResource;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;

@Service
public class AlarmStorageServiceImpl implements AlarmStorageService {

    @Autowired
    private KieSession kieSession;

    /** relative path to Maven module with rules */
    private static final String RULES_DIRECTORY = "drools-spring-kjar/src/main/resources/drools/spring/rules/";

    @Override
    public void saveAlarm(AlarmFile alarmFile) {
        // add necessary package declaration and import statements
        alarmFile.setContent("package drools.spring.rules;\n"
                + "dialect  \"mvel\"\n\n"
                + "import ftn.bsep9.model.Log;\n"
                + alarmFile.getContent());
        if (isSafe(alarmFile)) {
            String rulePath = RULES_DIRECTORY + alarmFile.getName() + ".drl";
            System.out.println("saving new rule to:" + rulePath + " ....");

            try (PrintStream out = new PrintStream(new FileOutputStream(rulePath))) {
                out.print(alarmFile.getContent());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            MavenCli cli = new MavenCli();
            cli.doMain(new String[]{"clean", "install"}, "drools-spring-kjar", System.out, System.out);
        }
        else
            System.out.println("Cannot create file: " + alarmFile.getName());
    }

    /**
     * Extracts the rule's name by finding a substring which starts after
     * string <code>rule "</code> and ends at the next <code>"</code> sign.
     * @param ruleString string containing the rule definition
     * @return extracted name of the rule
     */
    private String getRuleName(String ruleString) {
        int nameStartIndex = ruleString.indexOf("rule \"") + 6;
        return ruleString.substring(nameStartIndex, ruleString.indexOf("\"", nameStartIndex));
    }

    /**
     * Checks if the alarm file is safe to be inserted into the Drools project
     * <br/>- new file's name does NOT contain characters /.?\! etc,
     * <br/>- file with same name as the new file does NOT exist,
     * <br/>- rule with the same name doesn't exist in the KieBase
     * <br/>- rule is valid (checked by the rule Verifier)
     */
    private boolean isSafe(AlarmFile alarmFile) {
        String name = alarmFile.getName();
        String rule = alarmFile.getContent();

        boolean hasInvalidChars = name.matches(".*[/.?!'|\",\\\\*^%&].*");
        System.out.println("file name has invalid characters: " + hasInvalidChars);

        if (hasInvalidChars)
            return false;

        boolean fileExists = Paths.get(RULES_DIRECTORY + name + ".drl").toFile().exists();
        boolean ruleExists = kieSession.getKieBase().getRule("drools.spring.rules", getRuleName(rule)) != null;
        System.out.println("file exists: " + fileExists);
        System.out.println("rule exists: " + ruleExists);

        if (fileExists || ruleExists)
            return false;

        // verify rule's syntax
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        Verifier verifier = vBuilder.newVerifier();
        verifier.addResourcesToVerify(new ReaderResource(new StringReader(rule)), ResourceType.DRL);

        System.out.println(String.format("Rule has %s errors:", verifier.getErrors().size()));

        for (int i = 0; i < verifier.getErrors().size(); i++) {
            System.out.println(verifier.getErrors().get(i).getMessage());
        }
        return verifier.getErrors().size() == 0;
    }
}
