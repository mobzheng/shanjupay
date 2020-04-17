<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#if stus??>
    <#list stus as stu>
        <tr>
            <td>${stu_index +1}</td>
            <td <#if stu.name?? && stu.name == '小明'>style="background:red;"</#if>>${stu.name!""}</td>
            <td>${stu.age}</td>
            <td>${stu.money}</td>
        </tr>
    </#list>
    </#if>
</table>
输出stu1的学生信息：<br/>
姓名：${(stuMap["stu1"].name)!""}
年龄：${stuMap['stu1'].age}<br/>
输出stu1的学生信息：<br/>
姓名：${(stuMap.stu1.name)!""}<br/>
年龄：${stuMap.stu1.age}<br/>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#list stuMap?keys as key>
        <tr>
            <td>${key_index +1}</td>
            <td>${(stuMap[key].name)!""}</td>
            <td>${stuMap[key].age}</td>
            <td>${stuMap[key].money}</td>
        </tr>
    </#list>

</table>
url请求参数值：${RequestParameters['param1']!""}<br/>
Request属性值 : ${Request['attr1']}<br/>
Session属性值 : ${Session['session1']}<br/>
contextPath: ${rc.contextPath}<br/>
请求地址: ${rc.requestUri}
</body>
</html>