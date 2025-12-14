//package kr.co.ohgoodfood._legacy;
//
//import okhttp3.OkHttpClient;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//public class HttpClientConfig implements DisposableBean {
//
//    private OkHttpClient httpClient;
//
//    @Bean
//    public OkHttpClient okHttpClient() {
//        this.httpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .build();
//        return this.httpClient;
//    }
//
//    @Override
//    public void destroy() throws Exception {
//        if (httpClient != null) {
//            httpClient.dispatcher().executorService().shutdown();
//            httpClient.connectionPool().evictAll();
//            if (httpClient.cache() != null) {
//                httpClient.cache().close(); // 캐시 사용 시 반드시 닫아야 함
//            }
//        }
//    }
//}