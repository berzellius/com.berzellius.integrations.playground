package com.berzellius.integrations.playground.service;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by berz on 07.01.2017.
 */
@Service
public interface CallsImportBatchRunner {
    /**
     * @param from  - с какого времени импортировать звонки
     * @param to - по какое время
     * @param projectId - для какого projectId
     * @param startIndex - начальное смещение
     * @param maxResults - наибольшее количество строк за 1 запрос
     */
    public void runCallsImport(Date from, Date to, Integer projectId, Long startIndex, Integer maxResults) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException;

    /**
     * @param from  - с какого времени импортировать звонки
     * @param to - по какое время
     * @param projectId - для какого projectId
     * @param startIndex - начальное смещение
     * Со значением maxResults по умолчанию
     */
    public void runCallsImport(Date from, Date to, Integer projectId, Long startIndex) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException;

    /**
     * @param from  - с какого времени импортировать звонки
     * @param to - по какое время
     * @param projectId - для какого projectId
     * C автоопределением startIndex и значением maxResults по умолчанию
     */
    public void runCallsImport(Date from, Date to, Integer projectId) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException;

    /**
     * @param from - с какого времени импортировать звонки
     * @param to - по какое время
     * Для всех projectId с автоопределением startIndex и значением maxResults по умолчанию
     */
    public void runCallsImport(Date from, Date to) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException;

    /**
     * Для всех projectId, для текущей даты с автоопределением startIndex и значением maxResults по умолчанию
     */
    public void runCallsImport() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException;

    void setCallTrackingProjects(Integer[] callTrackingProjects);
}
