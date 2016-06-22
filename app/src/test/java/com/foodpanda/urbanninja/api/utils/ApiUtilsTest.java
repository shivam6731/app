package com.foodpanda.urbanninja.api.utils;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.ui.activity.LoginActivity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.robolectric.Robolectric.buildActivity;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class ApiUtilsTest {

    @Test
    public void testUnknownErrorHandle() {
        ErrorMessage errorMessage = ApiUtils.handleError(new Throwable("Unknown Error"));
        assertTrue(errorMessage.getStatus() == 500);
        assertEquals("Unknown Error", errorMessage.getMessage());
    }

    @Test
    public void testHttpErrorHandle() {
        ErrorMessage errorMessage = ApiUtils.handleError(new HttpException(
            Response.error(401, createResponseBoby())));

        assertTrue(errorMessage.getStatus() == 401);
        assertEquals("251: Authentication failed, invalid credentials.", errorMessage.getMessage());
    }

    @Test
    public void testShowErrorMessage() {
        LoginActivity loginActivity = buildActivity(LoginActivity.class).get();

        ApiUtils.showErrorMessage(new Throwable("Unknown Error"), loginActivity);
        Assert.assertEquals("Unknown Error", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testWrapObservable() {
        Observable<Object> observable = ApiUtils.wrapObservable(Observable.empty());
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        testSubscriber.onCompleted();

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertReceivedOnNext(Collections.emptyList());
    }

    private ResponseBody createResponseBoby() {
        return ResponseBody.create(MediaType.parse("text"),
            "{\"status\":401,\"errorCode\":251," +
                "\"message\":\"251: Authentication failed, invalid credentials.\"," +
                "\"developerMessage\":\"Could not find user by username: kostyw\"}");
    }
}
