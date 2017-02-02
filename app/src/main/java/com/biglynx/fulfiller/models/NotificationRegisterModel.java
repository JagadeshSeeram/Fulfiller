package com.biglynx.fulfiller.models;


import java.util.List;

public class NotificationRegisterModel {
    private String Platform;
    private String Handle;
    private List<TagsArray> Tags;

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

    public List<TagsArray> getTags() {
        return Tags;
    }

    public void setTags(List<TagsArray> tags) {
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
