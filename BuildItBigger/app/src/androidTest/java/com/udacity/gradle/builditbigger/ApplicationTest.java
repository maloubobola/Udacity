package com.udacity.gradle.builditbigger;

import android.test.AndroidTestCase;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends AndroidTestCase {
    private String joke = "";

    public void testAsyncTask() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);

        new EndPointTask() {
            @Override
            protected void onPostExecute(String result) {
                joke = result;
                signal.countDown();
            }
        }.execute();

        signal.await(30, TimeUnit.SECONDS);
        assertTrue(!joke.isEmpty());
    }
}