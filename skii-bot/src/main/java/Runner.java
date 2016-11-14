import com.google.gson.GsonBuilder;
import model.Updates;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Runner {

    static CloseableHttpClient httpclient = HttpClients.createDefault();
    static CloseableHttpAsyncClient httpcAsyncClient = HttpAsyncClients.createDefault();

    private static final String telegramUrl = "https://api.telegram.org/bot";
    private static final String token = "285741728:AAHyKugTknSHjhhED25_JazOMJJ7-5_AlZg";
    private static final String jasnaUri = "http://www.skiresort.info/ski-resort/jasna-nizke-tatry-chopok/snow-report/";
    private static final String jasnaWeatherUri = "http://www.skiresort.info/ski-resort/jasna-nizke-tatry-chopok/weather/";
    private static final String zakopaneUri = "http://www.skiresort.info/ski-resort/kasprowy-wierch-zakopane/snow-report/";
    private static HttpGet getUpdates = new HttpGet(telegramUrl + token + "/getupdates");
    private static HttpGet jasna = new HttpGet(jasnaUri);
    private static HttpGet zakopane = new HttpGet(zakopaneUri);

    static Map<Integer, String> sorry = new HashMap<>();
    static {
        sorry.put(0,"Urecognized command, say what?");
        sorry.put(1,"Ehmm???");
        sorry.put(2,"I am busy, sorry");
        sorry.put(3,"Maybe next time");
        sorry.put(4,"How dare you?!");
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {

        httpcAsyncClient.start();

        while (true) {
            TimeUnit.SECONDS.sleep(2);
            Updates updates = receiveUpdates(getUpdates);
            HttpGet getLastUpdates;

            if (!updates.result.isEmpty()) {
                int lastMessage = updates.result.size() - 1;
                long next_update_id = updates.result.get(lastMessage).update_id + 1;

                getLastUpdates = new HttpGet(telegramUrl + token + "/getupdates" + "?offset=" + next_update_id + "&timeout=" + 60);

                long chat_id = updates.result.get(lastMessage).message.chat.id;
                String text = updates.result.get(lastMessage).message.text.toLowerCase();
                String name = updates.result.get(lastMessage).message.from.first_name;

                switch (text) {
                    case "snow": {
                        String weatherJasna = sendWeatherRequest(jasna);
                        String weatherZakopane = sendWeatherRequest(zakopane);
                        sendMessage(buildRequest(chat_id, weatherJasna + "\n" + weatherZakopane));
                    }
                    break;
                    case "roman kiyashko": {
                        sendMessage(buildRequest(chat_id, "http://redtube.com"));
                    }break;
                    case "weather": {
                        sendMessage(buildRequest(chat_id, "http://ru.snow-forecast.com/resorts/Jasna-Chopok/snow-report"));
                    }
                    break;
                    default: {
                        sendMessage(buildRequest(chat_id, sorry.get(new Random().nextInt(5))));
                    }
                    break;
                }
                asyncSend(getLastUpdates);
            }
        }
    }

    private static HttpGet buildRequest(long chat_id, String message) throws UnsupportedEncodingException {
        return new HttpGet(telegramUrl + token + "/sendmessage" + "?chat_id=" + chat_id +
                "&text=" + URLEncoder.encode(message, "UTF-8"));
    }

    private static String asyncSend(HttpUriRequest request) throws InterruptedException, IOException, ExecutionException {
        Future<HttpResponse> future = httpcAsyncClient.execute(request, null);
        HttpResponse response = future.get();
        return EntityUtils.toString(response.getEntity());
    }

    private static void sendMessage(HttpUriRequest uri) throws ExecutionException, InterruptedException, IOException {
        System.out.println("message was sent to chat: " +
                EntityUtils.toString(httpclient.execute(uri).getEntity()));
    }

    private static String sendWeatherRequest(HttpUriRequest uri) throws IOException {
        String raw = EntityUtils.toString(httpclient.execute(uri).getEntity());
        Document doc = Jsoup.parse(raw);
        return doc.select("meta[name=description]").attr("content");
    }

    private static Updates receiveUpdates(HttpUriRequest uri) throws ExecutionException, InterruptedException, IOException {
        String raw = asyncSend(uri);
        Updates updates = new GsonBuilder().create().fromJson(raw, Updates.class);
        System.out.println(updates);
        return updates;
    }

}
