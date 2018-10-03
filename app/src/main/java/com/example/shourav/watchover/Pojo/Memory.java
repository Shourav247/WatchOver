package com.example.shourav.watchover.Pojo;

/**
 * Created by Musa on 9/19/2018.
 */

public class Memory {
    private String title;
    private String usedSpace;
    private String freeSpace;
    private String totalSpace;

    public Memory(String title, String usedSpace, String freeSpace, String totalSpace) {
        this.title = title;
        this.usedSpace = usedSpace;
        this.freeSpace = freeSpace;
        this.totalSpace = totalSpace;
    }

    public String getTitle() {
        return title;
    }

    public String getUsedSpace() {
        return usedSpace;
    }

    public String getFreeSpace() {
        return freeSpace;
    }

    public String getTotalSpace() {
        return totalSpace;
    }

}
