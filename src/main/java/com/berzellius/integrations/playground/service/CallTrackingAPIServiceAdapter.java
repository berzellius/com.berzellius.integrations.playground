package com.berzellius.integrations.playground.service;

import com.berzellius.integrations.basic.exception.APIAuthException;
import com.berzellius.integrations.playground.dmodel.Call;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by berz on 05.01.2017.
 */
@Service
public interface CallTrackingAPIServiceAdapter {

    public List<Call> getCalls(Date from, Date to, Long startIndex, Integer maxResults) throws APIAuthException;

    public List<Call> getCalls(Date from, Date to, Long startIndex, Integer maxResults, Integer project) throws APIAuthException;

    void processCallOnImport(Call call);

    void setProjects(Integer[] projects);
}
