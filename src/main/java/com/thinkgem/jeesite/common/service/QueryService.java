package com.thinkgem.jeesite.common.service;

import com.thinkgem.jeesite.common.persistence.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final String SQL_ID = "sqlId";

    private static final String PAGE_OBJECT_PROP = "page";

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    /***
     * usually used by web site
     * @param request
     * @param response
     * @param sqlId if the sqlId not in request params,you can specify yourself
     * @param supplementParams if some parmas not in request,you can specify yourself
     * @return
     * all the row in the list
     *  in map the key is the colunm lable and the value is cell value
     */
    public Page<Map<String, Object>> findListByParams(HttpServletRequest request,HttpServletResponse response, String sqlId, Map<String, Object> supplementParams)  {
        Map<String, Object> params  = getParametersAsMap(true, request);
        params.putAll(supplementParams);

        if (StringUtils.isNotBlank(sqlId))
            params.put(SQL_ID, sqlId);

        return findPage(new Page<Map<String, Object>>(request, response), params);
    }

    public Page<Map<String, Object>> findListByParams(HttpServletRequest request,HttpServletResponse response, String sqlId)   {
        return findListByParams(request, response, sqlId, Collections.EMPTY_MAP);
    }

    public Page<Map<String, Object>> findListByParams(HttpServletRequest request,HttpServletResponse response)  {
        return findListByParams(request, response, null, Collections.EMPTY_MAP);
    }

    /**
     * usually used by java code
     * @param params
     * @param sqlId
     * @return
     */
    public List<Map<String, Object>> findListByParams(String sqlId, Map<String, Object> params)   {
        SqlSession session = sqlSessionFactory.openSession();

        List<Map<String, Object>> list = session.selectList(sqlId, params);
        session.close();
        return  list;
    }

    private Page<Map<String, Object>> findPage(Page<Map<String, Object>> page, Map<String, Object> params)   {
        String sqlId = (String)params.get(SQL_ID);
        params.put(PAGE_OBJECT_PROP, page);
        List<Map<String, Object>> list = findListByParams(sqlId,params);
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
}