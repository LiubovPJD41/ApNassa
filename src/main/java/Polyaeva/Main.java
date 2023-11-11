package Polyaeva;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.List;


        public class Main {
            private static final String REMOTE_SERVICE_URI =
                    "https://api.nasa.gov/planetary/apod?api_key=UvWyZV9CnUPDF8L3D5r7DA3jZU6CW5AikQRDe9Uz";
            public static final ObjectMapper mapper = new ObjectMapper();

            public static void main(String[] args) throws IOException, URISyntaxException {
                CloseableHttpClient httpClient = HttpClientBuilder.create()
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                                .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                                .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                                .build())
                        .build();

                // создание объекта запроса с произвольными заголовками
                HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
                request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
                // отправка запроса
                CloseableHttpResponse response = httpClient.execute(request);
                // преобразование json в  JAVA объекты
                Post post = mapper.readValue(response.getEntity().getContent().readAllBytes(), new TypeReference<Post>() {
                });
                System.out.println(post.toString());

                //второй http запрос

                String url = post.getUrl();
                //System.out.println(url);
                HttpGet request2 = new HttpGet(url);
                request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
                // отправка запроса
                CloseableHttpResponse response2 = httpClient.execute(request2);
                byte[] content = EntityUtils.toByteArray(response2.getEntity());
                String picture = Paths.get(new URI(url).getPath()).getFileName().toString();
               // System.out.println(picture);
                File file = new File(picture);
                try {
                    if (file.createNewFile())
                        System.out.println("Файл был создан");
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                try (FileOutputStream fos = new FileOutputStream(picture)) {
                    // запись байтов в файл
                    fos.write(content, 0, content.length);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
    }
}