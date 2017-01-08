package com.berzellius.integrations.playground.config;


import com.berzellius.integrations.calltrackingru.dto.api.errorhandlers.CalltrackingAPIRequestErrorHandler;
import com.berzellius.integrations.playground.service.CallTrackingAPIServiceAdapter;
import com.berzellius.integrations.playground.service.CallTrackingAPIServiceAdapterImpl;
import com.berzellius.integrations.playground.service.CallsImportBatchRunner;
import com.berzellius.integrations.playground.service.CallsImportBatchRunnerImpl;
import com.berzellius.integrations.playground.settings.APISettings;
import com.berzellius.integrations.service.CallTrackingAPIService;
import com.berzellius.integrations.service.CallTrackingAPIServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

/**
 * Created by berz on 20.10.14.
 */
@Configuration
public class ServiceBeanConfiguration {

    @Bean
    CallTrackingAPIService callTrackingAPIService(){
        CallTrackingAPIService callTrackingAPIService = new CallTrackingAPIServiceImpl();
        callTrackingAPIService.setApiMethod(HttpMethod.POST);
        callTrackingAPIService.setApiURL(APISettings.CallTrackingAPIUrl);
        callTrackingAPIService.setLoginURL(APISettings.CallTrackingAPILoginUrl);
        callTrackingAPIService.setLoginMethod(HttpMethod.POST);
        callTrackingAPIService.setLogin(APISettings.CallTrackingLogin);
        callTrackingAPIService.setPassword(APISettings.CallTrackingPassword);
        callTrackingAPIService.setWebSiteLogin(APISettings.CallTrackingWebLogin);
        callTrackingAPIService.setWebSitePassword(APISettings.CallTrackingWebPassword);
        callTrackingAPIService.setWebSiteLoginUrl(APISettings.CallTrackingLoginUrl);
        callTrackingAPIService.setProjects(APISettings.CallTrackingProjects);

        CalltrackingAPIRequestErrorHandler calltrackingAPIRequestErrorHandler = new CalltrackingAPIRequestErrorHandler();
        callTrackingAPIService.setErrorHandler(calltrackingAPIRequestErrorHandler);

        return callTrackingAPIService;
    }

    @Bean
    CallTrackingAPIServiceAdapter callTrackingAPIServiceAdapter(){
        System.out.println("register CallTrackingAPIServiceAdapter!");

        CallTrackingAPIServiceAdapter callTrackingAPIServiceAdapter = new CallTrackingAPIServiceAdapterImpl();
        callTrackingAPIServiceAdapter.setProjects(APISettings.CallTrackingProjects);
        return callTrackingAPIServiceAdapter;
    }

    @Bean
    CallsImportBatchRunner callsImportBatchRunner(){
        CallsImportBatchRunner callsImportBatchRunner = new CallsImportBatchRunnerImpl();
        callsImportBatchRunner.setCallTrackingProjects(APISettings.CallTrackingProjects);

        return callsImportBatchRunner;
    }

}
