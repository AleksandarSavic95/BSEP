package ftn.bsep9;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

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
//    @Bean
//    public KieSession kieSession() {
//        KieServices ks = KieServices.Factory.get();
//        KieContainer kContainer = ks.newKieContainer(ks.newReleaseId("ftn.bsep9","drools-spring-kjar", "0.0.1-SNAPSHOT"));
//
//        // https://docs.jboss.org/drools/release/6.2.0.CR3/drools-docs/html/KIEChapter.html#KIEDeployingSection
//        KieScanner kScanner = ks.newKieScanner(kContainer);
//        kScanner.start(10000);
//
//        KieBaseConfiguration config = ks.newKieBaseConfiguration();
//        config.setOption(EventProcessingOption.STREAM);
//
//        KieBase kieBase = kContainer.newKieBase( config );
//        log.warn(":) created a KIE Container - returning...");
//        return kieBase.newKieSession();
//    }
//    @Bean
//    public KieSession kieSession() {
//        KieServices ks = KieServices.Factory.get();
//        KieContainer kContainer = ks.newKieContainer(ks.newReleaseId("ftn.bsep9","drools-spring-kjar", "0.0.1-SNAPSHOT"));
//
//        KieBaseConfiguration config = ks.newKieBaseConfiguration();
//        config.setOption(EventProcessingOption.STREAM);
//
//        KieBase kieBase = kContainer.newKieBase( config );
//
//
//        // https://docs.jboss.org/drools/release/6.2.0.CR3/drools-docs/html/KIEChapter.html#KIEDeployingSection
//        KieScanner kScanner = ks.newKieScanner(kContainer);
//        kScanner.start(10000);
//
//        log.warn(":) created a KIE Container - returning...");
//        return kieBase.newKieSession();
//    }

    @Bean
    public KieSession kieSession() {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kmodule = ks.newKieModuleModel();

        KieBaseModel baseModel = kmodule.newKieBaseModel("defaultKBase")
                .setDefault(true)
                .setEventProcessingMode(EventProcessingOption.STREAM);
        baseModel.newKieSessionModel("defaultKSession")
                .setDefault(true)
                .setClockType(ClockTypeOption.get("pseudo")); // COMMENT THIS!!!

        kfs.writeKModuleXML(kmodule.toXML());
//        kfs.write(ks.getResources().newClassPathResource("calendar_timer.xls", this.getClass())); // README when path is set then test works
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        System.out.println("ERRORS: " + kieBuilder.getResults().getMessages( Message.Level.ERROR ).size());

        KieContainer kContainer = // ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
            ks.newKieContainer(ks.newReleaseId("ftn.bsep9","drools-spring-kjar", "0.0.1-SNAPSHOT"));

        KieSession kSession = kContainer.newKieSession();


        // https://docs.jboss.org/drools/release/6.2.0.CR3/drools-docs/html/KIEChapter.html#KIEDeployingSection
        KieScanner kScanner = ks.newKieScanner(kContainer);
        kScanner.start(10000);

        log.warn(":) created a KIE Container - returning...");
        return kSession;
    }
}
