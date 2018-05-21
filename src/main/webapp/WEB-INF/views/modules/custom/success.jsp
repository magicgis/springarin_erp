<%@page import="org.springframework.web.multipart.MultipartException"%>
<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ page import="com.springrain.erp.common.beanvalidator.BeanValidators"%>
<%@ page import="org.slf4j.Logger,org.slf4j.LoggerFactory" %>
<html>
<head></head>
 <style>
 html,body,h1,h2,h3,h4,h5,h6,div,dl,dt,dd,ul,ol,li,p,blockquote,pre,hr,figure,table,caption,th,td,form,fieldset,legend,input,button,textarea,menu{margin:0;padding:0;}
header,footer,section,article,aside,nav,hgroup,address,figure,figcaption,menu,details{display:block;}
table{border-collapse:collapse;border-spacing:0;}
caption,th{text-align:left;font-weight:normal;}
html,body,fieldset,img,iframe,abbr{border:0;}
i,cite,em,var,address,dfn{font-style:normal;}
[hidefocus],summary{outline:0;}
li{list-style:none;}
h1,h2,h3,h4,h5,h6,small{font-size:100%;}
sup,sub{font-size:83%;}
pre,code,kbd,samp{font-family:inherit;}
q:before,q:after{content:none;}
textarea{overflow:auto;resize:none;}
label,summary{cursor:default;}
a,button{cursor:pointer;}
h1,h2,h3,h4,h5,h6,em,strong,b{font-weight:bold;}
del,ins,u,s,a,a:hover{text-decoration:none;}
body,textarea,input,button,select,keygen,legend{font:16px/1.14 arial,\5b8b\4f53;color:#333;outline:0;}
body{background:#fff;}
a,a:hover{color:#333;}
img{max-width: 100%;} 

/*清浮动*/
.clearfix:after{content:"";display:block;clear:both;}
.clearfix{zoom:1;}
/* Hides from IE-mac \*/
*html .clearfix {height: 1%;}
.clearfix {display: block;}
/* End hide from IE-mac */ 
.clear{clear:both;}
.fl{float:left;}
.fr{float:right;}

section{
	width: 100%;
}
section .mail{
	height: 693px;
	position: relative;
	overflow: hidden;
}
section .mail .mail_img{
	width: 2000px;
	position: absolute;
	left: 50%;
	margin-left: -1000px;
	overflow: hidden;
	height: 693px;
	text-align: center;
}
.mail_box{
	background: url("../static/images/bg_btn.png") no-repeat center #3a5370;
	position: absolute;
	bottom: 152px;
	left: 20.6%;
	width: 219px;
	height: 54px;
}
.mail_box:hover{
	background-color: #fa566e;
}

 
 </style>

 <body> 
  <section>
    <div class="mail">
      <div class="mail_img">
          <img src="../static/images/mail_img.jpg" alt=""/>
          <a href="http://www.inateck.com" target="_self"><div class="mail_box"></div></a>
      </div>
    </div>
  </section>
</body>
</html>