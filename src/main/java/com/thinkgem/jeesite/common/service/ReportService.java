package com.thinkgem.jeesite.common.service;

import com.thinkgem.jeesite.common.vo.ReportPageModel;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by Rick.Xu on 2016/03/24.
 */
@Service
public class ReportService {
    private static final String JQGIRD_REPORT_JSON = "reportModel";

    private static final String EXCEL_EXT = ".xlsx";

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final int SHEET_MAX_NUM = 1000000; //the sheet capacity

    private static final String NAME = "name";

    private static final String DICT_TYPE = "dict";

    private static final String CELL_TYPE = "formatter";
    private static final String CELL_TYPE_NUMBER = "number";
    private static final String CELL_TYPE_STRING = "string";

    @Resource
    private JdbcTemplateService jdbcTemplateService;

    public void report(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> param = JdbcTemplateService.getParametersAsMap(true,request);
        final ReportPageModel reportModel = getReportPageModel((String)param.get(JQGIRD_REPORT_JSON));
        report(reportModel,request,response);
    }

    private ReportPageModel getReportPageModel(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ReportPageModel model = mapper.readValue(json, ReportPageModel.class);
        return model;
    }

    private void report(final ReportPageModel model,HttpServletRequest request,HttpServletResponse response) throws Exception {
        final Workbook book = new SXSSFWorkbook();
        final Sheet sheet = book.createSheet("sheet0");
        //create head
        createHead(sheet, model.getColNames());

        jdbcTemplateService.queryForSpecificParam(model.getQueryName(), model.getPostData(),
                new JdbcTemplateService.JdbcTemplateExecutor<Void>() {
                    @Override
                    public Void query(JdbcTemplate jdbcTemplate, String queryString, Object[] args) {
                       jdbcTemplate.query(JqgridService.wrapSordString(queryString, model.getSidx(), model.getSord()), new RowCallbackHandler(){
                           private int rowIndex = 1;
                           private int sheetIndex = 1;
                           private Sheet curSheet = sheet;
                           @Override
                           public void processRow(ResultSet resultSet) throws SQLException {
                               if (rowIndex % SHEET_MAX_NUM == 0) {
                                    //create head
                                   rowIndex = 1;
                                   curSheet = book.createSheet("sheet"+sheetIndex++);
                                   createHead(curSheet, model.getColNames());
                               }

                               int i = 0;
                               Row row = curSheet.createRow(rowIndex++);
                               for (Map<String,Object> colModel : model.getColModel()) {
                                   Cell cell = row.createCell(i++);
                                   Object value = resultSet.getObject((String) colModel.get(NAME));
                                   if (colModel.get(DICT_TYPE) != null) {
                                       value = DictUtils.getDictLabel((String)value,(String)colModel.get(DICT_TYPE),"");
                                   }
                                   String cellType = colModel.get(CELL_TYPE) == null ? "string" : (String)colModel.get(CELL_TYPE);
                                   if (value != null) {
                                       if (CELL_TYPE_NUMBER.equals(cellType)) {
                                           cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                           cell.setCellValue(Double.parseDouble(String.valueOf(value)));
                                       } else if (CELL_TYPE_STRING.equals(cellType)) {
                                               cell.setCellType(Cell.CELL_TYPE_STRING);
                                               cell.setCellValue(String.valueOf(value));
                                       } else {
                                           cell.setCellValue(String.valueOf(value));
                                       }
                                   }
                               }
                           }
                       }, args);
                       return null;
                    }
                });

        //output
        OutputStream os = getOsFromResponse(response, request,model.getFileName() + EXCEL_EXT);
        book.write(os);
        os.close();
    }

    private void createHead(Sheet sheet, String[] colNames) {
        Row head = sheet.createRow(0);
        for (int i = 0; i < colNames.length; i++) {
            Cell cell = head.createCell(i);
            cell.setCellValue(colNames[i]);
        }
    }

    public static OutputStream getOsFromResponse(HttpServletResponse response,HttpServletRequest request, String fileName) throws IOException {
        OutputStream os = null;
        String _fileName = fileName.replaceAll("[\\/:*?\"<>[|]]", "");

        String browserType = request.getHeader("User-Agent").toLowerCase();

        if(browserType.indexOf("firefox") > -1) { //FF
            _fileName = "=?"+DEFAULT_ENCODING+"?B?"+(new String(Base64.encodeBase64(_fileName.getBytes(DEFAULT_ENCODING))))+"?=";
        } else {
            if(fileName.matches(".*[^\\x00-\\xff]+.*")) {
                if(request.getHeader("User-Agent").toLowerCase().indexOf("msie") > -1) { //IE
                    _fileName = java.net.URLEncoder.encode(_fileName,DEFAULT_ENCODING);
                } else  { //其他
                    _fileName = new String(_fileName.getBytes(DEFAULT_ENCODING), "ISO-8859-1");
                }
            }
        }

        response.reset();// 清空输出流
        response.setCharacterEncoding(DEFAULT_ENCODING);

        response.setHeader("Content-disposition", "attachment; filename="+_fileName+"");// 设定输出文件头
        response.setContentType("application/vnd.ms-excel;charset="+DEFAULT_ENCODING+"");// 定义输出类型
        os = response.getOutputStream(); // 取得输出流
        return os;
    }
}