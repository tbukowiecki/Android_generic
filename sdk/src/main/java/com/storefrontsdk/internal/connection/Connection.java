package com.storefrontsdk.internal.connection;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.github.kubatatami.judonetworking.Endpoint;
import com.github.kubatatami.judonetworking.EndpointFactory;
import com.github.kubatatami.judonetworking.Request;
import com.github.kubatatami.judonetworking.controllers.json.simple.JsonSimpleRestController;
import com.github.kubatatami.judonetworking.exceptions.HttpException;
import com.github.kubatatami.judonetworking.exceptions.JudoException;
import com.github.kubatatami.judonetworking.exceptions.ParseException;
import com.github.kubatatami.judonetworking.exceptions.ProtocolException;
import com.github.kubatatami.judonetworking.internals.results.ErrorResult;
import com.github.kubatatami.judonetworking.internals.results.RequestResult;
import com.github.kubatatami.judonetworking.internals.results.RequestSuccessResult;
import com.github.kubatatami.judonetworking.logs.ErrorLogger;
import com.github.kubatatami.judonetworking.transports.OkHttpTransportLayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.storefrontsdk.BuildConfig;
import com.storefrontsdk.Environment;
import com.storefrontsdk.R;
import com.storefrontsdk.internal.Configuration;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * Created by Kuba on 24/01/15.
 */
@EBean(scope = EBean.Scope.Singleton)
public class Connection {

    @RootContext
    protected Context context;
    protected StoreFontSdkApi api;
    protected Endpoint endpoint;
    protected Environment environment;

    protected JsonSimpleRestController controller = new JsonSimpleRestController() {
        @Override
        public RequestResult parseResponse(Request request, InputStream stream, Map<String, List<String>> headers) {
            RequestResult requestResult = super.parseResponse(request, stream, headers);
            if (requestResult instanceof ErrorResult && ((ErrorResult) requestResult).error instanceof ParseException) {
                try {
                    stream.reset();
                    String inputStreamString = getStringFromInputStream(stream);
                    if (inputStreamString.equals("[]")) {
                        return new RequestSuccessResult(request.getId(), null);
                    } else {
                        return requestResult;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return requestResult;
        }
    };

    @AfterInject
    public void createImageLoader() {
        DisplayImageOptions thumbDisplayOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.
                Builder(context.getApplicationContext()).defaultDisplayImageOptions(thumbDisplayOptions).build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().handleSlowNetwork(true);
    }


    public void createEndpoint(Environment environment, String sdkProjectSecret, int sdkProjectId) {
        if (sdkProjectId==controller.getCustomPostKey("sdk_project_id") && environment.equals(this.environment)) {
            return;
        }
        this.environment=environment;
        controller.addCustomPostKey("api_version", "1.1.0");
        controller.addCustomPostKey("sdk_project_secret", sdkProjectSecret);
        controller.addCustomPostKey("sdk_project_id", sdkProjectId);
        endpoint = EndpointFactory.createEndpoint(context,
                controller, new OkHttpTransportLayer(),
                environment.equals(Environment.PRODUCTION) ? Configuration.URL : Configuration.SANDBOX_URL);
        endpoint.setDebugFlags(BuildConfig.DEBUG ? Endpoint.FULL_DEBUG : Endpoint.NO_DEBUG);
        endpoint.setCacheEnabled(true);
        endpoint.addErrorLogger(new ErrorLogger() {
            @Override
            public void onError(JudoException e, Request request) {
                if (e instanceof ProtocolException || e instanceof HttpException || e instanceof ParseException) {
                    Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        api = endpoint.getService(StoreFontSdkApi.class);
    }

    public Environment getEnvironment() {
        return environment;
    }


    public Endpoint getEndpoint() {
        return endpoint;
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    public StoreFontSdkApi getApi() {
        return api;
    }

    public void clearCache() {
        if(endpoint!=null){
            endpoint.getMemoryCache().clearCache();
        }
    }
}
