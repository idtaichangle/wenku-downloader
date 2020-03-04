function showLogin() {
    $("#dialogType").val(1);
    $("#modalLabel").text("登录");
    $("button[type='submit']").text("登录");
    $('#loginModal').modal();
}

function showRegister() {
    $("#dialogType").val(2);
    $("#modalLabel").text("注册");
    $("button[type='submit']").text("注册");
    $('#loginModal').modal();
}

function doRegister(){
    $.post("/register",
        {"email":$("#email").val(),"password":$("#password").val()},
        function (result) {
            $('#loginModal').modal('hide');
            alert(result.msg);
        });
}

function loLogin(){
    $.post("/login",
        {"email":$("#email").val(),"password":$("#password").val()},
        function (result) {
            if(result.success){
                window.location.reload();
            }else{
                alert(result.msg);
            }
        });
}

function doLogout(){
    $.post("/logout",
        function (result) {
            window.location.reload();
        });
}

function showPay(payType) {
    if(payType==1){
        $("#recommand").attr("src","img/weixin.png")
        $("#qr_bg").attr("class","qr_bg_wx");
    }else if(payType==2){
        $("#recommand").attr("src","img/alipay.png")
        $("#qr_bg").attr("class","qr_bg_alipay");
    }
    $("#timeout").text("5:00");
    clearInterval(timer);
    $.post("createPayOrder",{"taskId":id,"payType":payType},function (result) {
        if(result.success){
            $("#qr").attr("src",result.data.qr_img);
            $('#payModal').modal();
            startCountDown();
            $('#payModal').on('hidden.bs.modal', function (e) {
                clearInterval(timer);
            });
        }
    })
}

function queryTask() {
    $.post("queryTaskFile",{"taskId":id},function (result) {
        if(result.success){
            $(".msg").hide();
            $("#preview").show();
            $("#preview_img").attr("src","/previewImg?taskId="+id);
            $(".doc-name").text(result.data.name);
            $("#ready").show();
            if(!login){
                $("#login_required").show();
            }else{
                $("#pay_instruction").show();
            }
        }
    });
}

function startWebsocket() {

    var ws;
    if(location.protocol.indexOf("https:")==0){
        ws=new WebSocket('wss://' + location.host + '/downloader-ws'+'?id='+id);
    }else{
        ws=new WebSocket('ws://' + location.host + '/downloader-ws'+'?id='+id);
    }

    ws.onmessage = function (result) {
        var json = JSON.parse(result.data);
        $(".msg").hide();
        if(json.type=="FETCH_META_RESULT"){
            if(json.success){
                $(".doc-name").text(json.meta.name);
                $("#loading").show();
                $("#meta").show()
            }
        }else if(json.type=="FETCH_DOCUMENT_RESULT"){
            if(json.success){
                $("#ready").show();
                $("#preview").show();
                $("#preview_img").attr("src","/previewImg?taskId="+id);
                if(!login){
                    $("#login_required").show();
                }else{
                    $("#pay_instruction").show();
                }
            }else{
                $("#fail").show();
            }
        }else if(json.type=="PAY_RESULT"){
            if(json.success){
                $('#payModal').modal('hide');
                $("#preview").show();
                $("#ready").show();
                $("#download").show();
                $("#d_link").attr("href","download?fileName="+json.link);
            }
        }
    }
}

var timer;
function startCountDown() {
    timer=setInterval(function () {
        var t= parseInt($("#timeout").text().split(':')[0])*60+parseInt($("#timeout").text().split(':')[1]);
        t=t-1;
        $("#timeout").text(Math.floor(t/60)+":"+(t%60));
        if(t<=0){
            clearInterval(timer);
            $('#payModal').modal('hide');
        }
    },1000);
}
