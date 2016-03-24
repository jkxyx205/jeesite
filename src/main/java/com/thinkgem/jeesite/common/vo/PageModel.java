package com.thinkgem.jeesite.common.vo;

/**
 * Created by Rick.Xu on 2016/03/22.
 */
public class PageModel {
    private int page;
    private int rows;    //rows == -1 一次性全部加载出来,不再分页
    private String sidx;
    private String sord;
    private String queryName;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getSidx() {
        return sidx;
    }

    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    public String getSord() {
        return sord;
    }

    public void setSord(String sord) {
        this.sord = sord;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }
}
