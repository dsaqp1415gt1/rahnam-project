var API_BASE_URL = "http://147.83.7.158:8080/rahnam-api";

var USERNAME = "pau";
var PASSWORD = "pau";


$("#btn-login").click(function(e) {
	e.preventDefault();
	if($("#login-username").val() == "" || $("#login-password").val() == "")
	{
		if($("#login-username").val() == "")
		{
			document.getElementById('login-username').style.background='#F6B5B5';
			$('#login-username').attr('placeholder','Pon un usuario');
		}
		if($("#login-password").val() == "")
		{
			document.getElementById('login-password').style.background='#F6B5B5';
			$('#login-password').attr('placeholder','Pon una contraseña');
		}
	}
	else
	{
		var login = new Object();
		login.username = $("#login-username").val();
		login.userpass = $("#login-password").val();
		getuserpass(login);
	}
});


function getuserpass(login)
{
	console.log(login);
	var url = API_BASE_URL + '/users/login';
	var data = JSON.stringify(login);

	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		contentType : 'application/vnd.rahnam.api.user+json',
		dataType : 'json',
		data : data,
	}).done(function(data, status, jqxhr) {
		var inf = data;
				if(inf.loginSuccessful!= true){
				alert("Usuario y/o contraseña erróneo. Por favor intentelo de nuevo.");		
				}
				else{
					Cookies.set('username', $("#login-username").val());
					Cookies.set('userpass', $("#login-password").val()); 
					window.location.replace("maingallery.html");
					}

  	}).fail(function() {
		alert("Problemas de conectividad. Por favor intentelo otro usuario/contraseña.");
	});
}






$("#btn-registro").click(function(e) {
	e.preventDefault();
	if( $("#signin-username").val() == "" || $("#signin-userpass").val() == "" || $("#signin-name").val() == "" || $("#signin-email").val() == "" || $("#signin-gender").val() == "")
	{
		if($("#signin-username").val() == "")
			{
				document.getElementById('signin-username').style.background='#F6B5B5';
				$('#signin-username').attr('placeholder','RELLENE EL CAMPO');
			}
		if($("#signin-userpass").val() == "")
			{
				document.getElementById('signin-userpass').style.background='#F6B5B5';
				$('#signin-userpass').attr('placeholder','RELLENE EL CAMPO');
			}
		if($("#signin-name").val() == "")
			{
				document.getElementById('signin-name').style.background='#F6B5B5';
				$('#signin-name').attr('placeholder','RELLENE EL CAMPO');
			}
		if($("#signin-email").val() == "")
			{
				document.getElementById('signin-email').style.background='#F6B5B5';
				$('#signin-email').attr('placeholder','RELLENE EL CAMPO');
			}
		if($("#signin-gender").val() == "")
			{
				document.getElementById('signin-gender').style.background='#F6B5B5';
				$('#signin-gender').attr('placeholder','RELLENE EL CAMPO');
			}
			
			
	}else{
		var postuser = new Object();
		
		postuser.username = $("#signin-username").val();
		postuser.userpass = $("#signin-userpass").val();
		postuser.name = $("#signin-name").val();
		postuser.email = $("#signin-email").val();
		postuser.gender = $("#signin_gender").val();
		
		
		
		console.log(postuser);
		SignIn(postuser);		

			
	}
});


function SignIn (user){
	var url = API_BASE_URL + '/users';
	var data = JSON.stringify(user);
	
	console.log(data);
	
	$.ajax({
		url : url,
		type : 'POST',
		contentType: "application/vnd.rahnam.api.user+json",
		crossDomain : true,
		dataType : 'json',
		data : data,
	}).done(function(data, status, jqxhr) {
		var inf = data;
		alert("Te has registrado con usuario: "+inf.username+". Acuerdate de la contraseña eh");
		window.location.replace("index.html");		
  	}).fail(function(jqXHR, textStatus) {
		alert("el servidor explotó y se convirtió en chocapic!! Prueba otro nombre de usuario");
		console.log(textStatus);
		
	});
}


