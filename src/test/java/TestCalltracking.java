import com.berzellius.integrations.basic.exception.APIAuthException;
import com.berzellius.integrations.calltrackingru.dto.api.calltracking.CallTrackingSourceCondition;
import com.berzellius.integrations.playground.TestApplication;
import com.berzellius.integrations.playground.dmodel.Call;
import com.berzellius.integrations.playground.reader.CallTrackingCallsReader;
import com.berzellius.integrations.playground.repository.CallRepository;
import com.berzellius.integrations.playground.service.CallTrackingAPIServiceAdapter;
import com.berzellius.integrations.playground.settings.APISettings;
import com.berzellius.integrations.service.CallTrackingAPIService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 03.01.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {TestApplication.class, TestBeans.class})
public class TestCalltracking {

    @Autowired
    private CallTrackingAPIService callTrackingAPIService;

    @Autowired
    private CallTrackingAPIServiceAdapter callTrackingAPIServiceAdapter;

    @Autowired
    private CallTrackingCallsReader callTrackingCallsReader;

    @Autowired
    private CallRepository callRepository;

    private Integer defaultCalltrackingProject = APISettings.CallTrackingProjects[0];

    private Integer reserveCalltrackingProject = APISettings.CallTrackingProjects[1];

    public void testGetMarketingChannels() throws APIAuthException {
        List<CallTrackingSourceCondition> callTrackingSourceConditions = callTrackingAPIService.getAllMarketingChannelsFromCalltracking();

        if(callTrackingSourceConditions == null){
            throw new IllegalStateException("null source conditions!");
        }

        System.out.println(callTrackingSourceConditions.size() + " condition objects");
        for(CallTrackingSourceCondition callTrackingSourceCondition : callTrackingSourceConditions){
            System.out.println(
                    "#" + callTrackingSourceCondition.getProjectId() + ": " +
                            callTrackingSourceCondition.getSourceName() + " -> " +
                            callTrackingSourceCondition.getUtmSource() + ", " +
                            callTrackingSourceCondition.getUtmMedium() + ", " +
                            callTrackingSourceCondition.getUtmCampaign()
            );
        }
    }

    @Test
    public void testGetCalls() throws APIAuthException {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        //calendar1.setTime(new Date());
        calendar2.setTime(new Date());

        calendar1.set(Calendar.YEAR, 2016);
        calendar1.set(Calendar.MONTH, 10);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);

        calendar1.set(Calendar.HOUR, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        calendar2.set(Calendar.HOUR, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);

        System.out.println("from " + calendar1.getTime() + "; to " + calendar2.getTime());

        Integer project = this.getDefaultCalltrackingProject();

        List<Call> calls = callTrackingAPIServiceAdapter.getCalls(calendar1.getTime(), calendar2.getTime(), 0l, 100, project);

        if(calls == null){
            throw new IllegalStateException("null calls!");
        }

        System.out.println(calls.size() + " calls");
    }

    /**
     * Тест проверяет работоспособность класса CallTrackingCallsReader
     * @throws Exception
     */
    @Test
    @Transactional
    public void testCalltrackingCallsReader() throws Exception {

        List<Call> calls = callTrackingCallsReader.read();

        if(calls == null){
            throw new IllegalStateException("null calls!");
        }

        System.out.println(calls.size() + " calls");

        Long size1 = callRepository.count();

        if(calls.size() > 0){
            callRepository.save(calls);
        }

        Long size2 = callRepository.count();
        System.out.println("size: " + size2);

        System.out.println("count calls in db before/after: " + size1 + "/" + size2);

        Long delta = size2 - size1;
        System.out.println("checking " + delta + " = " + calls.size());

        Assert.isTrue(delta == calls.size());
    }


    /**
     * Тест, проверяющий 2 шага чтения данных
     * Перед вторым шагом обновляем startIndex, делаем его равным количеству загруженных на первом шаге звонков
     * В конце проверяем, что не повторяющихся данных
     * @throws Exception
     */
    @Test
    @Transactional
    public void testCalltrackingGetCallsTwoSteps() throws Exception {
        callTrackingCallsReader.setFrom(TestUtil.earlyDate());
        callTrackingCallsReader.setTo(TestUtil.lateDate());
        Long loadedCount1 = callRepository
                .countByProjectIdAndDtGreaterThanEqualAndDtLessThanEqual(this.getDefaultCalltrackingProject(), TestUtil.earlyDate(), TestUtil.lateDate());
        System.out.println("before 1 step loaded: " + loadedCount1);
        callTrackingCallsReader.setStartIndex(loadedCount1);
        callTrackingCallsReader.setMaxResults(5);
        callTrackingCallsReader.setProjectId(this.getDefaultCalltrackingProject().longValue());

        List<Call> calls1 = callTrackingCallsReader.read();
        callRepository.save(calls1);

        Long loadedCount2 = callRepository
                .countByProjectIdAndDtGreaterThanEqualAndDtLessThanEqual(this.getDefaultCalltrackingProject(), TestUtil.earlyDate(), TestUtil.lateDate());
        System.out.println("before 2 step loaded: " + loadedCount2);
        callTrackingCallsReader.setStartIndex(loadedCount2);
        List<Call> calls2 = callTrackingCallsReader.read();

        callRepository.save(calls2);

        boolean equal = this.checkCallListsEquals(calls1, calls2);

        System.out.println("are two call lists loaded from two steps equals? Answer: " + equal);

        // Если есть повторяющиеся данные, значит startIndex не переназначился
        if(equal){
            throw new IllegalStateException("loaded 2 equal data blocks!");
        }
    }

    /**
     * Тест проверяет чтение данных в 2 шага без переустановки startIndex
     * В результате должны быть загружены одинаковые блоки данных на 2х шагах
     * и при записи в базу должно вызываться DataIntegrityViolationException
     * @throws Exception
     */
    @Test(expected = DataIntegrityViolationException.class)
    @Transactional
    public void testCalltrackingGetCallsTwoStepsWithSameStartIndex() throws Exception {
        callTrackingCallsReader.setFrom(TestUtil.earlyDate());
        callTrackingCallsReader.setTo(TestUtil.lateDate());
        Long loadedCount1 = callRepository
                .countByProjectIdAndDtGreaterThanEqualAndDtLessThanEqual(this.getDefaultCalltrackingProject(), TestUtil.earlyDate(), TestUtil.lateDate());
        System.out.println("before 1 step loaded: " + loadedCount1);
        callTrackingCallsReader.setStartIndex(loadedCount1);
        callTrackingCallsReader.setMaxResults(5);
        callTrackingCallsReader.setProjectId(this.getDefaultCalltrackingProject().longValue());

        List<Call> calls1 = callTrackingCallsReader.read();
        callRepository.save(calls1);

        Long loadedCount2 = callRepository
                .countByProjectIdAndDtGreaterThanEqualAndDtLessThanEqual(this.getDefaultCalltrackingProject(), TestUtil.earlyDate(), TestUtil.lateDate());
        System.out.println("before 2 step loaded: " + loadedCount2);

        // Ставим тот же startIndex, что был на 1м шаге
        callTrackingCallsReader.setStartIndex(loadedCount1);
        List<Call> calls2 = callTrackingCallsReader.read();

        callRepository.save(calls2);

        // Читаем данные из callRepository, чтобы гарантировать commit транзакции
        List<Call> calls = (List<Call>) callRepository.findAll();
        for(Call call : calls){
            System.out.println("number: " + call.getNumber() + ", dt: " + call.getDt() + ", projectId: " + call.getProjectId() + ", duplicateReason:" + call.getDuplicateReason());
        }
    }

    /**
     * Тест 2х шагов чтения с разными projectId
     * @throws Exception
     */
    @Test
    @Transactional
    public void testCalltrackingGetCallsTwoStepsWithDifferentProjects() throws Exception {
        callTrackingCallsReader.setFrom(TestUtil.earlyDate());
        callTrackingCallsReader.setTo(TestUtil.lateDate());

        // Очищаем данные
        callRepository.deleteAll();

        // начальный индекс 0
        callTrackingCallsReader.setStartIndex(0l);
        callTrackingCallsReader.setMaxResults(5);
        callTrackingCallsReader.setProjectId(this.getDefaultCalltrackingProject().longValue());

        List<Call> calls1 = callTrackingCallsReader.read();
        callRepository.save(calls1);


        // начальный индекс 0
        callTrackingCallsReader.setStartIndex(0l);
        // меняем projectId
        callTrackingCallsReader.setProjectId(this.getReserveCalltrackingProject().longValue());
        List<Call> calls2 = callTrackingCallsReader.read();

        callRepository.save(calls2);

        // Читаем данные из callRepository, чтобы гарантировать commit транзакции
        List<Call> calls = (List<Call>) callRepository.findAll();
        for(Call call : calls){
            System.out.println("number: " + call.getNumber() + ", dt: " + call.getDt());
        }
    }

    /**
     * Проверяем загрузку повторяющихся данных и сохранение с указанием duplicateReason
     * @throws Exception
     */
    @Test
    @Transactional
    public void testCalltrackingGetCallsTwoStepsWithSameDataUsingDuplicateReason() throws Exception {
        callTrackingCallsReader.setFrom(TestUtil.earlyDate());
        callTrackingCallsReader.setTo(TestUtil.lateDate());

        // Очищаем данные
        callRepository.deleteAll();

        // начальный индекс 0
        callTrackingCallsReader.setStartIndex(0l);
        callTrackingCallsReader.setMaxResults(5);
        callTrackingCallsReader.setProjectId(this.getDefaultCalltrackingProject().longValue());

        List<Call> calls1 = callTrackingCallsReader.read();
        callRepository.save(calls1);


        // начальный индекс 0
        callTrackingCallsReader.setStartIndex(0l);
        List<Call> calls2 = callTrackingCallsReader.read();

        // перед сохранением данных ставим duplicateReason
        for(Call call : calls2){
            call.setDuplicateReason("test case");
        }

        callRepository.save(calls2);

        // Читаем данные из callRepository, чтобы гарантировать commit транзакции
        List<Call> calls = (List<Call>) callRepository.findAll();
        for(Call call : calls){
            System.out.println("number: " + call.getNumber() + ", dt: " + call.getDt());
        }
    }

    private boolean checkCallListsEquals(List<Call> calls1, List<Call> calls2) {
        if(calls1.size() != calls2.size())
            return false;

        for(int i = 0; i < calls1.size(); i++){
            Call call1 = calls1.get(i);
            Call call2 = calls2.get(i);

            if(!this.checkCallsEqual(call1, call2))
                return false;
        }
        return true;
    }

    private boolean checkCallsEqual(Call call1, Call call2) {
        return (
                call1.equals(call2)
        );
    }

    public CallTrackingAPIService getCallTrackingAPIService() {
        return callTrackingAPIService;
    }

    public void setCallTrackingAPIService(CallTrackingAPIService callTrackingAPIService) {
        this.callTrackingAPIService = callTrackingAPIService;
    }

    public Integer getDefaultCalltrackingProject() {
        return defaultCalltrackingProject;
    }

    public void setDefaultCalltrackingProject(Integer defaultCalltrackingProject) {
        this.defaultCalltrackingProject = defaultCalltrackingProject;
    }

    public Integer getReserveCalltrackingProject() {
        return reserveCalltrackingProject;
    }

    public void setReserveCalltrackingProject(Integer reserveCalltrackingProject) {
        this.reserveCalltrackingProject = reserveCalltrackingProject;
    }
}
