//pop up a form
function popBox(){
    var popBox = document.getElementById('popBox');
    var popLayer = document.getElementById('popLayer');

    popLayer.style.width = document.body.scrollWidth + "px";
    popLayer.style.height = document.body.scrollHeight + "px";

    popLayer.style.display = "block";
    popBox.style.display = "block";

    document.getElementById("1").style.display="block";

    document.getElementById("register").style.display="none";
    document.getElementById("to-login").style.display="none";
    document.getElementById("telephone").style.display="none";
    document.getElementById("login").style.display="block";
    document.getElementById("to-register").style.display="block";
}

//close the form
function closeBox(){
    var popBox = document.getElementById('popBox');
    var popLayer = document.getElementById('popLayer');

    popLayer.style.display = "none";
    popBox.style.display = "none";

}


//order confirmation
function confirmOrder(point){
    let number = document.getElementById('number-input').value;
    if(number>0&&number%1===0){
    var confirmation=confirm("是否确定")//按确认和取消输出不同的内容
    if(confirmation===true){
        let userInfoStr = localStorage.getItem('userInfo')
        let resJson = JSON.parse(userInfoStr);// 获取用户ID
        let userId = resJson.id;
        let bookId = point;
        // let number = document.getElementById('number-input').value;

        //创建核心对象
        xmlhttp = null;
        if (window.XMLHttpRequest) {// code for Firefox, Opera, IE7, etc.
            xmlhttp = new XMLHttpRequest();
        } else if (window.ActiveXObject) {// code for IE6, IE5
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        //编写回调函数
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                let resTxt = xmlhttp.responseText
                // 如果返回的值不为空，表示从后台拿去到数据了
                if (resTxt) {
                    // 将查询到的数据转为JSON
                    let resJson = JSON.parse(resTxt)
                    alert('成功')
                }
            }
        }
        var doUrl="http://47.101.56.99:8080/demoproject//v1/order-management/orders"
        xmlhttp.open("post", doUrl, false);//一个servlet，后面还可以写是否同步
		//设置请求头
		xmlhttp.setRequestHeader("content-type", "application/x-www-form-urlencoded")
        //send 发送
        xmlhttp.send("userid="+userId+"&"+"bookid="+bookId+"&"+"number="+number);
        window.location ="order_index.html"
    }
    }
    else{
        alert('请输入大于0的整数')
    }
}

