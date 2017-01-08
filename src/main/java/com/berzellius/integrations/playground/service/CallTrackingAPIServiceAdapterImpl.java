package com.berzellius.integrations.playground.service;

import com.berzellius.integrations.basic.exception.APIAuthException;
import com.berzellius.integrations.playground.dmodel.Call;
import com.berzellius.integrations.service.CallTrackingAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 05.01.2017.
 */
@Service
public class CallTrackingAPIServiceAdapterImpl implements CallTrackingAPIServiceAdapter {


    private static final Logger log = LoggerFactory.getLogger(CallTrackingAPIServiceAdapter.class);

    private Integer[] projects = {};

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CallTrackingAPIService callTrackingAPIService;

    @Override
    public List<Call> getCalls(Date from, Date to, Long startIndex, Integer maxResults) throws APIAuthException {
        List<com.berzellius.integrations.calltrackingru.dto.api.calltracking.Call> calls1 =
                callTrackingAPIService.getCalls(from, to, startIndex, maxResults);

        return this.adaptCalls(calls1);
    }

    @Override
    public List<Call> getCalls(Date from, Date to, Long startIndex, Integer maxResults, Integer project) throws APIAuthException {
        List<com.berzellius.integrations.calltrackingru.dto.api.calltracking.Call> calls1 =
                callTrackingAPIService.getCalls(from, to, startIndex, maxResults, project);

        return this.adaptCalls(calls1);
    }

    private List<Call> adaptCalls(List<com.berzellius.integrations.calltrackingru.dto.api.calltracking.Call> calls1){
        List<Call> calls = new ArrayList<>();

        if(calls1 == null)
            throw new IllegalStateException("no calls loaded!");

        for(
                com.berzellius.integrations.calltrackingru.dto.api.calltracking.Call call :
                calls1
                ){
            Call call1 = this.adapt(call);
            calls.add(call1);
        }

        return calls;
    }

    private Call adapt(com.berzellius.integrations.calltrackingru.dto.api.calltracking.Call call) {

        Call call1 = new Call();

        call1.setNumber(call.getNumber());
        call1.setDt(call.getDt());
        call1.setProjectId(call.getProjectId());
        call1.setParams(call.getParams());
        call1.setState(call.getState());
        call1.setSource(call.getSource());
        call1.setStatus(call1.getStatus());

        return call1;
    }

    @Override
    public void processCallOnImport(Call call) {
        log.info("processing call on import..");
        call.setDtmCreate(new Date());

        if(call.getNumber() != null){
            if(call.getNumber().length() >= 11 &&
                    (
                            call.getNumber().charAt(0) == '7'
                    )){
                call.setNumber(call.getNumber().substring(1));
            }
        }
    }

    public Integer[] getProjects() {
        return projects;
    }

    @Override
    public void setProjects(Integer[] projects) {
        this.projects = projects;
    }
}
