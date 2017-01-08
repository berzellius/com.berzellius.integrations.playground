import com.berzellius.integrations.playground.TestApplicationCallsImportBatchConfiguration;
import com.berzellius.integrations.playground.repository.CallRepository;
import com.berzellius.integrations.playground.service.CallsImportBatchRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by berz on 06.01.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {TestApplicationCallsImportBatchConfiguration.class, TestBeans.class})
public class TestCallsImportBatchConfiguration {

    @Autowired
    Job callsImportJob;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    CallRepository callRepository;

    @Autowired
    CallsImportBatchRunner callsImportBatchRunner;

    /**
     * Запуск импорта звоноков за большой период времени
     * ВНИМАНИЕ! Не откатывает после себя транзакции
     * @throws JobParametersInvalidException
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     */
    @Test
    public void testRunCallImportAllProjects() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        callsImportBatchRunner.runCallsImport(TestUtil.earlyDate(), TestUtil.lateDate());
    }

    @Test(expected = IllegalArgumentException.class)
     public void testRunCallImportAllProjectsReplacedDates() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        callsImportBatchRunner.runCallsImport(TestUtil.lateDate(), TestUtil.earlyDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunCallImportAllProjectsNullProject() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        callsImportBatchRunner.runCallsImport(TestUtil.earlyDate(), TestUtil.lateDate(), null);
    }


}
