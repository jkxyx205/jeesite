package com.thinkgem.jeesite.common.web;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.JqgridService;
import com.thinkgem.jeesite.common.service.QueryService;
import com.thinkgem.jeesite.common.service.ReportService;
import com.thinkgem.jeesite.common.vo.JqGrid;
import com.thinkgem.jeesite.common.vo.ReportPageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rick on 2016/3/20.
 */

@Controller
@RequestMapping(value = "${adminPath}/common")
public class CommonController {
    private static final transient Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Resource
    private JqgridService jqgridService;

    @Resource
    private QueryService queryService;

    @Resource
    private ReportService reportService;

    @RequestMapping(value = "list",method=RequestMethod.POST)
    @ResponseBody
    public JqGrid mapList(HttpServletRequest request) throws Exception {
        return jqgridService.getJqgirdData(request);
    }

    /**
     * Page的实现方式，原生的实现方式
     * @param request
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "list2")
    @ResponseBody
    public Map<String, Object> pageList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Page<Map<String, Object>> page = queryService.findPageByParams(request, response);
        Map<String, Object> jqGridJson = new HashMap<String, Object>(4);

        long totalPage = page.getCount() / page.getPageSize();
        if (page.getCount() % page.getPageSize() != 0) {
            totalPage++;
        }
        jqGridJson.put("page", page.getPageNo()); //当前页
        jqGridJson.put("total", totalPage); //多少页
        jqGridJson.put("records",page.getCount());
        jqGridJson.put("rows", page.getList());
        return jqGridJson;
    }

    @RequestMapping(value = "report",method= RequestMethod.POST)
    public void report(HttpServletRequest request, HttpServletResponse response) throws Exception {
        reportService.report(request, response);
    }
}
