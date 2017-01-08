package com.berzellius.integrations.playground.reader;

import com.berzellius.integrations.playground.dmodel.Call;
import com.berzellius.integrations.playground.service.CallTrackingAPIServiceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by berz on 27.09.2015.
 */
@Service
public class CallTrackingCallsReader implements ItemReader<List<Call>> {

    private static final Logger log = LoggerFactory.getLogger(CallTrackingAPIServiceAdapter.class);

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Autowired
    private CallTrackingAPIServiceAdapter callTrackingAPIServiceAdapter;

    private Long startIndex;

    private Integer maxResults;

    private Date from;

    private Date to;

    private Date startTest;

    private Long projectId;

    public CallTrackingCallsReader(){}

    public CallTrackingCallsReader(Date from, Date to, Integer maxResults, Long startIndex){
        this.setFrom(from);
        this.setTo(to);
        this.setMaxResults(maxResults);
        this.setStartIndex(startIndex);
    }

    @Override
    public List<Call> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        log.info("Run with parameters: { dateFrom: " + this.getFrom() + ", dateTo: " + this.getTo() +
                ", startIndex: " + this.getStartIndex() + ", maxResults:" + this.getMaxResults() + "}");


        log.info("projectId: " + this.getProjectId());

        List<Call> calls = (this.getProjectId() == null)?
                callTrackingAPIServiceAdapter.getCalls(
                    this.getFrom(),
                    this.getTo(),
                    this.getStartIndex(),
                    this.getMaxResults()
                )
                    :
                callTrackingAPIServiceAdapter.getCalls(
                        this.getFrom(),
                        this.getTo(),
                        this.getStartIndex(),
                        this.getMaxResults(),
                        this.getProjectId().intValue()
                );

        System.out.println("read calls: " + calls);

        if(calls.size() > 0) {
            this.setStartIndex(this.getStartIndex() + calls.size());
            return calls;
        }
        else {
            return null;
        }
    }

    private void setDatesNow() {
        Date dt = new Date();
        System.out.println("updated dates: " + dt);
        this.setFrom(dt);
        this.setTo(dt);
    }

    public Long getStartIndex() {
        return this.startIndex;
    }

    public void setStartIndex(Long startIndex) {
        System.out.println("setting start index :".concat(startIndex.toString()));
        this.startIndex = startIndex;
    }

    public Integer getMaxResults() {
        return this.maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public Date getFrom() {
        return this.from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return this.to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Date getStartTest() {
        return startTest;
    }

    public void setStartTest(Date startTest) {
        this.startTest = startTest;
    }

}
