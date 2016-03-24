<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>电影管理</title>
	<meta name="decorator" content="default"/>
    <link rel="stylesheet" href="${ctxStatic}/jqGrid/4.6/css/ui.jqgrid.css"/>
</head>
<body>
  <form:form id="searchForm" modelAttribute="film"  class="breadcrumb form-search">
        <ul class="ul-form">
            <li><label>电影名称：</label>
                <form:input path="filmName" htmlEscape="false" maxlength="64" class="input-medium"/>
            </li>
            <li><label>电影类型：</label>
                <form:select path="filmType" class="input-medium">
                    <form:option value="" label=""/>
                    <form:options items="${fns:getDictList('film_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                </form:select>
            </li>
            <li class="btns"><input name="query" class="btn btn-primary" type="button" value="查询"/></li>
            <li class="btns"><input name="export" class="btn btn-primary" type="button" value="导出"/></li>
            <li class="btns"><input name="export2" onclick="report();" class="btn btn-primary" type="button" value="导出2"/></li>
            <li class="clearfix"></li>
        </ul>
    </form:form>

  <table id="list"></table>
  <div id="pager"></div>
  <hr/>
  <table id="list2"></table>
  <div id="pager2"></div>
  <script src="${ctxStatic}/jqGrid/4.6/i18n/grid.locale-cn.js"></script>
  <script src="${ctxStatic}/jqGrid/4.6/js/jquery.jqGrid.min.js"></script>
  <script src="${ctxStatic}/common/jquery.jqgrid.form.js"></script>
  <script src="${ctxStatic}/common/jquery.report.js"></script>
  <script>
      $('#list').jqGridForm({
          queryName:'com.thinkgem.jeesite.modules.film.dao.FilmDao.findList3',
          url:"${ctx}/common/list",
          fileName:'export',
          formId:"searchForm",
          pager:"pager2",
          colNames:['电影类型','电影名称'],
          colModel:[
              {name:'filmType',index:'filmType', sortable:true, frozen : true,dict:'film_type'},
              {name:'filmName',index:'filmName', frozen : true}
          ]
      });

   /* $('#list2').jqGridForm({
        queryName:'com.thinkgem.jeesite.modules.film.dao.FilmDao.count',
        //url:"${ctx}/common/list2", //default list
        formId:"searchForm",
        colNames:['电影类型','数量'],
        colModel:[
            {name:'filmType',index:'filmType', sortable:true, frozen : true},
            {name:'count',index:'count', frozen : true}
            ]
         });*/

    //手动report
    function report() {
        $.fn.report({
            url:"${ctx}/common/report",
            queryName:"com.thinkgem.jeesite.modules.film.dao.FilmDao.findList2",
            fileName:"test",
            sidx:'filmType',
            sord:'desc',
            postData : {
                //"filmName" : "",
                //"filmType"   : "0"
            },
            colNames:['电影类型','电影名称'],
            colModel:[
                {name:'filmType',formatter:'string',dict:'film_type'},
                {name:'filmName',formatter:'string'},
                {name:'score',formatter:'string'}
            ]
        });
    }
  </script>
</body>
</html>