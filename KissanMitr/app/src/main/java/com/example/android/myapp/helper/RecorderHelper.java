package com.example.android.myapp.helper;

public class RecorderHelper {

    private String recordingName;
    private String recordingDate;
    private String recordingLength;

    public RecorderHelper(){}

    public RecorderHelper(String recordingName, String recordingDate, String recordingLength){

        this.recordingName = recordingName;
        this.recordingDate = recordingDate;
        this.recordingLength = recordingLength;
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

    public String getRecordingLength() {
        return recordingLength;
    }

    public void setRecordingLength(String recordingLength) {
        this.recordingLength = recordingLength;
    }

}
