import com.berzellius.integrations.playground.reader.CallTrackingCallsReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by berz on 06.01.2017.
 */
@Configuration
public class TestBeans {

    @Bean
    CallTrackingCallsReader callTrackingCallsReader(){
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2016);
        c1.set(Calendar.MONTH, Calendar.NOVEMBER);
        c1.set(Calendar.DAY_OF_MONTH, 1);
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);

        c2.set(Calendar.YEAR, 2017);
        c2.set(Calendar.MONTH, Calendar.JANUARY);
        c2.set(Calendar.DAY_OF_MONTH, 1);
        c2.set(Calendar.HOUR_OF_DAY, 23);
        c2.set(Calendar.MINUTE, 59);
        c2.set(Calendar.SECOND, 59);


        Date dt1 = c1.getTime();
        Date dt2 = c2.getTime();
        System.out.println("ff: " + dt1 + ", tt: " + dt2);

        CallTrackingCallsReader callTrackingCallsReader = new CallTrackingCallsReader(dt1, dt2, 11, 0l);
        return callTrackingCallsReader;
    }
}
