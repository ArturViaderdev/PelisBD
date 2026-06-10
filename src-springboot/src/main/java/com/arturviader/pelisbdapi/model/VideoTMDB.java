package com.arturviader.pelisbdapi.model;

import lombok.Data;

@Data
public class VideoTMDB {
    private String id;
    private String iso_639_1;
    private String name;

    public VideoTMDB(String id, String iso_639_1, String name, String site, String size, String type, String key) {
        this.id = id;
        this.iso_639_1 = iso_639_1;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String site;
    private String size;
    private String type;
    private String key;
}
