package com.thinkgem.jeesite.common.service;

import com.thinkgem.jeesite.common.utils.SQL.SqlFormatter;
import com.thinkgem.jeesite.common.vo.JqGrid;
import com.thinkgem.jeesite.common.vo.PageModel;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rick.Xu on 2016/03/22.
 */
@Service
public class JqgridService {
    private static final String JQGIRD_PARAM_SORD = "sord";
    private static final String JQGIRD_PARAM_PAGE = "page";
    private static final String JQGIRD_PARAM_ROW = "rows";
    private static final String JQGIRD_PARAM_QUERYNAME = "queryName";
    private static final String JQGIRD_PARAM_SIDX = "sidx";
    private static final String DICT_TYPE = "dict";

    @Resource
    private JdbcTemplateService  JdbcTemplateService;

    public JqGrid getJqgirdData(HttpServletRequest request) throws Exception {
        Map<String,Object> param = JdbcTemplateService.getParametersAsMap(true, request);
        PageModel model = getPageModel(param);
        return getJqgirdData(model,param);

    }
    public JqGrid getJqgirdData(final PageModel model,Map<String,Object> param) throws Exception {
        long count = 0;

        if (model.getRows() != -1) {
            count = JdbcTemplateService.queryForSpecificParam(model.getQueryName(), param, new JdbcTemplateService.JdbcTemplateExecutor<Long>() {

                @Override
                public Long query(JdbcTemplate jdbcTemplate, String queryString, Object[] args) {
                    queryString = SqlFormatter.formatSqlCount(queryString);
                    return jdbcTemplate.queryForObject(queryString, args, Long.class);
                }
            });

            if (model.getRows() >= count){
                model.setPage(1);
            }
        }

        List<Map<String, Object>> rows = JdbcTemplateService.queryForSpecificParam(model.getQueryName(), param, new JdbcTemplateService.JdbcTemplateExecutor<List<Map<String, Object>>>() {

            public List<Map<String, Object>> query(JdbcTemplate jdbcTemplate,
                                                   String queryString, Object[] args) {
                queryString = wrapSordString(queryString, model.getSidx(), model.getSord());
                if(model.getRows() != -1) {
                    queryString = pageSql(queryString,model);
                }
                //
                //return jdbcTemplate.queryForList(sql, args);
                List<Map<String, Object>>  ret = jdbcTemplate.queryForList(queryString, args);
                translate(ret, model.getDicMap());
                return ret;

            }
        });
        if(model.getRows() == -1) {
            count = rows.size();
        }

        JqGrid bo = new JqGrid();

        long total;

        if(model.getRows() != -1) {
            if(count%model.getRows() == 0) {
                total = count/model.getRows();
            } else {
                total = count/model.getRows() + 1;
            }
            bo.setTotal(total);
            bo.setPage(model.getPage());
        }

        bo.setRows(rows);
        bo.setRecords(count);

        return bo;
    }

    private void translate(List<Map<String, Object>>  ret, Map<String, String> dicMap) {
        if (dicMap == null) return;

        for(Map<String, Object> m: ret) {
            Set<String> names = m.keySet();
            for(String name : names) {
                if (dicMap.containsKey(name)) {
                    Object value = translate(dicMap.get(name), m.get(name));
                    m.put(name, value);
                }
            }
        }
    }

    public static String wrapSordString(String sql,String sidx, String sord) {
        StringBuilder sb = new StringBuilder("SELECT * FROM (");
        sb.append(sql).append(") temp");
        if(StringUtils.isNotBlank(sidx) && StringUtils.isNotBlank(sord)) {
            sb.append(" ORDER BY ").append(sidx).append(" ").append(sord);
            return sb.toString();
        } else {
            return sql;
        }
    }
    private Object translate(String dict,Object value) {
        if(value!= null && (value.getClass() == String.class)) {
            String v = (String)value;
            if(StringUtils.isNotBlank(v)) {
                String[] vs = v.split(",");
                if(vs.length == 1) {
                    return DictUtils.getDictLabel((String)value,dict,"");
                } else {
                    return DictUtils.getDictLabels((String)value,dict,"");
                }
            }
        }
        return value;
    }

    private PageModel getPageModel(Map<String,Object> param) throws IOException {
        PageModel model = new PageModel();
        model.setQueryName((String) param.get(JQGIRD_PARAM_QUERYNAME));
        if (param.get(JQGIRD_PARAM_PAGE) == null) param.put(JQGIRD_PARAM_PAGE,"1");
        if (param.get(JQGIRD_PARAM_ROW) == null) param.put(JQGIRD_PARAM_ROW,"-1");
        model.setPage(Integer.parseInt(param.get(JQGIRD_PARAM_PAGE).toString()));
        model.setRows(Integer.parseInt(param.get(JQGIRD_PARAM_ROW).toString()));

        model.setSord((String) param.get(JQGIRD_PARAM_SORD));
        model.setSidx((String) param.get(JQGIRD_PARAM_SIDX));

        ObjectMapper mapper = new ObjectMapper();
        Object dicString = param.get(DICT_TYPE);
        if (dicString != null)
            model.setDicMap(mapper.readValue((String)dicString, Map.class));

        return model;
    }

    private String pageSql(String sql, PageModel model) {
        if (model == null) {
            model = new PageModel();
        }
        return SqlFormatter.pageSql(sql,model);
    }
}
