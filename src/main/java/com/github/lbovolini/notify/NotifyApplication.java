package com.github.lbovolini.notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;

import javax.validation.Validator;
import java.util.Locale;

// !todo mover beans
@SpringBootApplication
public class NotifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotifyApplication.class, args);
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

		// !todo
		messageSource.setBasename("classpath:lang/messages");
		//messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setDefaultEncoding("UTF-8");

		return messageSource;
	}

	@Bean
	public LocalValidatorFactoryBean getValidator() {
		LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
		bean.setValidationMessageSource(messageSource());

		return bean;
	}

	@Bean
	public HttpHandler httpHandler(ApplicationContext context) {

		AcceptHeaderLocaleContextResolver localeContextResolver = new AcceptHeaderLocaleContextResolver();
		// !todo mudar pra en
		localeContextResolver.setDefaultLocale(Locale.forLanguageTag("pt-BR"));

		return WebHttpHandlerBuilder.applicationContext(context)
				.localeContextResolver(localeContextResolver)
				.build();
	}

	@Bean
	@Primary
	public Validator springValidator() {
		return getValidator();
	}

}
