package com.thinkgem.jeesite.common.vo;

import java.util.List;
import java.util.Map;

/**
 * Created by Rick.Xu on 2016/03/24.
 */
public class ReportPageModel extends PageModel {
    private String url;

    private Map<String, Object> postData;

    private String[] colNames;

    private List<Map<String,Object>> colModel;

    private String fileName; //export file name

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getPostData() {
        return postData;
    }

    public void setPostData(Map<String, Object> postData) {
        this.postData = postData;
    }

    public String[] getColNames() {
        return colNames;
    }

    public void setColNames(String[] colNames) {
        this.colNames = colNames;
    }

    public List<Map<String, Object>> getColModel() {
        return colModel;
    }

    public void setColModel(List<Map<String, Object>> colModel) {
        this.colModel = colModel;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
