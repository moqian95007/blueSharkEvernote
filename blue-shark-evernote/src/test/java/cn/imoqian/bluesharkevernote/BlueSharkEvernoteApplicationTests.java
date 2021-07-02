package cn.imoqian.bluesharkevernote;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.KeyStore;

@SpringBootTest
class BlueSharkEvernoteApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void basicHttpsGetUsingSslSocketFactory() throws Exception {
        String keyStoreFile = "D:\\code\\ttt.ks";
        String password = "poiuyt";
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream in = new FileInputStream(keyStoreFile);
        ks.load(in, password.toCharArray());

        System.out.println(KeyStore.getDefaultType().toString());
        System.out.println(TrustManagerFactory.getDefaultAlgorithm().toString());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), null);

        LayeredConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(ctx);

        String url = "https://ttt.aneasystone.com";

        /**
          * Return the page with content:
          *  401 Authorization Required
          */

        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();

        HttpGet request = new HttpGet(url);
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) ...");

        CloseableHttpResponse response = httpclient.execute(request);
        String responseBody = readResponseBody(response.getEntity().getContent());
        System.out.println(responseBody);
    }

    // 读取输入流中的数据
    private String readResponseBody(InputStream inputStream) throws IOException {

        BufferedReader in = new BufferedReader(
                new InputStreamReader(inputStream));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
