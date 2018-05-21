/*!
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

// 引入js和css文件
function include(id, path, file){
	if (document.getElementById(id)==null){
        var files = typeof file == "string" ? [file] : file;
        for (var i = 0; i < files.length; i++){
            var name = files[i].replace(/^\s|\s$/g, "");
            var att = name.split('.');
            var ext = att[att.length - 1].toLowerCase();
            var isCSS = ext == "css";
            var tag = isCSS ? "link" : "script";
            var attr = isCSS ? " type='text/css' rel='stylesheet' " : " type='text/javascript' ";
            var link = (isCSS ? "href" : "src") + "='" + path + name + "'";
            document.write("<" + tag + (i==0?" id="+id:"") + attr + link + "></" + tag + ">");
        }
	}
}

// 打开一个窗体
function windowOpen(url, name, width, height){
	var top=parseInt((window.screen.height-height)/2,10),left=parseInt((window.screen.width-width)/2,10),
		options="location=no,menubar=no,toolbar=no,dependent=yes,minimizable=no,modal=yes,alwaysRaised=yes,"+
		"resizable=yes,scrollbars=yes,"+"width="+width+",height="+height+",top="+top+",left="+left;
	window.open(url ,name , options);
}

// 显示加载框
function loading(mess){
	top.$.jBox.tip.mess = null;
	top.$.jBox.tip(mess,'loading',{opacity:0});
}

// 确认对话框
function confirmx(mess, href){
	top.$.jBox.confirm(mess,'系统提示',function(v,h,f){
		if(v=='ok'){
			loading('正在提交，请稍等...');
			location = href;
		}
	},{buttonsFocus:1});
	top.$('.jbox-body .jbox-icon').css('top','55px');
	return false;
}

$(document).ready(function() {
	//所有下拉框使用select2
	$("select[class!='no2']").select2();
	$('.fancybox').fancybox();
	$("th input:checkbox").click(function() {
		var checkedStatus = this.checked;
		var checkbox = $(this).parents('#contentTable').find('tr td:first-child input:checkbox');		
		checkbox.each(function() {
			this.checked = checkedStatus;
			if (checkedStatus == this.checked) {
				$(this).closest('.checker > span').removeClass('checked');
			}
			if (this.checked) {
				$(this).closest('.checker > span').addClass('checked');
			}
		});
	});	
	$("td input:checkbox").click(function() {
		if (this.checked) {
			$(this).closest('.checker > span').addClass('checked');
		}else{
			$(this).closest('.checker > span').removeClass('checked');
		}
	});
	
	$(".nav").find("a").addClass("nava");
	
	$("form").on("keyup",".number",function(){
		this.value=this.value.replace(/\D/g,'');
	}).on("afterpaste",".number",function(){
		this.value=this.value.replace(/\D/g,'');
	}).on("blur",".number",function(){
		this.value=this.value.replace(/\D/g,'');
	});;
	
	$("form").on("keyup",".english",function(){
		this.value=this.value.replace(/[\u4e00-\u9fa5]/g,'');
	}).on("afterpaste",".english",function(){
		this.value=this.value.replace(/[\u4e00-\u9fa5]/g,'');
	}).on("blur",".english",function(){
		this.value=this.value.replace(/[\u4e00-\u9fa5]/g,'');
	});
	
	$("form").on("keyup",".price",function(){
		keyUp(this);
	}).on("keypress",".price",function(){
		keyPress(this);
	}).on("blur",".price",function(){
		onBlur(this);
	});
	
});

function keyPress(ob) {
	 if (!ob.value.match(/^[\+\-]?\d*?\.?\d*?$/)) ob.value = ob.t_value; else ob.t_value = ob.value; if (ob.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/)) ob.o_value = ob.value;
}
function keyUp(ob) {
	if (!ob.value.match(/^[\+\-]?\d*?\.?\d*?$/)) ob.value = ob.t_value; else ob.t_value = ob.value; if (ob.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/)) ob.o_value = ob.value;
}
function onBlur(ob) {
	if(!ob.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/))ob.value=ob.o_value;else{if(ob.value.match(/^\.\d+$/))ob.value=0+ob.value;if(ob.value.match(/^\.$/))ob.value=0;ob.o_value=ob.value};
}



function checkImgType(this_) {

	var filepath = $(this_).val();
	var extStart = filepath.lastIndexOf(".");

	var ext = filepath.substring(extStart, filepath.length).toUpperCase();

	if (ext != ".PNG" && ext != ".GIF" && ext != ".JPG" && ext != ".JPEG") {

		alert("图片限于png,gif,jpg,jpeg格式");
		$(this_).focus();
		if ($.browser.msie) { //判断浏览器
			this_.select();
			document.execCommand("delete");
		} else {
			$(this_).val("");
		}
		return false;
	}

	var file_size = 0;
	if ($.browser.msie) {
		var img = new Image();
		img.src = filepath;

		if (img.fileSize > 0) {

			if (img.fileSize > 3 * 1024) {

				alert("图片不大于3MB。");
				$(this_).focus();

				this_.select();
				document.execCommand("delete");
				return false;
			}

		}

	} else {

		file_size = this_.files[0].size;

		console.log(file_size / 1024 / 1024 + " MB");

		var size = file_size / 1024/1024;
		if (size > 3) {
			alert("上传的文件大小不能超过3M！");
			$(this_).focus();
			$(this_).val("");
			return false;
		}
	}
	return true;
}

js = {lang:{}};
js.lang.String = function(){

    this.REGX_HTML_ENCODE = /"|&|'|<|>|[\x00-\x20]|[\x7F-\xFF]|[\u0100-\u2700]/g;

    this.REGX_HTML_DECODE = /&\w+;|&#(\d+);/g;

    this.REGX_TRIM = /(^\s*)|(\s*$)/g;

    this.HTML_DECODE = {
        "&lt;" : "<", 
        "&gt;" : ">", 
        "&amp;" : "&", 
        "&nbsp;": " ", 
        "&quot;": "\"", 
        "&copy;": ""

        // Add more
    };

    this.encodeHtml = function(s){
        s = (s != undefined) ? s : this.toString();
        return (typeof s != "string") ? s :
            s.replace(this.REGX_HTML_ENCODE, 
                      function($0){
                          var c = $0.charCodeAt(0), r = ["&#"];
                          c = (c == 0x20) ? 0xA0 : c;
                          r.push(c); r.push(";");
                          return r.join("");
                      });
    };

    this.decodeHtml = function(s){
        var HTML_DECODE = this.HTML_DECODE;

        s = (s != undefined) ? s : this.toString();
        return (typeof s != "string") ? s :
            s.replace(this.REGX_HTML_DECODE,
                      function($0, $1){
                          var c = HTML_DECODE[$0];
                          if(c == undefined){
                              // Maybe is Entity Number
                              if(!isNaN($1)){
                                  c = String.fromCharCode(($1 == 160) ? 32:$1);
                              }else{
                                  c = $0;
                              }
                          }
                          return c;
                      });
    };

    this.trim = function(s){
        s = (s != undefined) ? s : this.toString();
        return (typeof s != "string") ? s :
            s.replace(this.REGX_TRIM, "");
    };


    this.hashCode = function(){
        var hash = this.__hash__, _char;
        if(hash == undefined || hash == 0){
            hash = 0;
            for (var i = 0, len=this.length; i < len; i++) {
                _char = this.charCodeAt(i);
                hash = 31*hash + _char;
                hash = hash & hash; // Convert to 32bit integer
            }
            hash = hash & 0x7fffffff;
        }
        this.__hash__ = hash;

        return this.__hash__; 
    };

};

var tabTableInput = function(tableId, inputType) {
	$("#" + tableId).on("keydown","tr input",function(evt) {
		var rowInputs = [];
		var inputRowIndex = 0;
		var trs = $("#" + tableId).find("tr");
		$.each(trs, function(i, obj) {
			if ($(obj).find("th").length > 0) { //跳过表头                  
				return true;
			}
			var rowArray = [];
			var thisRowInputs;
			if (!inputType) { //所有的input                    
				thisRowInputs = $(obj).find(
						"input:not(:disabled):not(:hidden):not([readonly])");
			} else {
				thisRowInputs = $(obj).find(
						"input:not(:disabled):not(:hidden):not([readonly])[type="
								+ inputType + "]");
			}
			if (thisRowInputs.length == 0)
				return true;
			thisRowInputs.each(function(j) {
				$(this).attr("_r_", inputRowIndex).attr("_c_", j);
				rowArray.push({
					"c" : j,
					"input" : this
				});
			});
			rowInputs.push({
				"length" : thisRowInputs.length,
				"rowindex" : inputRowIndex,
				"data" : rowArray
			});
			inputRowIndex++;
		});
		
		
	
		var r = $(this).attr("_r_");
		var c = $(this).attr("_c_");
		var tRow;
		if (evt.which == 38) { //上                            
			if (r == 0)
				return;
			r--; //向上一行                             
			tRow = rowInputs[r];
			if (c > tRow.length - 1) {
				c = tRow.length - 1;
			}
		} else if (evt.which == 40) { //下                             
			if (r == rowInputs.length - 1) { //已经是最后一行                                 
				return;
			}
			r++;
			tRow = rowInputs[r];
			if (c > tRow.length - 1) {
				c = tRow.length - 1;
			}
		} else if (evt.which == 37) { //左                             
			if (r == 0 && c == 0) { //第一行第一个,则不执行操作                                 
				return;
			}
			if (c == 0) { //某行的第一个,则要跳到上一行的最后一个,此处保证了r大于0                                 
				r--;
				tRow = rowInputs[r];
				c = tRow.length - 1;
			} else { //否则只需向左走一个                                
				c--;
			}
		} else if (evt.which == 39) { //右                             
			tRow = rowInputs[r];
			if (r == rowInputs.length - 1 && c == tRow.length - 1) { //最后一个不执行操作                                 
				return;
			}
			if (c == tRow.length - 1) { //当前行的最后一个,跳入下一行的第一个                                 
				r++;
				c = 0;
			} else {
				c++;
			}
		}
		$(rowInputs[r].data[c].input).focus();
	});
};	

js.lang.String.call(String.prototype);

