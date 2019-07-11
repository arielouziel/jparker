package com.aouziel.jparker.swagger;

import com.aouziel.jparker.model.HourRatePlusFixedPricingPolicy;
import com.aouziel.jparker.model.HourRatePricingPolicy;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {
	@Autowired
	private TypeResolver typeResolver;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
					.apis(RequestHandlerSelectors
						.basePackage("com.aouziel.jparker.controller"))
					.paths(PathSelectors.regex("/.*"))
					.build()
				.apiInfo(apiEndPointsInfo())
				.additionalModels(
						typeResolver.resolve(HourRatePricingPolicy.class),
						typeResolver.resolve(HourRatePlusFixedPricingPolicy.class)
				);
	}

	private ApiInfo apiEndPointsInfo() {

		return new ApiInfoBuilder().title("JParker REST API")
				.description("An Open API for parking lot management")
				.contact(new Contact("Ariel Ouziel", "www.arielouziel.com", "contact@arielouziel.com"))
				.license("Apache 2.0")
				.licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
				.version("1.0.0")
				.build();
	}
}
