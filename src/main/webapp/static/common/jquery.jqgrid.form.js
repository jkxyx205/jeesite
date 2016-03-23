/**
 * Created by Rick.Xu on 2016/03/22.
 */
(function($) {
    var Grid = function(element, options) {
        this.$element = $(element);
        this.options = $.extend({},
            $.fn.jqGridForm.defaults, options);
        this.init();
    };

    Grid.prototype = {
        constructor: Grid,
        init: function() {
            var o = this;
            this.options.serializeGridData = function(postData) {
                postData.queryName =o.options.queryName;
                return postData;
            };

            this.grid = this.$element.jqGrid(this.options);
            $(window).resize(function(){
                o.grid.jqGrid('setGridWidth',$(window).width()-100);
                o.grid.jqGrid('setGridHeight',$(window).height()/2-100);
            }).resize();
            var $form = $('#'+ this.options.formId);
            $form.find('input[name=query]').bind('click', function() {
                query($form, o.grid);
            });
            $form.find('input[name=export]').bind('click', function() {
                //exportGrid(o);
            });
        },
        getGrid: function() {
            return this.grid;
        }
    };

    function getFormParams($form) {
        return param = $form.form2json({allowEmptyMultiVal:true});
    }
    function query($form, $grid) {
        //$grid.jqGrid("setGridParam", {postData:null});
        $grid.jqGrid("setGridParam", {postData:getFormParams($form)}).trigger("reloadGrid", [{page:1}]);
    }

    function exportGrid(o) {
        alert(o);
    }

    $.fn.jqGridForm = function(options) {
        var args = arguments;
        var value;
        var chain = this.each(function() {
            data = $(this).data("jqGridForm");
            if (!data) {
                if (options && typeof options == 'object') { //初始化
                    return $(this).data("jqGridForm", data = new Grid(this, options));
                }
            } else {
                if (typeof options == 'string') {
                    if (data[options] instanceof Function) { //调用方法
                        var property = options; [].shift.apply(args);
                        value = data[property].apply(data, args);
                    } else { //获取属性
                        return value = data.options[options];
                    }
                }
            }

        });

        if (value !== undefined) {
            return value;
        } else {
            return chain;
        }
    };

    $.fn.jqGridForm.defaults = {
        url:ctx + '/common/jqgridList2',
        datatype: "json",
        mtype: 'POST',
        //rowList:[10,20,30],
        pager: '#pager',
        //sortname: 'update_date',
        //sortorder: "desc",
        viewrecords: true,
        rownumbers: true,	// 显示序号
        shrinkToFit: false, // 不按百分比自适应列宽
        /*ajaxGridOptions : {
            contentType : 'application/json; charset=utf-8'
        },
        ajaxRowOptions : {
            contentType : "application/json",
            dataType : "json"
        },*/
        height:'auto',
        multiselect: true // 显示多选复选框
        //multiboxonly:true, // 单击复选框时在多选
        //footerrow: true, // 显示底部合计行,
    };
})(jQuery);