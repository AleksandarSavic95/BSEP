package ftn.bsep9;

import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class SiemApplication {

	private static Logger log = LoggerFactory.getLogger(SiemApplication.class);

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(SiemApplication.class, args);

		String[] beanNames = ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);

		StringBuilder sb = new StringBuilder("Application beans:\n");
		for (String beanName : beanNames) {
			sb.append(beanName);
            sb.append("\n");
		}
		log.info(sb.toString());
	}

	@Bean
	public KieContainer kieContainer() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.newKieContainer(ks.newReleaseId("drools-spring-siem","drools-spring-kjar", "0.0.1-SNAPSHOT"));
		KieScanner kScanner = ks.newKieScanner(kContainer);
		kScanner.start(10_000);
		return kContainer;
	}

	/**
	 *   M Y   T R Y   - not tested
	 * Creates KieSession from the KieContainer Bean.
	 * @return created KieSession.
	 */
	@Bean
	public KieSession kieSession() {
		return kieContainer().newKieSession("AutowiredKieSession");
	}
}
