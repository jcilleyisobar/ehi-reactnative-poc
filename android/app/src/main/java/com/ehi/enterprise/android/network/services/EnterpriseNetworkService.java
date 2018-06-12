package com.ehi.enterprise.android.network.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.crittercism.app.Crittercism;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.app.stetho.StethoInjector;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.network.cookies.ReceivedCookiesInterceptor;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.interfaces.IRequestProcessorService;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.requests.authentication.PostLoginRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.util.AlertCallUsContinueWrapperActivity;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.ehi.enterprise.android.utils.manager.LoginManager;
import com.google.gson.Gson;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CertificatePinner;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public class EnterpriseNetworkService extends Service implements IRequestProcessorService {

    private static final String TAG = EnterpriseNetworkService.class.getSimpleName();

    private IBinder mBinder = new ApiServiceBinder();

    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private String mName;
    private Handler mUiHandler;
    private WeakReference<IApiCallback> mCallUsContinueCallback;
    private WeakReference<ResponseWrapper> mCallUsContinueResponseWrapper;


    public interface HttpClient {
        @GET
        Call<ResponseBody> get(@Url String url);

        @POST
        Call<ResponseBody> post(@Url String url, @Body Object body);

        @PUT
        Call<ResponseBody> put(@Url String url, @Body Object body);

        @PATCH
        Call<ResponseBody> patch(@Url String url);

        @DELETE
        Call<ResponseBody> delete(@Url String url);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mUiHandler = new Handler(getMainLooper());

        HandlerThread thread = new HandlerThread("RetorfitRequestProcessorService[" + mName + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public synchronized void performRequest(AbstractRequestProvider requestProvider, IApiCallback callback) {
        Message msg = mServiceHandler.obtainMessage();
        msg.obj = new RequestCallbackPair(requestProvider, callback);
        mServiceHandler.sendMessage(msg);
    }

    private void onProcessRequest(final AbstractRequestProvider provider, WeakReference<IApiCallback> weakCallback) {

        if (provider == null) {
            DLog.e(TAG,
                    "AbstractRequestProvider is not defined, dispatching processRequest() call"
                    , new NullPointerException());
            return;
        }

        ResponseWrapper wrapper = performRetrofitRequest(provider, weakCallback);

        //Handle invalid auth token
        if (EHIServicesError.ErrorCode.CROS_INVALID_AUTH_TOKEN.equals(wrapper.getErrorCode())) {
            if (LoginManager.getInstance().getEncryptedCredentials() != null) {
                //will re-auth if have encrypted data
                PostLoginRequest postLoginRequest = new PostLoginRequest(LoginManager.getInstance().getEncryptedCredentials());
                ResponseWrapper<EHIProfileResponse> loginResponse = performRetrofitRequest(postLoginRequest, weakCallback);
                if (loginResponse.isSuccess()) {
                    postLoginRequest.handleResponse(loginResponse);
                    LoginManager.getInstance().setUserAuthToken(loginResponse.getData().getAuthToken());
                    LoginManager.getInstance().setEncryptedCredentials(loginResponse.getData().getEncryptedAuthData(), true);
                    LoginManager.getInstance().setProfile(loginResponse.getData());
                    //resending failed request
                    wrapper = performRetrofitRequest(provider, weakCallback);
                } else {
                    //if failed to relogin not because of bad connection will cleare tokens and encrypted data data
                    if (loginResponse.getStatus() != ResponseWrapper.NO_CONNECTIONS_AVAILABLE) {
                        LoginManager.getInstance().logOut();
                    }
                }
            } else {
                //will clear auth token and logout user return original 403 response to UI.
                LoginManager.getInstance().logOut();
            }
        }

        if (Settings.LOG_CRASHES && !wrapper.isSuccess()) {
            Crittercism.logHandledException(new EHINetworkFailure(wrapper.getMessage()));
        }
        provider.handleResponse(wrapper);
        sendCallback(wrapper, weakCallback, provider);
    }

    private Retrofit.Builder getRetrofitBuilder(final AbstractRequestProvider provider) {
        Retrofit.Builder adapterBuilder = new Retrofit.Builder();

        String endpointUrl = provider.getEndpointUrl();
        if (!endpointUrl.endsWith("/")){
            endpointUrl = endpointUrl + "/";
        }
        adapterBuilder.baseUrl(endpointUrl);
        CertificatePinner.Builder certificatePinner = new CertificatePinner.Builder();
        if (Settings.SOLR_ENVIRONMENT.getSolrCertificates() != null) {
            for (Certificate certificate : Settings.SOLR_ENVIRONMENT.getSolrCertificates()) {
                certificatePinner.add(Settings.SOLR_ENVIRONMENT.getSolrHost(), CertificatePinner.pin(certificate));
            }
        }
        if (Settings.ENVIRONMENT.getCertificates() != null) {
            for (Certificate certificate : Settings.ENVIRONMENT.getCertificates()) {
                certificatePinner.add(Settings.ENVIRONMENT.getGboLocationEndpoint(), CertificatePinner.pin(certificate));
                certificatePinner.add(Settings.ENVIRONMENT.getGboProfileEndpoint(), CertificatePinner.pin(certificate));
                certificatePinner.add(Settings.ENVIRONMENT.getGboRentalEndpoint(), CertificatePinner.pin(certificate));
            }
        }
        if (Settings.ENVIRONMENT.getFareOfficeCertificates() != null &&
                !EHITextUtils.isEmpty(Settings.ENVIRONMENT.getPaymentEnvironmentHost())) {
            for (Certificate certificate : Settings.ENVIRONMENT.getFareOfficeCertificates()) {
                certificatePinner.add(Settings.ENVIRONMENT.getPaymentEnvironmentHost(), CertificatePinner.pin(certificate));
            }
        }
        final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.certificatePinner(certificatePinner.build());

        //noinspection ConstantConditions This will be null in production
        if (StethoInjector.getStethoNetworkInterceptor() != null) {
            okHttpClientBuilder.networkInterceptors().add(StethoInjector.getStethoNetworkInterceptor());
        }

        okHttpClientBuilder.connectTimeout(20, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(20, TimeUnit.SECONDS);
        Gson gson = BaseAppUtils.getDefaultGson();
        adapterBuilder.addConverterFactory(GsonConverterFactory.create(gson));

        if (provider.getHeaders() != null) {
            Interceptor headerInterceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                    final Request.Builder chainRequestBuilder = chain.request().newBuilder();
                    for (Object objectEntry : provider.getHeaders().entrySet()) {
                        Map.Entry<String, String> entry = (Map.Entry<String, String>) objectEntry;
                        chainRequestBuilder.addHeader(entry.getKey(), entry.getValue());
                    }
                    final String gboRegionCookie = LocalDataManager.getInstance().getGboRegionCookie();
                    chainRequestBuilder.addHeader("Cookie", gboRegionCookie);
                    Request request = chainRequestBuilder.build();
                    return chain.proceed(request);
                }
            };

            okHttpClientBuilder.addInterceptor(headerInterceptor);
        }

        final HttpLoggingInterceptor httpLoggingInterceptor =  new HttpLoggingInterceptor();

        if (Settings.SHOW_LOGS) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        okHttpClientBuilder.addInterceptor(httpLoggingInterceptor);
        okHttpClientBuilder.addInterceptor(new ReceivedCookiesInterceptor());

        adapterBuilder.client(okHttpClientBuilder.build());

        return adapterBuilder;
    }

    private ResponseWrapper performRetrofitRequest(final AbstractRequestProvider provider, WeakReference<IApiCallback> weakCallback) {
        Retrofit.Builder retrofitBuilder = getRetrofitBuilder(provider);
        HttpClient client = retrofitBuilder.build().create(HttpClient.class);
        Response<ResponseBody> retrofitResponse;

        String url = provider.getRequestUrl();
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        ResponseWrapper wrapper = null;

        //performing request
        try {
            switch (provider.getRequestType()) {
                case POST:
                    retrofitResponse = client.post(url, provider.getRequestBody()).execute();
                    break;
                case PUT:
                    retrofitResponse = client.put(url, provider.getRequestBody()).execute();
                    break;
                case PATCH:
                    retrofitResponse = client.patch(url).execute();
                    break;
                case DELETE:
                    retrofitResponse = client.delete(url).execute();
                    break;
                case GET:
                default:
                    retrofitResponse = client.get(url).execute();
                    break;
            }
        } catch (EOFException error) {
            DLog.w(TAG, error);
            wrapper = new ResponseWrapper();
            wrapper.setStatus(ResponseWrapper.JSON_PARSING_ERROR);
            wrapper.setMessage(getString(R.string.reachability_details));
            return wrapper;
        } catch (Exception error) {
            DLog.w(TAG, error);
            wrapper = new ResponseWrapper();
            wrapper.setStatus(ResponseWrapper.NO_CONNECTIONS_AVAILABLE);
            wrapper.setMessage(getString(R.string.reachability_details));
            return wrapper;
        }

        if (retrofitResponse != null) {
            if (retrofitResponse.isSuccessful()) {
                wrapper = parseResponse(provider, retrofitResponse.body(), retrofitResponse.code());
            } else {
                //noinspection ResourceType
                wrapper = parseResponse(provider, retrofitResponse.errorBody(), retrofitResponse.code());

                // populate error messages from string in case response is from farepayment service
                if (provider.getEndpointUrl().contains(Settings.ENVIRONMENT.getPaymentEnvironmentHost())) {
                    populateFarepaymentErrorMessage(wrapper);
                }

                // attempt to get service error messages if they were provided
                EHIServicesError.DisplayAs displayAs = wrapper.getDisplayAs();

                if (displayAs != null
                        && displayAs.equals(EHIServicesError.DisplayAs.CALL_US_CONTINUE)) {
                    setCallUsContinueCallback(weakCallback);
                    setCallUsContinueResponseWrapper(wrapper);
                    startActivity(AlertCallUsContinueWrapperActivity.getIntent(EnterpriseNetworkService.this, wrapper.getMessage()));
                }
            }
        }
        return wrapper;
    }


    private ResponseWrapper parseResponse(AbstractRequestProvider provider, ResponseBody retrofitResponseBody, int statusCode) {
        //parsing data
        Reader bodyStream = null;
        ResponseWrapper wrapper = new ResponseWrapper();
        try {
            if (provider.isExpectingBody()) {
                bodyStream = retrofitResponseBody.charStream();
                wrapper.setData(provider.parseRawResponse(bodyStream));
            }
            //noinspection ResourceType
            wrapper.setStatus(statusCode);
        } catch (Exception e) {
            DLog.w(TAG, e);
            wrapper.setStatus(ResponseWrapper.JSON_PARSING_ERROR);
            wrapper.setMessage(getString(R.string.reachability_details));
        } finally {
            if (bodyStream != null) {
                try {
                    bodyStream.close();
                } catch (IOException e) {
                    DLog.w(TAG, e);
                }
            }
        }
        return wrapper;
    }

    private void sendCallback(final ResponseWrapper wrapper, WeakReference<IApiCallback> weakCallback, AbstractRequestProvider provider) {
        if (weakCallback == null) {
            DLog.w(TAG,
                    "Callback for Api call is not specified. Would notify UI about the result.",
                    new NullPointerException());
        } else {
            final IApiCallback callback = weakCallback.get();
            if (callback != null) {
                if (!wrapper.isSuccess()) {
                    EHIAnalyticsEvent.create()
                            .action(EHIAnalytics.Motion.MOTION_NONE.value, EHIAnalytics.Action.ACTION_ERROR.value)
                            .smartTrackAction(true)
                            .macroEvent(EHIAnalytics.MacroEvent.MACRO_ERROR.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.error(wrapper, provider))
                            .tagScreen()
                            .tagEvent()
                            .tagMacroEvent();
                }
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.handleResponse(wrapper);
                    }
                });
            } else {
                DLog.w(TAG,
                        "Dead reference to IApiCallback object",
                        new NullPointerException());
            }
        }
    }

    public final class ApiServiceBinder extends Binder {
        public IRequestProcessorService getService() {
            // Return this instance of LocalService so clients can call public methods
            return EnterpriseNetworkService.this;
        }
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            RequestCallbackPair pair = (RequestCallbackPair) msg.obj;
            onProcessRequest(pair.provider, pair.callback);
        }
    }

    public void setCallUsContinueCallback(WeakReference<IApiCallback> callUseContinueCallback) {
        mCallUsContinueCallback = callUseContinueCallback;
    }

    public WeakReference<IApiCallback> getCallUsContinueCallback() {
        final WeakReference<IApiCallback> callUseContinueCallback = mCallUsContinueCallback;
        mCallUsContinueCallback = null;
        return callUseContinueCallback;
    }

    public void setCallUsContinueResponseWrapper(ResponseWrapper callUsContinueResponseWrapper) {
        mCallUsContinueResponseWrapper = new WeakReference<>(callUsContinueResponseWrapper);
    }

    public ResponseWrapper getCallUsContinueResponseWrapper() {
        final ResponseWrapper responseWrapper = mCallUsContinueResponseWrapper.get();
        mCallUsContinueResponseWrapper = null;
        return responseWrapper;
    }

    private void populateFarepaymentErrorMessage(ResponseWrapper wrapper) {
        String fareErrorMessage;
        if (wrapper != null) {
            switch (wrapper.getStatus()) {
                case ResponseWrapper.BAD_REQUEST:
                    fareErrorMessage = getString(R.string.alert_service_farepayment_bad_input);
                    break;
                case ResponseWrapper.NOT_FOUND:
                    fareErrorMessage = getString(R.string.alert_service_farepayment_bad_submission_key);
                    break;
                case ResponseWrapper.CONFLICT:
                    fareErrorMessage = getString(R.string.alert_service_farepayment_duplicate_submission);
                    break;
                default:
                    fareErrorMessage = getString(R.string.alert_service_farepayment_unexpected_error);
                    break;
            }
            wrapper.setMessage(fareErrorMessage);
        }
    }
}
