package ftn.bsep9;

import org.drools.core.ClockType;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.cdi.KContainer;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@SpringBootApplication
public class SiemApplication {

	private static Logger log = LoggerFactory.getLogger(SiemApplication.class);
	private static String sessionName = "myKSession";
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
//    @Bean
//    public KieSession kieSession() {
//        KieServices ks = KieServices.Factory.get();
//        KieContainer kContainer = ks.newKieContainer(ks.newReleaseId("ftn.bsep9","drools-spring-kjar", "0.0.1-SNAPSHOT"));
//
//        KieBaseConfiguration config = ks.newKieBaseConfiguration();
//        config.setOption(EventProcessingOption.STREAM);
//
//        KieBase kieBase = kContainer.getKieBase();
//        KieSession kSession = kieBase.newKieSession();
//
//        // https://docs.jboss.org/drools/release/6.2.0.CR3/drools-docs/html/KIEChapter.html#KIEDeployingSection
//        KieScanner kScanner = ks.newKieScanner(kContainer);
//        kScanner.start(10000);
//
//        log.warn(":) created a KIE Container - returning...");
//        return kSession;
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

//    @Bean
//    public KieSession kieSession() {
//        KieServices ks = KieServices.Factory.get();
//        KieFileSystem kfs = ks.newKieFileSystem();
//        KieModuleModel kModule = ks.newKieModuleModel();
//
//        KieBaseModel baseModel = kModule.newKieBaseModel("defaultKieBase")
//                .setDefault(true)
//                .setEventProcessingMode(EventProcessingOption.STREAM);
//        baseModel.newKieSessionModel("defaultKSession")
//                .setDefault(true)
//                .setClockType(ClockTypeOption.get("pseudo"));
//
//        kfs.writeKModuleXML(kModule.toXML());
//        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
//        System.out.println("ERRORS: " + kieBuilder.getResults().getMessages( Message.Level.ERROR ).size());
//
//        KieContainer kContainer = ks.newKieContainer(ks.newReleaseId(
//                "ftn.bsep9","drools-spring-kjar", "0.0.1-SNAPSHOT"));
//
//        KieSession kSession = kContainer.newKieSession();
//
//        // https://docs.jboss.org/drools/release/6.2.0.CR3/drools-docs/html/KIEChapter.html#KIEDeployingSection
//        KieScanner kScanner = ks.newKieScanner(kContainer);
//        kScanner.start(10000);
//
//        log.warn(":) created a KIE Container - returning...");
//        return kSession;
//    }
//    @Bean
//    public KieSession kieSession() {
//        KieServices ks = KieServices.Factory.get();
//
////        KieFileSystem kfs = ks.newKieFileSystem();
////        kfs.write(ResourceFactory.newClassPathResource("cep2/heart-monitor-rules.drl"));
////        KieBuilder kbuilder = ks.newKieBuilder(kfs);
////        kbuilder.buildAll();
////        if (kbuilder.getResults().hasMessages(Message.Level.ERROR)) {
////            throw new IllegalArgumentException("Coudln't build knowledge module" + kbuilder.getResults());
////        }
////        KieContainer kContainer = ks.newKieContainer(kbuilder.getKieModule().getReleaseId());
//
//        KieContainer kContainer = ks.newKieContainer(ks.newReleaseId(
//                "ftn.bsep9","drools-spring-kjar", "0.0.1-SNAPSHOT"));
//        KieBaseConfiguration kbConf = ks.newKieBaseConfiguration();
//        kbConf.setOption(EventProcessingOption.STREAM);
//        KieBase kBase = kContainer.newKieBase(kbConf);
//
//        KieSessionConfiguration ksConf1 = ks.newKieSessionConfiguration();
//        ksConf1.setOption(ClockTypeOption.get(ClockType.REALTIME_CLOCK.getId()));
//        KieSession kSession1 = kBase.newKieSession(ksConf1, null);
//
////        KieSession kSession = kContainer.newKieSession();
//
//        // https://docs.jboss.org/drools/release/6.2.0.CR3/drools-docs/html/KIEChapter.html#KIEDeployingSection
//        KieScanner kScanner = ks.newKieScanner(kContainer);
//        kScanner.start(10000);
//
//        log.warn(":) created a KIE Container - returning...");
//        return kSession1;
//    }
//    @Bean
//    public KieSession kieSession() {
//        KieServices ks = KieServices.Factory.get();
//        KieFileSystem kfs = ks.newKieFileSystem();
//        KieModuleModel kmodule = ks.newKieModuleModel();
//
//        KieBaseModel baseModel = kmodule.newKieBaseModel("defaultKBase")
//                .setDefault(true)
//                .setEventProcessingMode(EventProcessingOption.STREAM);
//        baseModel.newKieSessionModel("defaultKSession")
//                .setDefault(true)
//                .setClockType(ClockTypeOption.get("pseudo"));
//
//        kfs.writeKModuleXML(kmodule.toXML());
////        kfs.write(ks.getResources().newClassPathResource("calendar_timer.xls", this.getClass())); // README when path is set then test works
//        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
////        assertEquals( 0, kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );
//
//        System.out.println("\n\nREPO I RELEASEid OD REPOA");
//        System.out.println(ks.getRepository());
//        System.out.println(ks.getRepository().getDefaultReleaseId());
//        KieContainer kContainer = ks.newKieContainer(ks.newReleaseId("ftn.bsep9","drools-spring-kjar", "0.0.1-SNAPSHOT"));
//        KieBaseConfiguration kbc = ks.newKieBaseConfiguration();
//        kbc.setOption(EventProcessingOption.STREAM);
//        KieBase kb = kContainer.newKieBase(kbc);
//        KieSession ksession = kb.newKieSession();
////        KieSession ksession = kContainer.newKieSession();
//
//        // https://docs.jboss.org/drools/release/6.2.0.CR3/drools-docs/html/KIEChapter.html#KIEDeployingSection
//        KieScanner kScanner = ks.newKieScanner(kContainer);
//        kScanner.start(10000);
//
//        log.warn(":) created a KIE Container - returning...");
//        return ksession;
//    }
    @Bean
    public KieSession kieSession() {
        final KieServices kieServices = KieServices.Factory.get();
        final ReleaseId releaseId = kieServices.newReleaseId(groupId, artifactId, version);
        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        final KieSession kieSession = kieContainer.newKieSession();

        final KieScanner kieScanner = kieServices.newKieScanner(kieContainer);
        kieScanner.start(10000);

        log.warn(":) created a KIE Container - returning...");
        return kieSession; // sessionName
    }
}
