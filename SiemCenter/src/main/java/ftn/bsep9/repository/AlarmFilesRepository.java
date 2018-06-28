package ftn.bsep9.repository;

import ftn.bsep9.model.AlarmFile;
import ftn.bsep9.service.RuleService;
import org.drools.core.io.impl.ReaderResource;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.kie.api.io.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AlarmFilesRepository {

    @Autowired
    private RuleService ruleService;

    /** relative path to Maven module with rules */
    private static final String RULES_DIRECTORY = "drools-spring-kjar/src/main/resources/drools/spring/rules/";

    public List<String> getFileNamesAscending() {
        File[] files = new File(RULES_DIRECTORY).listFiles((dir, name) -> name.endsWith(".drl"));
        if (files == null)
            return new ArrayList<>();
        System.out.println("Sorting " + files.length + " files...");
        return Arrays.asList(files).parallelStream().map(File::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
    }

    public boolean fileExists(String name) {
        File file = getRuleFile(name);
        return file.exists();
    }

    public File getRuleFile(String alarmName) {
        return Paths.get(RULES_DIRECTORY + alarmName + ".drl").toFile();
    }

    public AlarmFile saveAlarm(AlarmFile alarmFile, boolean forUpdate) {
        if (isSafe(alarmFile, forUpdate)) {
            String rulePath = RULES_DIRECTORY + alarmFile.getName() + ".drl";
            System.out.println("saving new rule to:" + rulePath + " ....");

            try (PrintStream out = new PrintStream(new FileOutputStream(rulePath))) {
                out.print(alarmFile.getContent());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            ruleService.updateRules();

            return alarmFile;
        } else {
            System.out.println("Cannot create file: " + alarmFile.getName());
            return null;
        }
    }

    /**
     * Checks if the alarm file is safe to be inserted into the Drools project
     * <br/>- new file's name does NOT contain characters /.?\! etc,
     * <br/>- file with same name as the new file does NOT exist,
     * <br/>- rule with the same name doesn't exist in the KieBase
     * <br/>- rule is valid (checked by the rule Verifier)
     */
    private boolean isSafe(AlarmFile alarmFile, boolean forUpdate) {
        String name = alarmFile.getName();
        String rule = alarmFile.getContent();

        boolean hasInvalidChars = name.matches(".*[/.?!'|\",\\\\*^%&].*");
        System.out.println("file name has invalid characters: " + hasInvalidChars);

        if (hasInvalidChars)
            return false;

        boolean fileExists = fileExists(name);
        boolean ruleExists = ruleService.ruleExists(rule);
        System.out.println("file exists: " + fileExists);
        System.out.println("rule exists: " + ruleExists);

        // if a file is being updated, file and rule must exist
        if (forUpdate) {
            if (!fileExists || !ruleExists) {
                return false;
            }
        } else { // if it's being created, then they must not exist
            if (fileExists || ruleExists)
                return false;
        }

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

    public boolean delete(String alarmName) {
        File file = getRuleFile(alarmName);
        boolean deleted = file.exists() && file.delete();
        if (deleted) {
            ruleService.updateRules();
        }
        return deleted;
    }
}
