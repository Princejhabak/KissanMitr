package com.example.android.myapp.helper;

public class RecorderUploadHelper {

    private String recordingName;
    private String recordingDate;
    private String recordingDownloadUrl;

    public RecorderUploadHelper(){}

    public RecorderUploadHelper(String recordingName, String recordingDate, String recordingDownloadUrl) {
        this.recordingName = recordingName;
        this.recordingDate = recordingDate;
        this.recordingDownloadUrl = recordingDownloadUrl;
    }

    public String getRecordingName() {
        return recordingName;
    }

    public void setRecordingName(String recordingName) {
        this.recordingName = recordingName;
    }

    public String getRecordingDate() {
        return recordingDate;
    }

    public void setRecordingDate(String recordingDate) {
        this.recordingDate = recordingDate;
    }

    public String getRecordingDownloadUrl() {
        return recordingDownloadUrl;
    }

    public void setRecordingDownloadUrl(String recordingDownloadUrl) {
        this.recordingDownloadUrl = recordingDownloadUrl;
    }
}
