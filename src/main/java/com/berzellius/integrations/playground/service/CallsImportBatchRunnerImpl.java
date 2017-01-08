package com.berzellius.integrations.playground.service;

import com.berzellius.integrations.playground.repository.CallRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by berz on 07.01.2017.
 */
@Service
public class CallsImportBatchRunnerImpl implements CallsImportBatchRunner {

    public static final int maxResultsDefault = 100;

    @Autowired
    Job callsImportJob;

    @Autowired
    CallRepository callRepository;

    @Autowired
    JobLauncher jobLauncher;

    private Integer[] CallTrackingProjects;

    @Override
    public void runCallsImport(Date from, Date to, Integer projectId, Long startIndex, Integer maxResults) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        Assert.notNull(from, "failed on Date from value - must not be null");
        Assert.notNull(to, "failed on Date to value - must not be null");
        Assert.isTrue(from.before(to), "failed on dates - 'from' must be before 'to'");
        Assert.notNull(projectId, "failed on projectId - must not be null");
        Assert.isTrue(startIndex != null && startIndex >= 0, "failed on projectId - must be not null and greater or equal 0");
        Assert.isTrue(maxResults != null && maxResults > 0, "failed on maxResults - must be not null and greater 0");

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addDate("start", new Date());
        jobParametersBuilder.addLong("projectId", projectId.longValue());
        jobParametersBuilder.addLong("startIndex", startIndex);
        jobParametersBuilder.addLong("maxResults", maxResults.longValue());
        jobParametersBuilder.addDate("from", from);
        jobParametersBuilder.addDate("to", to);

        System.out.println("START calls import job!");

        jobLauncher.run(callsImportJob, jobParametersBuilder.toJobParameters());
    }

    @Override
    public void runCallsImport(Date from, Date to, Integer projectId, Long startIndex) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        runCallsImport(from, to, projectId, startIndex, maxResultsDefault);
    }

    @Override
    public void runCallsImport(Date from, Date to, Integer projectId) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        Long startIndex = callRepository.countByProjectIdAndDtGreaterThanEqualAndDtLessThanEqual(projectId, from, to);
        runCallsImport(from, to, projectId, startIndex);
    }

    @Override
    public void runCallsImport(Date from, Date to) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        if(this.getCallTrackingProjects().length == 0){
            throw new IllegalStateException("Empty calltracking projects list - i dont know what to load!");
        }

        for(Integer projectId : this.getCallTrackingProjects()){
            runCallsImport(from, to, projectId);
        }
    }

    @Override
    public void runCallsImport() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        calendar1.setTime(new Date());
        calendar2.setTime(new Date());

        calendar1.set(Calendar.HOUR, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        calendar2.set(Calendar.HOUR, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);

        runCallsImport(calendar1.getTime(), calendar2.getTime());
    }

    public Integer[] getCallTrackingProjects() {
        return CallTrackingProjects;
    }

    @Override
    public void setCallTrackingProjects(Integer[] callTrackingProjects) {
        CallTrackingProjects = callTrackingProjects;
    }
}
