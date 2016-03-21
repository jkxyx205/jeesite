package com.thinkgem.jeesite.common.web;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.QueryService;
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

    @RequestMapping(value = "jqgridList")
    @ResponseBody
    public Map<String, Object> memberList(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
}
