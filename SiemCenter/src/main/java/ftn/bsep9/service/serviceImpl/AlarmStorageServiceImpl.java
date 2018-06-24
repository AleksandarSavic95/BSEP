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
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
public class AlarmStorageServiceImpl implements AlarmStorageService {

    @Autowired
    private KieSession kieSession;

    /** relative path to Maven module with rules */
    private static final String RULES_DIRECTORY = "drools-spring-kjar/src/main/resources/drools/spring/rules/";
    private static final String RULES_PACKAGE = "drools.spring.rules";

    @Override
    public AlarmFile create(AlarmFile alarmFile) {
        return saveAlarm(alarmFile, false);
    }

    @Override
    public AlarmFile get(String alarmName) {
        File file = Paths.get(RULES_DIRECTORY + alarmName + ".drl").toFile();
        if (!file.exists()) {
            return null;
        }
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = new String(encoded, StandardCharsets.UTF_8);
        return new AlarmFile(alarmName, content);
    }

    @Override
    public AlarmFile update(String alarmName, AlarmFile updatedFile) {
        if (!updatedFile.getName().equals(alarmName)) { // sanity check
            return null;
        }
        if (!fileExists(alarmName))
            return null;
        return saveAlarm(updatedFile, true);
    }

    @Override
    public boolean delete(String alarmName) {
        File file = Paths.get(RULES_DIRECTORY + alarmName + ".drl").toFile();
        boolean deleted = file.exists() && file.delete();
        if (deleted) {
            updateRules();
        }
        return deleted;
    }

    @Override
    public PagedListHolder<String> findAllWithPages(int page, int size, String sortDirection) {

        // TODO: get list of file names here! // what happens after we make endpoint/folder for siem's users' rules?

        PagedListHolder<String> pagedListHolder = new PagedListHolder<String>(new ArrayList<String>());
        pagedListHolder.setPage(page);
        pagedListHolder.setPageSize(size);
        return pagedListHolder;
    }

    private AlarmFile saveAlarm(AlarmFile alarmFile, boolean forUpdate) {
        // add necessary package declaration and import statements
        alarmFile.setContent("package " + RULES_PACKAGE + ";\n"
                + "dialect  \"mvel\"\n\n"
                + "import ftn.bsep9.model.Log;\n"
                + alarmFile.getContent());
        if (isSafe(alarmFile, forUpdate)) {
            String rulePath = RULES_DIRECTORY + alarmFile.getName() + ".drl";
            System.out.println("saving new rule to:" + rulePath + " ....");

            try (PrintStream out = new PrintStream(new FileOutputStream(rulePath))) {
                out.print(alarmFile.getContent());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            updateRules();

            return alarmFile;
        }
        else {
            System.out.println("Cannot create file: " + alarmFile.getName());
            return null;
        }
    }

    private void updateRules() {
        MavenCli cli = new MavenCli();
        cli.doMain(new String[]{"clean", "install"}, "drools-spring-kjar", System.out, System.out);
    }

    private boolean fileExists(String name) {
        File file = Paths.get(RULES_DIRECTORY + name + ".drl").toFile();
        return file.exists();
    }

    /**
     * Extracts the rule's name by finding a substring which starts after
     * string <code>rule "</code> and ends at the next <code>"</code> sign.
     * @param ruleString string containing the rule definition
     * @return extracted name of the rule
     */
    private String getRuleName(String ruleString) {
        int nameStartIndex = ruleString.indexOf("rule \"") + 6;
        System.out.println("startIndex: " + nameStartIndex + " end index: " + ruleString.indexOf("\"", nameStartIndex));
        System.out.println("name: " + ruleString.substring(nameStartIndex, ruleString.indexOf("\"", nameStartIndex)));
        return ruleString.substring(nameStartIndex, ruleString.indexOf("\"", nameStartIndex));
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
        boolean ruleExists = kieSession.getKieBase().getRule(RULES_PACKAGE, getRuleName(rule)) != null;
        System.out.println("file exists: " + fileExists);
        System.out.println("rule exists: " + ruleExists);

        // if a file is being updated, file and rule must exist
        if (forUpdate) {
            if (!fileExists || !ruleExists) {
                return false;
            }
        }
        else { // if it's being created, then they must not exist
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
}
