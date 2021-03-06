package com.foodpanda.urbanninja.api.model;

import com.foodpanda.urbanninja.model.enums.Status;

import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;

public class PerformActionWrapperTest {

    @Test
    public void testPerformActionDateFormatted() {
        DateTime dateTime = DateTime.now();
        PerformActionWrapper performActionWrapper = new PerformActionWrapper(Status.ARRIVED, dateTime, any(RiderLocation.class));
        assertNotNull(performActionWrapper.getActionPerformedAt());
        assertEquals(dateTime.toString(), performActionWrapper.getActionPerformedAt());
    }
}
