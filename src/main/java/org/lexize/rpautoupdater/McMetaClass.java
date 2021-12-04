package org.lexize.rpautoupdater;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class McMetaClass {
    @SerializedName("download_url")
    String downloadUrl;
    @SerializedName("check_url")
    String checkUrl;
    @SerializedName("version")
    float version;
}
