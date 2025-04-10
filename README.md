Developed from the original and full [SAF extension for AppInventor by vknow360](https://github.com/vknow360/SAF)

# SimpleSaf
Simplified and reduced extension for App Inventor implementation of Storage Access Framework <br>
This version can be used to copy a foreign file to the ASD, and then replace the original foreign file with the one now "owned" by the app<br>
**Latest Version:** 1.4

## Blocks
Offers user a chooser to select a single file from their selected directory (no default directory).<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/GetDoc_Method.png"/><br>
Offers user a chooser to select a directory, and for them to "Use the folder" and give access permission.<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/GetDocTree_Method.png"/><br>
Copies the selected document (using the uriString) to the device's ASD.<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/CopyDocToASD_Method.png"/><br>
Deletes a selected document (using the uriString).<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/DeleteDoc_Method.png"/><br>
Copies a selected file in the "root" of the ASD to a selected directory, chosen by the user.<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/CopyDocFromASD_Method.png"/><br>
Returns the filename of a document uriString.<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/GetDisplayName_Method.png"/><br>
Returns the mimetype of a document uriString.<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/GetMimeType_Method.png"/><br>
Event that returns the object uri and uriString for a selected document or directory.<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/GotUri_Event.png"/><br>
Event that returns the success of a copy to ASD, and the path.<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/DocCopiedToASD_Event.png"/><br>
Event that returns the success of a copy from the ASD, and the path.<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/DocCopiedFromASD_Event.png"/><br>
Event that returns the filname of the deleted document, if successful.<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/DocDeleted_Event.png"/><br>
Event that returns errors. If left blank, then hides any errors.<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/ErrorOccurred_Event.png"/><br>
Property that provides a directroy mimetype.<br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/simplesaf/DocDirMimeType_Get_Property.png"/><br>

## Usages
1) https://community.appinventor.mit.edu/t/saf-app-inventor-implementation-of-storage-access-framework/41603

# TextDocSaf
Simplified and reduced extension for App Inventor implementation of Storage Access Framework <br>
This version can be used for full C R U D of text/csv files (foreign or otherwise) in the Shared Directories<br>
**Latest Version:** 1.0

## Blocks
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/GetTextDoc_Method.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/GetTextDocTree_Method.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/CreateTextDoc_Method.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/ReadFromTextDoc_Method.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/WriteToTextDoc_Method.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/DeleteTextDoc_Method.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/GetTextDocDisplayName_Method.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/GetTextDocMimeType_Method.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/GotTextUri_Event.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/TextDocCreated_Event.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/GotTextReadResult_Event.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/GotTextWriteResult_Event.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/TextDocDeleted_Event.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/TextExtnErrorOccurred_Event.png"/><br>
<img src="https://github.com/TIMAI2/SimpleSaf/blob/main/images/textdocsaf/DirMimeType_Get_Property.png"/><br>



## Usages
1) https://community.appinventor.mit.edu/t/saf-app-inventor-implementation-of-storage-access-framework/41603



