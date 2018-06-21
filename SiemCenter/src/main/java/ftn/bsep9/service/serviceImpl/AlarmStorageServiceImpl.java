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

    /** Relative path to Maven module with rules. */
    private static final String RULES_DIRECTORY = "drools-spring-kjar/src/main/resources/drools/spring/rules/";

    @Override
    public void saveAlarm(AlarmFile alarmFile) {
        // add necessary package declaration and import statements
        alarmFile.setContent("package drools.spring.rules;\n"
                + "dialect  \"mvel\"\n\n"
                + "import ftn.bsep9.model.Log;\n"
                + alarmFile.getContent());
        if (isSafe(alarmFile)) {
            //System.out.println("system property: user.dir: " + System.getProperty("user.dir"));
            //String absPath2 = System.getProperty("user.dir") + "/" + RULES_DIRECTORY + alarmFile.getName();
            //System.out.println("absPath2: " + absPath2);

            // D:\Radim\BSEP\New\BSEP\drools-spring-kjar\src\main\resources\drools\spring\rules
            //String absRulesFilePath = Paths.get(RULES_DIRECTORY).toAbsolutePath().toString() + alarmFile.getName() + ".drl";
            //System.out.println(absRulesFilePath);

            //System.out.println("Paths.get(\"\").toAbsolutePath().toString() + \"/\" + rule..");
            //System.out.println(Paths.get("").toAbsolutePath().toString() + "/" + alarmFile.getName() + ".drl");
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

    private String getRuleName(String ruleString) {
        int nameStartIndex = ruleString.indexOf("rule \"") + 6;
        System.out.println("nameStartIndex: " + nameStartIndex + " " + ruleString.indexOf("\"", nameStartIndex));
        System.out.println("name is: "
                + ruleString.substring(nameStartIndex, ruleString.indexOf("\"", nameStartIndex)));
        return ruleString.substring(nameStartIndex, ruleString.indexOf("\"", nameStartIndex));
    }

    /**
     * Checks if the alarm file is safe to be inserted into the drools project
     * - rule text contains : rule "...
     * - rule with the same name doesn't exist in the KieBase
     * - new file's name does NOT contain /.?!.... and
     * - file with same name as the new file does NOT exist
     */
    private boolean isSafe(AlarmFile alarmFile) {
        String name = alarmFile.getName();
        String rule = alarmFile.getContent();

        // boolean hasRuleNameDef = alarmFile.getContent().contains("rule \""); // not needed after validation
        boolean hasInvalidChars = name.matches(".*[/.?!'|\",\\\\*^%&].*");
        boolean fileExists = Paths.get(RULES_DIRECTORY + name + ".drl").toFile().exists();
        boolean ruleDoesntExist = kieSession.getKieBase().getRule("drools.spring.rules", getRuleName(rule)) == null;
        System.out.println("matches: " + hasInvalidChars);
        System.out.println("file exists: " + fileExists);
        System.out.println("rule exists: " + ruleDoesntExist);

        if (!hasInvalidChars && !fileExists && ruleDoesntExist) {
            VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
            Verifier verifier = vBuilder.newVerifier();
            verifier.addResourcesToVerify(new ReaderResource(new StringReader(rule)), ResourceType.DRL);

            if (verifier.getErrors().size() > 0) {
                System.out.println("This rule has :" + verifier.getErrors().size() + " errors!");
            }

            for (int i = 0; i < verifier.getErrors().size(); i++) {
                System.out.println(verifier.getErrors().get(i).getMessage());
            }
            return verifier.getErrors().size() == 0;
        }

        return false;
    }

    public static void main(String[] args) {
//        try {
//            Runtime.getRuntime().exec("cmd /K mvn -pl drools-spring-kjar");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String[] tests = new String[] {
                "asdf.asdf",
                "asdf/asdf",
                "asdf./asdf",
                "asdf/.asdf",
                "qwe te rt sdfgs dfrrgr",
                "\\"};
        for(String t : tests) {
            System.out.println(t + " " + t.matches(".*[/.?!'|\",\\\\*^%&].*"));
        }
//        MavenCli cli = new MavenCli();
//        cli.doMain(new String[]{"clean", "install"},
//                "../drools-spring-kjar",
//                System.out,
//                System.out);
    }
}
