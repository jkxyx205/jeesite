<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>电影管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/film/film/count">电影列表</a></li>
		<shiro:hasPermission name="film:film:edit"><li><a href="${ctx}/film/film/form">电影添加</a></li></shiro:hasPermission>
	</ul>
    <form:form id="searchForm" modelAttribute="film" action="${ctx}/film/film/" method="post" class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <ul class="ul-form">
            <li><label>电影类型：</label>
                <form:select path="filmType" class="input-medium">
                    <form:option value="" label=""/>
                    <form:options items="${fns:getDictList('film_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                </form:select>
            </li>
            <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
            <li class="clearfix"></li>
        </ul>
    </form:form>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>电影类型</th>
				<th>数量</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="film">
			<tr>
				<td>
					${fns:getDictLabel(film.filmType, 'film_type', '')}
				</td>
				<td>
					${film.count}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>