package com.foodpanda.urbanninja.api.model;

import com.foodpanda.urbanninja.model.TimeWindow;

import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ScheduleWrapperTest {

    @Test
    public void testIsScheduleFinished() {
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        scheduleWrapper.setTimeWindow(new TimeWindow(DateTime.now(), DateTime.now().minusSeconds(10)));
        assertTrue(scheduleWrapper.isScheduleFinished());
    }

    @Test
    public void testIsScheduleNotFinished() {
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        scheduleWrapper.setTimeWindow(new TimeWindow(DateTime.now(), DateTime.now().plusSeconds(10)));
        assertFalse(scheduleWrapper.isScheduleFinished());
    }

    @Test
    public void testIsTimeWindowNull() {
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        assertFalse(scheduleWrapper.isScheduleFinished());
    }

    @Test
    public void testIsEndAtNull() {
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        scheduleWrapper.setTimeWindow(new TimeWindow(DateTime.now(), null));
        assertFalse(scheduleWrapper.isScheduleFinished());
    }
}
