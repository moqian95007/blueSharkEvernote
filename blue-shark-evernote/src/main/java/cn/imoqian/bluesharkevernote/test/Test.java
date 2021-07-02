package cn.imoqian.bluesharkevernote.test;

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

import java.util.List;

public class Test {
    public static void main(String[] args) {
        try {
            // Retrieved during authentication:
            String authToken = "S=s1:U=3b7:E=17a89e0b303:C=17a65d42d90:P=1cd:A=en-devtoken:V=2:H=a64f7d8893c00a2b48e1fa98b3c7b554";
            String noteStoreUrl = "https://sandbox.yinxiang.com/shard/s1/notestore";

//        String userAgent = myCompanyName + " " + myAppName + "/" + myAppVersion;
            String userAgent = "imoqian" + " " + "blueshark" + "/" + "1.0.0";

            THttpClient noteStoreTrans = new THttpClient(noteStoreUrl);
            noteStoreTrans.setCustomHeader("User-Agent", userAgent);
            TBinaryProtocol noteStoreProt = new TBinaryProtocol(noteStoreTrans);
            NoteStore.Client noteStore = new NoteStore.Client(noteStoreProt, noteStoreProt);

            //获取笔记本列表
            List<Notebook> notebooks = noteStore.listNotebooks(authToken);

            //创建笔记本
            Notebook notebook = new Notebook();
            notebook.setName("My fancy new notebook");
            Notebook theNewNotebook = noteStore.createNotebook(authToken, notebook);

            //GUID是对象的唯一ID，大概相当于主键ID
            String guid = theNewNotebook.getGuid();

            //创建笔记
            Note myNewNote = new Note();
            myNewNote.setNotebookGuid(guid);


        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (TException e) {
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
