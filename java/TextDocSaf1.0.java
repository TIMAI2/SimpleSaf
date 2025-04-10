package uk.co.metricrat.textdocsaf;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.*;
import com.google.appinventor.components.runtime.ActivityResultListener;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.errors.YailRuntimeError;
import com.google.appinventor.components.runtime.util.AsynchUtil;

import java.io.*;
import java.lang.*;
import java.net.FileNameMap;
import java.net.URLConnection;

@DesignerComponent(
        version = 1,
        versionName = "1.0",
        description = "Extension to access and manipulate text/csv files using Storage Access Framework <br> Simplified/Reduced from the full SAF extension developed " +
                "by Sunny Gupta," +
                " a.k.a. " +
                "vknow360",
        iconName = "icon.png",
        category = ComponentCategory.EXTENSION,
        nonVisible = true,
        androidMinSdk = 21
)

public class TextDocSaf extends AndroidNonvisibleComponent implements ActivityResultListener {
    private final Activity activity;
    private final ContentResolver contentResolver;
    private int intentReqCode = 0;
    private String pkgName = "";

    public TextDocSaf(ComponentContainer container) {
        super(container.$form());
        activity = container.$context();
        contentResolver = activity.getContentResolver();
    }

    @Override
    public void resultReturned(int requestCode, int resultCode, Intent intent) {
        if (intentReqCode == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                GotTextUri(intent.getData(), String.valueOf(intent.getData()));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                GotTextUri("", "");
            }
        }
    }

    private int getIntentReqCode() {
        if (intentReqCode == 0) {
            this.intentReqCode = form.registerForActivityResult(this);
        }
        return intentReqCode;
    }

    private void postError(final String method, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextExtnErrorOccurred(method, message);
            }
        });
    }

//###FUNCTIONS###//

    @SimpleFunction(description = "Select a single text file")
    public void GetTextDoc() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        activity.startActivityForResult(Intent.createChooser(intent, "GetDoc"), getIntentReqCode());
    }

    @SimpleFunction(description = "Select a document tree for text files")
    public void GetTextDocTree() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        activity.startActivityForResult(Intent.createChooser(intent, "GetTree"), getIntentReqCode());
    }

    @SimpleFunction(description = "Creates a new and empty text document.If document already exists then an incremental value will be suffixed. Mimetypes: " +
            "'text/plain' for text files, 'text/comma-separated-values' for csv files")
    public void CreateTextDoc(final String dirUriString, final String fileName, final String mimeType) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                String pdu = BuildTextDocUriUsingTree(dirUriString,GetTextTreeDocId(dirUriString));
                try {
                    final String uri = DocumentsContract.createDocument(activity.getContentResolver(), Uri.parse(pdu), mimeType, fileName).toString();
                    postTextCreateResult(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                    postTextCreateResult(e.getMessage());
                }
            }
        });
    }

    @SimpleFunction(description = "Reads from given document as text. Can also read text files from assets, but these are read only!")
    public void ReadFromTextDoc(final String uriString) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                if (!GetTextDocMimeType(uriString).equals(DirMimeType())) {
                    String res;
                    try {
                        if (uriString.startsWith("//")) {
                            InputStream is = form.getAssets().open(uriString.substring(2));
                            res = readTextFromInputStream(is);
                        } else {
                            res = readTextFromInputStream(contentResolver.openInputStream(Uri.parse(uriString)));
                        }
                    } catch (Exception e) {
                        res = e.getMessage();
                    }
                    postTextReadResult(res);
                } else {
                    postError("ReadFromFile", "Can't read text from dir");
                }
            }
        });
    }

    @SimpleFunction(description = "Writes content as text to given text doc uri")
    public void WriteToTextDoc(final String uriString, final String content) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                if (!GetTextDocMimeType(uriString).equals(DirMimeType())) {
                    String res;
                    try {
                        OutputStream fileOutputStream = contentResolver.openOutputStream(Uri.parse(uriString), "wt");
                        res = writeTextToOutputStream(fileOutputStream, content);
                        res = res.isEmpty() ? uriString : res;
                    } catch (Exception e) {
                        res = e.getMessage();
                    }
                    postTextWriteResult(res);
                } else {
                    postError("WriteToFile", "Can't write text to dir");
                }
            }
        });
    }

    @SimpleFunction(description = "Deletes text doc from given uri and returns result")
    public void DeleteTextDoc(String uriString) {
        try {
            String name = GetTextDocDisplayName(uriString);
            boolean outcome = DocumentsContract.deleteDocument(activity.getContentResolver(), Uri.parse(uriString));
            if (outcome == true) {
                TextDocDeleted(name);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new YailRuntimeError(e.getMessage(), "SAF");
        }
    }

    @SimpleFunction(description = "Returns display name of given text doc uri")
    public String GetTextDocDisplayName(final String documentUri) {
        try {
            return getStringValue(documentUri, DocumentsContract.Document.COLUMN_DISPLAY_NAME);
        } catch (Exception e) {
            postError("DisplayName", e.getMessage());
        }
        return "";
    }

    @SimpleFunction(description = "Returns mime type of given text doc uri")
    public String GetTextDocMimeType(final String documentUri) {
        try {
            return getStringValue(documentUri, DocumentsContract.Document.COLUMN_MIME_TYPE);
        } catch (Exception e) {
            postError("MimeType", e.getMessage());
        }
        return "";
    }

//###FUNCTIONS###//

//###PROPERTIES###//

    @SimpleProperty(description = "Returns mime type for a directory")
    public String DirMimeType() {
        return DocumentsContract.Document.MIME_TYPE_DIR;
    }

//###PROPERTIESS###//

//###EVENTS###//

    @SimpleEvent(description = "Event invoked when user selects a document or tree from SAF file picker")
    public void GotTextUri(Object uri, String uriString) {
        EventDispatcher.dispatchEvent(this, "GotTextUri", uri, uriString);
    }

    @SimpleEvent(description = "Event invoked after creating document.Returns document's uri if operation was successful else returns error message")
    public void TextDocCreated(String uriString) {
        EventDispatcher.dispatchEvent(this, "TextDocCreated", uriString);
    }

    @SimpleEvent(description = "Event invoked after reading from document.Returns content if operation was successful else returns error message")
    public void GotTextReadResult(Object result) {
        EventDispatcher.dispatchEvent(this, "GotTextReadResult", result);
    }

    @SimpleEvent(description = "Event invoked after writing to document.Returns document's uri if operation was successful else returns error message")
    public void GotTextWriteResult(String response) {
        EventDispatcher.dispatchEvent(this, "GotTextWriteResult", response);
    }

    @SimpleEvent(description = "Event invoked when user deletes a document")
    public void TextDocDeleted(String filename) {
        EventDispatcher.dispatchEvent(this, "TextDocDeleted", filename);
    }

    @SimpleEvent(description = "Event indicating error/exception has occurred and returns origin method and error message.")
    public void TextExtnErrorOccurred(String methodName, String errorMessage) {
        EventDispatcher.dispatchEvent(this, "TextExtnErrorOccurred", methodName, errorMessage);
    }

//###EVENTS###//

//###PRIVATES###//

    private String BuildTextDocUriUsingTree(String treeUri, String documentId) {
        return DocumentsContract.buildDocumentUriUsingTree(Uri.parse(treeUri), documentId).toString();
    }

    private String GetTextTreeDocId(String uriString) {
        return DocumentsContract.getTreeDocumentId(Uri.parse(uriString));
    }

    private String getStringValue(String documentUri, String projection) throws Exception {
        Cursor cursor = activity.getContentResolver().query(Uri.parse(documentUri),
                new String[]{projection},
                null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            cursor.close();
        }
        return "";
    }

    private void postTextCreateResult(final String uriString) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextDocCreated(uriString);
            }
        });
    }

    private String writeTextToOutputStream(OutputStream fileOutputStream, String content) {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(fileOutputStream);
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void postTextWriteResult(final String response) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GotTextWriteResult(response);
            }
        });
    }

    private String readTextFromInputStream(InputStream fileInputStream) {
        InputStreamReader input = new InputStreamReader(fileInputStream);
        try {
            StringWriter output = new StringWriter();
            int BUFFER_LENGTH = 4096;
            char[] buffer = new char[BUFFER_LENGTH];
            int offset = 0;
            int length;
            while ((length = input.read(buffer, offset, BUFFER_LENGTH)) > 0) {
                output.write(buffer, 0, length);
            }
            return normalizeTextNewLines(output.toString());
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            try {
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void postTextReadResult(final Object r) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GotTextReadResult(r);
            }
        });
    }

    private String normalizeTextNewLines(String s) {
        return s.replaceAll("\r\n", "\n");
    }

    private String mimeType(String fileName) {
        try {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            return fileNameMap.getContentTypeFor(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

//###PRIVATES###//

}