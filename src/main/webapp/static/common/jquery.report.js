(function($){
    $.fn.report = function(options) {
        this.options = $.extend({},
            $.fn.report.defaults, options);

        var param = {};
        param.reportModel = JSON.stringify(this.options);
        request(this.options.url,param);
    }

    $.fn.report.defaults = {
        url:ctx + '/common/list'
    };

    function request(url,data) {
        var form = jQuery('<form  action="' + url + '" method="POST"></form>');
        if (data) {
            for (var i in data) {
                var $hidden = $('<input type="hidden" name="' + i + '" />');
                if (Array.isArray(data[i]))
                    $hidden.val(data[i].join(";"));
                else
                    $hidden.val(data[i]);
                $hidden.appendTo(form);
            }
        }
        $(form).appendTo('body');
        form.submit();
    }
})(jQuery);