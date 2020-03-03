

function showLogin() {
    $('#loginModal').modal();
}

function showRegister() {
    $('#loginModal').modal();
}

function login(){
    $.post("/login",
        {"email":$("#email").val(),"password":$("#password").val()},
        function (result) {
            if(result.success){
                window.location.reload();
            }
        });
}

function logout(){
    $.post("/logout",
        function (result) {
            window.location.reload();
        });
}
