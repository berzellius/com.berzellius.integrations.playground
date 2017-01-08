package com.berzellius.integrations.playground.batch;

import com.berzellius.integrations.playground.dmodel.Call;
import com.berzellius.integrations.playground.reader.CallTrackingCallsReaderStepScoped;
import com.berzellius.integrations.playground.repository.CallRepository;
import com.berzellius.integrations.playground.service.CallTrackingAPIServiceAdapter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.text.ParseException;
import java.util.List;

/**
 * Created by berz on 20.09.2015.
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
@PropertySource("classpath:batch.properties")
public class CallsImportBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private CallTrackingAPIServiceAdapter callTrackingAPIServiceAdapter;

    @Autowired
    private CallRepository callRepository;

    @Bean
    @StepScope
    public ItemReader<List<Call>> callsReader() throws ParseException {
        CallTrackingCallsReaderStepScoped reader = new CallTrackingCallsReaderStepScoped();
        return reader;
    }

    @Bean
    public ItemProcessor<List<Call>, List<Call>> itemProcessor(){
        return new ItemProcessor<List<Call>, List<Call>>() {
            @Override
            public List<Call> process(List<Call> calls) throws Exception {

                System.out.println("process calls: " + calls);

                for(Call call : calls){
                    callTrackingAPIServiceAdapter.processCallOnImport(call);
                }

                return calls;
            }
        };
    }

    @Bean
    public ItemWriter<List<Call>> writer(){

        return new ItemWriter<List<Call>>() {
            @Override
            public void write(List<? extends List<Call>> callsPortions) throws Exception {
                for(List<Call> calls : callsPortions) {
                    System.out.println("write calls to base: " + calls);
                    callRepository.save(calls);
                }
            }
        };
    }

    @Bean
    public Step callsImportStep(
            StepBuilderFactory stepBuilderFactory,
            ItemReader<List<Call>> callsReader,
            ItemProcessor<List<Call>, List<Call>> itemProcessor,
            ItemWriter<List<Call>> writer
    ){
        return stepBuilderFactory.get("callsImportStep")
                // представляется верным, что chunk size - это эквивалент commit interval
                .<List<Call>, List<Call>>chunk(1)
                .reader(callsReader)
                .processor(itemProcessor)
                .writer(writer)
                /*.faultTolerant()
                .skip(RuntimeException.class)
                .skipLimit(2000)*/
                .build();
    }

    @Bean
    public Job callsImportJob(Step callsImportStep){
        RunIdIncrementer runIdIncrementer = new RunIdIncrementer();


        return jobBuilderFactory.get("calls ImportJob")
                .incrementer(runIdIncrementer)
                .flow(callsImportStep)
                .end()
                .build();
    }
}
