var hosturl="http://localhost:8080/demoproject/";

//获取登录状态
function getUserInfo () {
    let userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
        let resJson = JSON.parse(userInfoStr);
        // 关闭对话框
        closeBox()
        // 设置头像的地址
        document.getElementById('myAvatar').src= resJson.avatar
        // 显示头像
        document.getElementById('myAvatar').style= 'display:inline-block'
        // 设置用户名的地址
        document.getElementById('username').innerText= resJson.name
        // 显示用户名
        document.getElementById('username').style= 'display:inline-block'
        // 隐藏按钮
        document.getElementById('loginModalBtn').style= 'display:none'
        // 存储用户信息
        localStorage.setItem('userInfo', resTxt)
    } else {

    }
}

window.onload = function () {
    getUserInfo()
}


//登录
function loginAction () {
    let userPhone = document.getElementById('userPhone').value;
    let userPwd = document.getElementById('userPwd').value;
    if (!userPhone) {
        alert('请输入用户id')
        return false
    }
    if (!userPwd) {
        alert('请输入密码')
        return false
    }
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
            let resJson = JSON.parse(resTxt)
            closeBox()
            // 设置头像的地址
            document.getElementById('myAvatar').src= resJson.avatar
            // 显示头像
            document.getElementById('myAvatar').style= 'display:inline-block'
            // 设置用户名的地址
            document.getElementById('username').src= resJson.name
            // 显示用户名
            document.getElementById('username').style= 'display:inline-block'
            // 隐藏按钮
            document.getElementById('loginModalBtn').style= 'display:none'
            // 存储用户信息
            localStorage.setItem('userInfo', resTxt)
        }
    }
    //open设置请求方式和请求路径
    xmlhttp.open("get", "http://localhost:8080/demoproject/login?userId="+userPhone+"&pwd="+userPwd);//一个servlet，后面还可以写是否同步
    //send 发送
    xmlhttp.send();
}

//注册
function registerAction () {
    let name = document.getElementById('name').value;
    let password = document.getElementById('userPwd').value;
    let phone = document.getElementById('phone').value;
    if (!name) {
        alert('请输入用户名')
        return false
    }
    if (!password) {
        alert('请输入密码')
        return false
    }
    if (!phone) {
        alert('请输入手机号')
        return false
    }
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
                closeBox()
                alert('注册成功！您可以登录')
            }
        }
        else{
            // 如果已存在对应的手机号，接口返回值500
            alert('您输入的手机号已存在，请重新输入')
        }
    }
    //open设置请求方式和请求路径
    var post="name="+name+"&phone="+phone+"&password="+password;
    var doUrl="http://47.101.56.99:8080/demoproject//v1/user-management/users?"+post
    xmlhttp.open("post", doUrl, false);//一个servlet，后面还可以写是否同步
    //send 发送
    xmlhttp.send(null);
}

var onClick=1;//判断开关，防止二次触发

function upload() {
    var input = document.querySelector('#search-input');
    
    if (input.files.length === 0) {
        console.log("未选择文件");
        return;
    }
 
    var formData = new FormData();
    formData.append("file", input.files[0]);
    //创建核心对象
    xmlhttp = null;
    if (window.XMLHttpRequest) {// code for Firefox, Opera, IE7, etc.
        xmlhttp = new XMLHttpRequest();
    } else if (window.ActiveXObject) {// code for IE6, IE5
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    //编写回调函数
    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4 && (xmlhttp.status == 200 || xmlhttp.status == 201)) {
           //上传成功
            alert("success!");
			let resTxt = xmlhttp.responseText
			// 如果返回的值不为空，表示从后台拿去到数据了
			if (resTxt) {
				// 将查询到的数据转为JSON
				let resJson = JSON.parse(resTxt)
				console.log("JOBNAME:"+resJson.jobname);
				console.log("JOBID"+resJson.jobid);
				console.log("OWNER"+resJson.owner);
			}
        }
    }
    //open设置请求方式和请求路径
    xmlhttp.open("POST", "http://localhost:8080/demoproject/uploadA");
    //send 发送
    xmlhttp.send(formData);
}


//显示书籍具体信息
function toBookDetail(point){
    window.location ="bookDetail_index.html?id="+point
    console.log(1);
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
            var text = xmlhttp.responseText; //使用接口返回内容，响应内容
            var resultJson = eval("(" + text + ")"); //把响应内容对象转成javascript对象
            console.log(resultJson);
            for (var i = 0; i < resultJson.length; i++) {
                var str="<li><dl><dd>";
                str+="<a onclick='toBookDetail("+resultJson.id+")'> <img src=" + (hosturl+resultJson.cover) + "></a></dd>";
                str+="<dt>"
                str+="<p class=\"book_title\"><a>"+ resultJson.title +"</a></p>";
                str+="<p class=\"book_inline\">"+ resultJson.author +"</p>";
                str+="<p class=\"book_inline\">"+ resultJson.publisher +"</p>";
                str+="<p class=\"book_inline\">"+ resultJson.introduction +"</p>";
                str+="<p class=\"book_inline\">$ "+ resultJson.price +"</p>";
                str+="<p class=\"book_inline\">数量：<input id='number-input' value='0' size='1'></p><br/>";
                str+="<button class=\"book_buy\" onclick='addCart("+resultJson.id+")'>ADD CART</button><br/><br/>";
                str+="<button class=\"book_buy\" onclick='confirmOrder("+resultJson.id+")'>BUY &nbspNOW</button>";
                str+="</dt></dl></li>";
                var dot=document.createElement("SHOWBOOKDETAIL");
                dot.innerHTML=str;
                document.getElementById("book-detail").appendChild(dot);
            }
        }
    }
    //open设置请求方式和请求路径
    xmlhttp.open("get", "http://47.101.56.99:8080/demoproject//v1/book-management/books?id=" + point);//一个servlet，后面还可以写是否同步
    //send 发送
    xmlhttp.send();

}

//加购物车
function addCart (point) {
    let userInfoStr = localStorage.getItem('userInfo')
    let resJson = JSON.parse(userInfoStr);// 获取用户ID
    let userId = resJson.id;
    let bookId = point;
    let number = document.getElementById('number-input').value;
    if(number>0&&number%1===0){
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
        //open设置请求方式和请求路径
        var doUrl="http://47.101.56.99:8080/demoproject//v1/cart-management/carts"
        xmlhttp.open("post", doUrl, false);//一个servlet，后面还可以写是否同步
        //设置请求头
        xmlhttp.setRequestHeader("content-type", "application/x-www-form-urlencoded")
        //send 发送
        xmlhttp.send("userid="+userId+"&"+"bookid="+bookId+"&"+"number="+number);
    }
    else{
        alert('请输入大于0的整数')
    }
}


