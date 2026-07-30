package com.app.learning.ui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class OfflineUiTest {

    @Test
    public void testApplicationContextOffline() {
        assertNotNull(RuntimeEnvironment.getApplication());
    }
}
