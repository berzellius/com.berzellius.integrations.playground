package com.berzellius.integrations.playground.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by berz on 06.01.2017.
 */
@Service
@StepScope
public class CallTrackingCallsReaderStepScoped extends CallTrackingCallsReader {

    public CallTrackingCallsReaderStepScoped(){}

    public CallTrackingCallsReaderStepScoped(Date from, Date to, Integer maxResults, Long startIndex){
        super(from, to, maxResults, startIndex);
    }

    @Override
    @Value("#{jobParameters['projectId']}")
    public void setProjectId(Long projectId){
        super.setProjectId(projectId);
    }

    @Override
    @Value("#{jobParameters['start']}")
    public void setStartTest(Date startTest){
        super.setStartTest(startTest);
    }

    @Value("#{jobParameters['maxResults']}")
    public void setMaxResults(Long maxResults){ super.setMaxResults(maxResults.intValue());}

    @Override
    @Value("#{jobParameters['startIndex']}")
    public void setStartIndex(Long startIndex){ super.setStartIndex(startIndex);}

    @Override
    @Value("#{jobParameters['from']}")
    public void setFrom(Date from){ super.setFrom(from);}

    @Override
    @Value("#{jobParameters['to']}")
    public void setTo(Date to){ super.setTo(to);}
}
