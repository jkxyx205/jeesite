package com.thinkgem.jeesite.common.web;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.JqgridService;
import com.thinkgem.jeesite.common.service.QueryService;
import com.thinkgem.jeesite.common.vo.JqGrid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rick on 2016/3/20.
 */

@Controller
@RequestMapping(value = "${adminPath}/common")
public class QueryController {

    @Resource
    private QueryService queryService;

    @Resource
    private JqgridService jqgridService;

    /**
     * Page的实现方式，原生的实现方式
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "jqgridList")
    @ResponseBody
    public Map<String, Object> pageList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Page<Map<String, Object>> page = queryService.findListByParams(request, response);
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

    @RequestMapping(value = "jqgridList2")
    @ResponseBody
    public JqGrid mapList(HttpServletRequest request) throws Exception {
        return jqgridService.getJqgirdData(request);
    }
}
