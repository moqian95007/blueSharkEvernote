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
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;
import org.springframework.validation.Errors;

import java.util.List;

public class Test {
    public static void main2(String[] args) {
        String json = "[{\"date\":\"2021-09-18\",\"daycode\":3,\"weekday\":6,\"cnweekday\":\"星期六\",\"lunaryear\":\"辛丑\",\"lunarmonth\":\"八月\",\"lunarday\":\"十二\",\"info\":\"双休日\",\"start\":\"\",\"now\":\"\",\"end\":\"\",\"holiday\":\"\",\"name\":\"\",\"enname\":\"\",\"isnotwork\":0,\"vacation\":\"\",\"remark\":\"\",\"tip\":\"调休\",\"rest\":\"\"},{\"date\":\"2021-09-19\",\"daycode\":1,\"weekday\":0,\"cnweekday\":\"星期日\",\"lunaryear\":\"辛丑\",\"lunarmonth\":\"八月\",\"lunarday\":\"十三\",\"info\":\"节假日\",\"start\":0,\"now\":0,\"end\":2,\"holiday\":\"9月21号\",\"name\":\"中秋节\",\"enname\":\"\",\"isnotwork\":1,\"vacation\":[\"2021-09-19\",\"2021-09-20\",\"2021-09-21\"],\"remark\":[\"2021-09-18\"],\"tip\":\"9月19日至9月21日放假调休，共3天。9月18日(周六)上班。\",\"rest\":\"9月22日至9月24日请假3天，与周末连休可拼8天长假。\"},{\"date\":\"2021-09-20\",\"daycode\":1,\"weekday\":1,\"cnweekday\":\"星期一\",\"lunaryear\":\"辛丑\",\"lunarmonth\":\"八月\",\"lunarday\":\"十四\",\"info\":\"节假日\",\"start\":0,\"now\":1,\"end\":2,\"holiday\":\"9月21号\",\"name\":\"中秋节\",\"enname\":\"\",\"isnotwork\":1,\"vacation\":[\"2021-09-19\",\"2021-09-20\",\"2021-09-21\"],\"remark\":[\"2021-09-18\"],\"tip\":\"9月19日至9月21日放假调休，共3天。9月18日(周六)上班。\",\"rest\":\"9月22日至9月24日请假3天，与周末连休可拼8天长假。\"},{\"date\":\"2021-09-21\",\"daycode\":1,\"weekday\":2,\"cnweekday\":\"星期二\",\"lunaryear\":\"辛丑\",\"lunarmonth\":\"八月\",\"lunarday\":\"十五\",\"info\":\"节假日\",\"start\":0,\"now\":2,\"end\":2,\"holiday\":\"9月21号\",\"name\":\"中秋节\",\"enname\":\"the Mid-Autumn Festival\",\"isnotwork\":1,\"vacation\":[\"2021-09-19\",\"2021-09-20\",\"2021-09-21\"],\"remark\":[\"2021-09-18\"],\"tip\":\"9月19日至9月21日放假调休，共3天。9月18日(周六)上班。\",\"rest\":\"9月22日至9月24日请假3天，与周末连休可拼8天长假。\"},{\"date\":\"2021-09-22\",\"daycode\":0,\"weekday\":3,\"cnweekday\":\"星期三\",\"lunaryear\":\"辛丑\",\"lunarmonth\":\"八月\",\"lunarday\":\"十六\",\"info\":\"工作日\",\"start\":\"\",\"now\":\"\",\"end\":\"\",\"holiday\":\"\",\"name\":\"\",\"enname\":\"\",\"isnotwork\":0,\"vacation\":\"\",\"remark\":\"\",\"tip\":\"\",\"rest\":\"\"}]";
        JSONArray jsonArray = JSON.parseArray(json);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject oneDay = jsonArray.getJSONObject(i);
            StringBuilder title = new StringBuilder();
            String date = oneDay.getString("date");
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
        }

    }

    public static void main(String[] args) {
        try {
            Test test = new Test();
            // Retrieved during authentication:
//            String authToken = "S=s1:U=3b7:E=17a89e0b303:C=17a65d42d90:P=1cd:A=en-devtoken:V=2:H=a64f7d8893c00a2b48e1fa98b3c7b554";
//            String noteStoreUrl = "https://sandbox.yinxiang.com/shard/s1/notestore";
            String authToken = "S=s50:U=c0f2e0:E=17a9e86d24b:C=17a7a7a4ba8:P=1cd:A=en-devtoken:V=2:H=08752b2b9f1c8458569f18ea75738fa2";
            String noteStoreUrl = "https://app.yinxiang.com/shard/s50/notestore";

/*
//法一
//        String userAgent = myCompanyName + " " + myAppName + "/" + myAppVersion;
            String userAgent = "imoqian" + " " + "blueshark" + "/" + "1.0.0";
            THttpClient noteStoreTrans = new THttpClient(noteStoreUrl);
            noteStoreTrans.setCustomHeader("User-Agent", userAgent);
            TBinaryProtocol noteStoreProt = new TBinaryProtocol(noteStoreTrans);
            NoteStore.Client noteStore = new NoteStore.Client(noteStoreProt, noteStoreProt);

            //获取笔记本列表
//            List<Notebook> notebooks = noteStore.listNotebooks(authToken);
//            for (Notebook notebook: notebooks) {
//                System.out.println(notebook);
//            }
            //获取笔记本
//            Notebook notebook=noteStore.getNotebook(authToken,"743300c1-9f9e-49ed-bd08-f9795965a1f1");

            //创建笔记本
//            Notebook notebook = new Notebook();
//            notebook.setName("My fancy new notebook");
//            Notebook theNewNotebook = noteStore.createNotebook(authToken, notebook);

            //GUID是对象的唯一ID，大概相当于主键ID
//            String guid = theNewNotebook.getGuid();

            //创建笔记
//            Note myNewNote = new Note();
//            myNewNote.setNotebookGuid(guid);
*/

//法二
            EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.PRODUCTION, authToken);
            evernoteAuth.setNoteStoreUrl(noteStoreUrl);
            ClientFactory factory = new ClientFactory(evernoteAuth);
            NoteStoreClient noteStore = factory.createNoteStoreClient();
            List<Notebook> notebooks = noteStore.listNotebooks();
            for (Notebook notebook : notebooks) {
                System.out.println(notebook);
            }

            Notebook notebook = noteStore.getNotebook("743300c1-9f9e-49ed-bd08-f9795965a1f1");
            String title = "2021-07-31 星期六 ";
            String body = "456";
            String body1 = "<div>" +
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
//            test.makeNote(userStore, title, body1, notebook);

        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (TException e) {
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

    public static void main1(String[] args) {

        try {
            String developerToken = "S=s1:U=3b7:E=17a89e0b303:C=17a65d42d90:P=1cd:A=en-devtoken:V=2:H=a64f7d8893c00a2b48e1fa98b3c7b554";
            String noteStoreUrl = "https://sandbox.yinxiang.com/shard/s1/notestore";


            // Set up the NoteStore client
            EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, developerToken);
            evernoteAuth.setNoteStoreUrl(noteStoreUrl);
            ClientFactory factory = new ClientFactory(evernoteAuth);
            NoteStoreClient noteStore = factory.createNoteStoreClient();
            // Make API calls, passing the developer token as the authenticationToken param
            List<Notebook> notebooks = noteStore.listNotebooks();

            for (Notebook notebook : notebooks) {
                System.out.println("Notebook: " + notebook.getName());
            }
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }

    }
}
