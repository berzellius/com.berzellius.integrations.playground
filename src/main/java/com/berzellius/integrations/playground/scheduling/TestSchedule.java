package com.berzellius.integrations.playground.scheduling;

import com.berzellius.integrations.playground.dmodel.Call;
import com.berzellius.integrations.playground.repository.CallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by berz on 03.01.2017.
 */
@Service
@Transactional
public class TestSchedule {

    @Autowired
    CallRepository callRepository;



    @Scheduled(fixedDelay = 10000)
    public void test(){
        System.out.println("schedule me! по часам");
        Call call = new Call();
        call.setSource("ssss");
        call.setDtmCreate(new Date());
        callRepository.save(call);
    }
}
