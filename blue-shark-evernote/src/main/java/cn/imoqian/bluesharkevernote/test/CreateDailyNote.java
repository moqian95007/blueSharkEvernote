package cn.imoqian.bluesharkevernote.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CreateDailyNote {
    private static String KEY = "";
    //SANDBOX
    private static String TOKEN = "";
    private static String NOTE_STORE_URL = "";
    private static String NOTEBOOK_GUID = "";
    private static EvernoteService EVERNOTE_SERVICE= EvernoteService.SANDBOX;

    public static void main(String[] args) {
        CreateDailyNote createDailyNote = new CreateDailyNote();
        createDailyNote.createDailyNote("2021-07");
    }

    void createDailyNote(String month) {
        try {
            String workDayBody = "<div>" +
                    "通勤路上听得到<br />" +
                    "上班打卡<br />" +
                    "支付宝能量<br />" +
                    "支付宝小鸡<br />" +
                    "mixin签到<br />" +
                    "编程营每日小任务<br />" +
                    "编程营打卡群待办（上面共10分钟）<br />" +
                    "<br />" +
                    "文章日更<br />" +
                    "<br />" +
                    "</div>";
            String holidayBody = "<div>" +
                    "支付宝能量<br />" +
                    "支付宝小鸡<br />" +
                    "mixin签到<br />" +
                    "<br />" +
                    "文章日更<br />" +
                    "<br />" +
                    "</div>";

            //印象笔记 client
            EvernoteAuth evernoteAuth = new EvernoteAuth(EVERNOTE_SERVICE, TOKEN);
            evernoteAuth.setNoteStoreUrl(NOTE_STORE_URL);
            ClientFactory factory = new ClientFactory(evernoteAuth);
            NoteStoreClient client = factory.createNoteStoreClient();


            //获取节假日接口：https://www.tianapi.com/apiview/139
            String url = "http://api.tianapi.com/txapi/jiejiari/index?key={KEY}&type=2&date={MONTH}";
            url = url.replace("{KEY}", KEY).replace("{MONTH}", month);
            CloseableHttpClient httpclient = HttpClients.custom().build();
            HttpGet request = new HttpGet(url);
//        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) ...");
            CloseableHttpResponse response = httpclient.execute(request);
            String responseBody = readResponseBody(response.getEntity().getContent());
            System.out.println(responseBody);
            JSONObject responseObj = JSON.parseObject(responseBody);
            if (responseObj.getInteger("code") == 200) {
                JSONArray jsonArray = responseObj.getJSONArray("newslist");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject oneDay = jsonArray.getJSONObject(i);
                    StringBuilder title = new StringBuilder();
                    String date = oneDay.getString("date");
                    Integer isnotwork = oneDay.getInteger("isnotwork");
                    String cnweekday = oneDay.getString("cnweekday");
                    String info = oneDay.getString("info");
                    String tip = oneDay.getString("tip");
                    String memo = "";
                    //周末+调休
                    if ("调休".equals(tip)) {
                        memo = "上班（调休）";
                    }
                    //节假日（包括周中+周末）
                    if ("节假日".equals(info)) {
                        //用有没有英文名区别是否为节日当天，其实有一个holiday的字段标记，节日是那一天，但是格式和date不一样，还要转换有点麻烦。
                        //"date":"2021-09-21",
                        //"holiday":"9月21号",
                        String name = oneDay.getString("name");
                        String enname = oneDay.getString("enname");
                        if ("".equals(enname)) {
                            memo += name + "假期";
                        } else {
                            memo += name;
                        }
                    }
                    title.append(date);
                    title.append(" ");
                    title.append(cnweekday);
                    title.append(" ");
                    title.append(memo);
                    System.out.println(title);

                    //获取指定笔记本
                    Notebook notebook = client.getNotebook(NOTEBOOK_GUID);
                    //创建笔记
                    //休息日
                    if (1 == isnotwork) {
                        //title 不能以空格结尾，有点死板
                        makeNote(client, title.toString().trim(), holidayBody, notebook);
                    } else {
                        makeNote(client, title.toString().trim(), workDayBody, notebook);
                    }
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (EDAMNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Note makeNote(NoteStoreClient noteStore, String noteTitle, String noteBody, Notebook parentNotebook) {

        //具体笔记内容格式见：https://dev.yinxiang.com/doc/articles/enml.php
        String nBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        nBody += "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">";
        nBody += "<en-note>" + noteBody + "</en-note>";

        // Create note object
        Note ourNote = new Note();
        ourNote.setTitle(noteTitle);
        ourNote.setContent(nBody);

        // parentNotebook is optional; if omitted, default notebook is used
        if (parentNotebook != null && parentNotebook.isSetGuid()) {
            ourNote.setNotebookGuid(parentNotebook.getGuid());
        }

        // Attempt to create note in Evernote account
        Note note = null;
        try {
            note = noteStore.createNote(ourNote);
        } catch (EDAMUserException edue) {
            // Something was wrong with the note data
            // See EDAMErrorCode enumeration for error code explanation
            // http://dev.yinxiang.com/documentation/reference/Errors.html#Enum_EDAMErrorCode
            System.out.println("EDAMUserException: " + edue);
        } catch (EDAMNotFoundException ednfe) {
            // Parent Notebook GUID doesn't correspond to an actual notebook
            System.out.println("EDAMNotFoundException: Invalid parent notebook GUID");
        } catch (Exception e) {
            // Other unexpected exceptions
            e.printStackTrace();
        }

        // Return created note object
        return note;
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
