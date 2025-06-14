package org.baeldung;

import org.baeldung.persistence.dao.pfe.BureauAvocatRepository;
import org.baeldung.persistence.dao.pfe.RdvRepository;
import org.baeldung.persistence.model.pfe.RDV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextListener;

import java.util.Optional;

@SpringBootApplication
@RestController
public class Application extends SpringBootServletInitializer{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	/*
	 * @Override public void run(String... args) throws Exception { BureauAvocat
	 * bureauAvocat=new BureauAvocat(); bureauAvocat.setAdresse("fesss 1233");
	 * bureauAvocat.setNom("avocat");
	 *  bureauAvocatRepository.save(bureauAvocat);
	 * System.err.println("finich");
	 * 
	 * }
	 */
    // Dans une classe de test ou temporairement dans un autre endpoint
    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

}