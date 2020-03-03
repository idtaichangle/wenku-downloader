

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

function register(){
    $.post("/register",
        {"email":$("#email").val(),"password":$("#password").val()},
        function (result) {
            $('#loginModal').modal('hide');
            alert(result.msg);
        });
}

function login(){
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

function logout(){
    $.post("/logout",
        function (result) {
            window.location.reload();
        });
}
