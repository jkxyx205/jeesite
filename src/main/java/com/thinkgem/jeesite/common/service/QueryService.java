package com.thinkgem.jeesite.common.service;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.vo.JqGrid;
import com.thinkgem.jeesite.common.vo.PageModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Rick.Xu on 2016/1/2.
 */
@Service
@Transactional(readOnly = true)
public class QueryService {
    private static Logger logger = LoggerFactory.getLogger(QueryService.class);

    private static final String QUERY_NAME = "queryName";

    private static final String PAGE_OBJECT_PROP = "page";

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Resource
    private JqgridService jqgridService;

    /***
     * usually used by web site
     * @param request
     * @param response
     * @param queryName if the queryName not in request params,you can specify yourself
     * @param supplementParams if some parmas not in request,you can specify yourself
     * @return
     * all the row in the list
     *  in map the key is the colunm lable and the value is cell value
     */
    public Page<Map<String, Object>> findPageByParams(HttpServletRequest request,HttpServletResponse response, String queryName, Map<String, Object> supplementParams)  {
        Map<String, Object> params  = JdbcTemplateService.getParametersAsMap(true, request);
        params.putAll(supplementParams);
        if (StringUtils.isNotBlank(queryName))
            params.put(QUERY_NAME, queryName);

        return findPage(new Page<Map<String, Object>>(request, response), params);
    }

    /***
     * mybatis 查询模式
     * @param request
     * @param response
     * @param queryName
     * @return
     */
    public Page<Map<String, Object>> findPageByParams(HttpServletRequest request,HttpServletResponse response, String queryName)   {
        return findPageByParams(request, response, queryName, Collections.EMPTY_MAP);
    }

    /**
     * jdbcTemplate 查询模式 支持in 不定参数查询
     * @param request
     * @param response
     * @param queryName
     * @return
     * @throws Exception
     */
    public Page<Map<String, Object>> findPageByParams2(HttpServletRequest request,HttpServletResponse response, String queryName) throws Exception {
        Page<Map<String, Object>> page = new Page<Map<String, Object>>(request, response);
        Map<String,Object> params = JdbcTemplateService.getParametersAsMap(true,request);
        PageModel pageModel = new PageModel();
        pageModel.setQueryName(queryName);
        pageModel.setPage(page.getPageNo());
        pageModel.setRows(page.getPageSize());
        JqGrid jqGrid = jqgridService.getJqgirdData(pageModel,params);
        page.setCount(jqGrid.getRecords());
        page.setList(jqGrid.getRows());
        return page;
    }

    public Page<Map<String, Object>> findPageByParams(HttpServletRequest request,HttpServletResponse response)  {
        return findPageByParams(request, response, null, Collections.EMPTY_MAP);
    }

    /**
     * usually used by java code
     * @param params
     * @param queryName
     * @return
     */
    public List<Map<String, Object>> findListByParams(String queryName, Map<String, Object> params)   {
        SqlSession session = sqlSessionFactory.openSession();
        List<Map<String, Object>> list = session.selectList(queryName, params);
        session.close();
        return  list;
    }

    private Page<Map<String, Object>> findPage(Page<Map<String, Object>> page, Map<String, Object> params)   {
        String queryName = (String)params.get(QUERY_NAME);
        params.put(PAGE_OBJECT_PROP, page);
        List<Map<String, Object>> list = findListByParams(queryName,params);
        page.setList(list);
        return  page;
    }
}