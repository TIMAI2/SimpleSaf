package uk.co.metricrat.simplesaf;

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
        version = 4,
        versionName = "1.4",
        description = "Extension to access and manipulate files using Storage Access Framework <br> Simplified/Reduced from the full SAF extension developed " +
                "by Sunny Gupta," +
                " a.k.a. " +
                "vknow360",
        iconName = "icon.png",
        category = ComponentCategory.EXTENSION,
        nonVisible = true,
        androidMinSdk = 21
)


public class SimpleSaf extends AndroidNonvisibleComponent implements ActivityResultListener {
    private final Activity activity;
    private final ContentResolver contentResolver;
    private int intentReqCode = 0;
    private String pkgName = "";

    public SimpleSaf(ComponentContainer container) {
        super(container.$form());
        activity = container.$context();
        contentResolver = activity.getContentResolver();
    }

    @Override
    public void resultReturned(int requestCode, int resultCode, Intent intent) {
        if (intentReqCode == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                GotUri(intent.getData(), String.valueOf(intent.getData()));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                GotUri("", "");
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
                ErrorOccurred(method, message);
            }
        });
    }

//###FUNCTIONS###//

    @SimpleFunction(description = "Select a single file")
    public void GetDoc() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        activity.startActivityForResult(Intent.createChooser(intent, "GetDoc"), getIntentReqCode());
    }

    @SimpleFunction(description = "Select a document tree (directory)")
    public void GetDocTree() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        activity.startActivityForResult(Intent.createChooser(intent, "GetTree"), getIntentReqCode());
    }

    @SimpleFunction(description = "Copies document from source uri to the application specific directory (ASD)")
    public void CopyDocToASD(final String sourceUri) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                String name = GetDisplayName(sourceUri);
                pkgName = activity.getPackageName();
                String path = "/storage/emulated/0/Android/data/" + pkgName + "/files";
                File file = new File(path, name);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream is = contentResolver.openInputStream(Uri.parse(sourceUri));
                    byte[] buffers = new byte[4096];
                    int read;
                    while ((read = is.read(buffers)) != -1) {
                        fos.write(buffers, 0, read);
                    }
                    is.close();
                    fos.close();
                    postCtoASDresult(true, file.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                    postCtoASDresult(false, e.getMessage());
                }
            }
        });
    }
//##
    @SimpleFunction(description = "Copies a File from the root of the ASD to a target directory uri")
    public void CopyDocFromASD(final String filename, final String dirUriString){
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                pkgName = activity.getPackageName();
                String path = "/storage/emulated/0/Android/data/" + pkgName + "/files/" + filename;
                File file = new File(path);
                String tpu = BuildDocUriUsingTree(dirUriString,GetTreeDocId(dirUriString));
                try {
                    final Uri uri = DocumentsContract.createDocument(activity.getContentResolver(), Uri.parse(tpu), mimeType(path), filename);
                    InputStream inputStream = new FileInputStream(file);
                    OutputStream outputStream = contentResolver.openOutputStream(uri,"wt");
                    byte[] b = new byte[4096];
                    int c;
                    while ((c = inputStream.read(b)) != -1) {
                        outputStream.write(b, 0, c);
                    }
                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();
                    postASDtoCresult(true,uri.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    postASDtoCresult(false,e.getMessage());
                }
            }
        });
    }

    @SimpleFunction(description = "Deletes document from given uri in Shared Directories and returns filename if successful")
    public void DeleteDoc(String uriString) {
        try {
            String name = GetDisplayName(uriString);
            boolean outcome = DocumentsContract.deleteDocument(activity.getContentResolver(), Uri.parse(uriString));
            if (outcome == true) {
                DocDeleted(name);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new YailRuntimeError(e.getMessage(), "SAF");
        }
    }

    @SimpleFunction(description = "Returns display name of given document uri")
    public String GetDisplayName(final String documentUri) {
        try {
            return getStringValue(documentUri, DocumentsContract.Document.COLUMN_DISPLAY_NAME);
        } catch (Exception e) {
            postError("DisplayName", e.getMessage());
        }
        return "";
    }

    @SimpleFunction(description = "Returns mime type of given document uri")
    public String GetMimeType(final String documentUri) {
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
    public String DocDirMimeType() {
        return DocumentsContract.Document.MIME_TYPE_DIR;
    }

//###PROPERTIESS###//

//###EVENTS###//

    @SimpleEvent(description = "Event invoked when user selects a document or tree (directory) from SAF file picker")
    public void GotUri(Object uri, String uriString) {
        EventDispatcher.dispatchEvent(this, "GotUri", uri, uriString);
    }

    @SimpleEvent(description = "Event raised after getting 'CopyDocToASD' result")
    public void DocCopiedToASD(boolean successful, String response) {
        EventDispatcher.dispatchEvent(this, "DocCopiedToASD", successful, response);
    }

    @SimpleEvent(description = "Event raised after getting 'CopyDocFromASD' result")
    public void DocCopiedFromASD(boolean successful, String response) {
        EventDispatcher.dispatchEvent(this, "DocCopiedFromASD", successful, response);
    }


    @SimpleEvent(description = "Event invoked when user deletes a document")
    public void DocDeleted(String filename) {
        EventDispatcher.dispatchEvent(this, "DocDeleted", filename);
    }

    @SimpleEvent(description = "Event indicating error/exception has occurred and returns origin method and error message.")
    public void ErrorOccurred(String methodName, String errorMessage) {
        EventDispatcher.dispatchEvent(this, "ErrorOccurred", methodName, errorMessage);
    }
//###EVENTS###//

//###PRIVATES###//

    private String BuildDocUriUsingTree(String treeUri, String documentId) {
        return DocumentsContract.buildDocumentUriUsingTree(Uri.parse(treeUri), documentId).toString();
    }

    private String GetTreeDocId(String uriString) {
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

    private void postCtoASDresult(final boolean successful, final String response) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DocCopiedToASD(successful, response);
            }
        });
    }

    private void postASDtoCresult(final boolean successful, final String response) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DocCopiedFromASD(successful, response);
            }
        });
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