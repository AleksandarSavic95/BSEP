package ftn.bsep9;

import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SiemApplication {

	private static Logger log = LoggerFactory.getLogger(SiemApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SiemApplication.class, args);
	}

    /**
     * Creates a KieSession Spring Bean (Singleton) from the KieContainer.
     * @return created KieSession.
     */
    @Bean
    public KieSession kieSession() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.newKieContainer(ks.newReleaseId("ftn.bsep9","drools-spring-kjar", "0.0.1-SNAPSHOT"));
        // https://docs.jboss.org/drools/release/6.2.0.CR3/drools-docs/html/KIEChapter.html#KIEDeployingSection
        KieScanner kScanner = ks.newKieScanner(kContainer);
        kScanner.start(10000);
        log.warn(":) created a KIE Container - returning...");
        return kContainer.newKieSession();
    }
}
