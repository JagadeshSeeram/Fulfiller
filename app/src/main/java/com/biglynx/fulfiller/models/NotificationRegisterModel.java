package com.biglynx.fulfiller.models;


import java.util.List;

public class NotificationRegisterModel {
    private String Platform;
    private String Handle;
    private List<String> Tags;

    public String getPlatform() {
        return Platform;
    }

    public void setPlatform(String platform) {
        this.Platform = platform;
    }

    public String getHandle() {
        return Handle;
    }

    public void setHandle(String handle) {
        this.Handle = handle;
    }

    public List<String> getTags() {
        return Tags;
    }

    public void setTags(List<String> tags) {
        this.Tags = tags;
    }

    public static class TagsArray{
        private String tag;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }
}
