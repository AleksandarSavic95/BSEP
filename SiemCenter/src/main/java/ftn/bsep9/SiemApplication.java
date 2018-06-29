package ftn.bsep9;

import ftn.bsep9.service.AlarmService;
import ftn.bsep9.service.NotificationService;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class SiemApplication {

    @Autowired
    private AlarmService alarmService;

	private static Logger log = LoggerFactory.getLogger(SiemApplication.class);

    private static String groupId = "ftn.bsep9";
    private static String artifactId = "drools-spring-kjar";
    private static String version = "0.0.1-SNAPSHOT";

	public static void main(String[] args) {
		SpringApplication.run(SiemApplication.class, args);
	}

    /**
     * Creates a KieSession Spring Bean (Singleton) from the KieContainer.
     * @return created KieSession.
     */
    @Bean
    public KieSession kieSession() {
        final KieServices kieServices = KieServices.Factory.get();
        final ReleaseId releaseId = kieServices.newReleaseId(groupId, artifactId, version);
        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        final KieSession kieSession = kieContainer.newKieSession();

        final KieScanner kieScanner = kieServices.newKieScanner(kieContainer);
        kieScanner.start(10000);

        System.out.println("Alarm service setup...");
        kieSession.setGlobal("alarmService", alarmService);

        log.warn(":) created a KIE Container - returning...");
        return kieSession; // sessionName
    }
}
