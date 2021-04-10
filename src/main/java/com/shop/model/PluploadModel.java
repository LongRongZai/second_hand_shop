package com.shop.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class PluploadModel {

    public PluploadModel() {
    }

    private Long id;
    private Integer uuid;
    private String name;
    private String path;
    private String viewPath;
    private String size;
    private String type;
    private String jsonValue;
    private String timeLong;

    public String getJsonValue() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("attachName", this.name);
        jsonObject.put("attachUrl", this.path);
        jsonObject.put("attachSize", this.size);
        jsonObject.put("attachType", this.type);
        return jsonObject.toJSONString();
    }

}
