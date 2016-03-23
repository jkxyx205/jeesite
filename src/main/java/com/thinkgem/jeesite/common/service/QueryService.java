package com.thinkgem.jeesite.common.service;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.SQL.SqlFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by Rick.Xu on 2016/1/2.
 */
@Service
@Transactional(readOnly = true)
public class QueryService {
    private static Logger logger = LoggerFactory.getLogger(QueryService.class);

    public static final String PARAM_IN_SEPERATOR = ";";

    private static final String QUERY_NAME = "queryName";

    private static final String PAGE_OBJECT_PROP = "page";

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Resource
    private JdbcTemplate jdbcTemplate;

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
    public Page<Map<String, Object>> findListByParams(HttpServletRequest request,HttpServletResponse response, String queryName, Map<String, Object> supplementParams)  {
        Map<String, Object> params  = getParametersAsMap(true, request);
        params.putAll(supplementParams);
        if (StringUtils.isNotBlank(queryName))
            params.put(QUERY_NAME, queryName);

        return findPage(new Page<Map<String, Object>>(request, response), params);
    }

    public Page<Map<String, Object>> findListByParams(HttpServletRequest request,HttpServletResponse response, String queryName)   {
        return findListByParams(request, response, queryName, Collections.EMPTY_MAP);
    }

    public Page<Map<String, Object>> findListByParams(HttpServletRequest request,HttpServletResponse response)  {
        return findListByParams(request, response, null, Collections.EMPTY_MAP);
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

    public static Map<String,Object> getParametersAsMap(boolean skipBlank,HttpServletRequest request) {
        Enumeration<String> en = request.getParameterNames();
        Map<String, Object> map = new HashMap<String, Object>();
        while(en.hasMoreElements()) {
            String name = en.nextElement();
            String[] values = request.getParameterValues(name);

            //多选 会在name后面加[]
            name = name.replace("[]", "");

            if(values != null) {
                if(values.length>1) {
                    StringBuilder sb = new StringBuilder();
                    for(String v:values) {
                        if(skipBlank && StringUtils.isBlank(v))
                            continue;
                        sb.append(v).append(";");
                    }
                    sb.deleteCharAt(sb.length()-1);

                    map.put(name, sb.toString());
                } else  {
                    String value = values[0];
                    if(skipBlank && StringUtils.isBlank(value))
                        continue;
                    map.put(name,value);
                }
            }
        }
        return map;
    }

    private String getQueryStringByqueryName(String queryName, Map<String, Object> param) {
        MappedStatement mappedStatement = sqlSessionFactory.getConfiguration().getMappedStatement(queryName);
        String sql = mappedStatement.getBoundSql(param).getSql();
        return sql;
    }

    private <T> T queryForSpecificParamSQL(String sql,
                                          Map<String, Object> param,
                                          String paramInSeperator,
                                          JdbcTemplateExecutor<T> jdbcTemplateExecutor) {
        Map<String, Object> formatMap = new HashMap<String, Object>();
        String formatSql = SqlFormatter.formatSql(sql, param, formatMap,
                paramInSeperator);

        Object[] args = NamedParameterUtils.buildValueArray(formatSql,
                formatMap);
        formatSql = formatSql.replaceAll(SqlFormatter.PARAM_REGEX,"?"); //mysql
        return jdbcTemplateExecutor.query(jdbcTemplate, formatSql, args);
    }

    public <T> T queryForSpecificParam(String queryName,
                                       Map<String, Object> param,
                                       JdbcTemplateExecutor<T> jdbcTemplateExecutor) {
        return queryForSpecificParam(queryName, param, PARAM_IN_SEPERATOR,
                jdbcTemplateExecutor);
    }

    public <T> T queryForSpecificParam(String queryName,
                                       Map<String, Object> param, String paramInSeperator,
                                       JdbcTemplateExecutor<T> jdbcTemplateExecutor) {
        String sql = getQueryStringByqueryName(queryName, param);
        return  queryForSpecificParamSQL(sql, param, paramInSeperator,jdbcTemplateExecutor);
    }

    public long queryForSpecificParamCount(String queryName,
                                           Map<String, Object> param) {
        String sql = getQueryStringByqueryName(queryName, param);
        return  queryForSpecificParamSQL(sql, param, PARAM_IN_SEPERATOR,new JdbcTemplateExecutor<Long>() {

            @Override
            public Long query(JdbcTemplate jdbcTemplate, String queryString, Object[] args) {
                return jdbcTemplate.queryForObject(queryString, args, Long.class);
            }
        });
        //
    }

    public interface JdbcTemplateExecutor<T> {
        public T query(JdbcTemplate jdbcTemplate, String queryString,
                       Object[] args);
    }
}