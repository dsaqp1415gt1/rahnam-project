var API_BASE_URL = "http://localhost:8080/rahnam-api";
var USERNAME = Cookies.get('username');
var PASSWORD = Cookies.get('userpass');


$.ajaxSetup({
    headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});

$(document).ready(function(){
	$('#navnick').text(USERNAME);
	getCategorias();
		
	username2 = getUrlParameter('username');
	
	getProfile(username2);
	getPhotosMain(username2);
	
});


$("#logout").click(function(e) {
	e.preventDefault();
	Cookies.remove('username');
	Cookies.remove('userpass');
	window.location.replace("index.html");
});

$("#button_searchtitle").click(function(e) {
	e.preventDefault();
	var titulo = $('#searchtitle').val();
	var newurl = 'search.html?title=' + titulo;
	window.location.replace(newurl);
});

function getUrlParameter(sParam){
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            return sParameterName[1];
        }
    }
} 


function getCategorias() {
	var URL = API_BASE_URL + '/photos/categories';
	$.ajax({
		url: URL,
		headers: {          
                 Accept : "application/vnd.rahnam.api.category.collection+json; charset=utf-8",         
                "Content-Type": "application/vnd.rahnam.api.category.collection+json; charset=utf-8"   
		},
		type: 'GET',
		crossDomain : true
	})
	.done(function (data, status, jqxhr) {
		var cats = data.categories
		$.each(cats, function(c,v){
			$('#menucats').append('<li><a href="search.html?category='+cats[c].name+'">'+cats[c].name+'</a></li>');
		});
	})
	.fail(function (jqXHR, textStatus) {
    	alert("KO");
		console.log(textStatus);
	});
}


function getProfile(username2){

	var url = API_BASE_URL + '/users/' + username2;

	console.log(url);

	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		
	}).done(function(data, status, jqxhr) {

				var user = data;	
				
						
				$("#username2").text(""+user.username);		
				
					if(user.name != undefined)	{	
					$("#name").text("Nombre: "+user.name);}
				
					if(user.email != undefined)	{	
					$("#email").text("E-mail: "+user.email);}
					else{
					$("#email").text("E-mail: No ha especificado ningún e-mail")}
					
					if(user.gender !=undefined){
					$("#sexo").text("Sexo: "+user.gender);}
					else{
					$("#sexo").text("Sexo: No especificado")}
			
				
								
				
	})
}

function getPhotosMain(username2) {
	var URL = API_BASE_URL + '/photos/user/'+ username2;
	var colcont = 1;
	$.ajax({
		url: URL,
		headers: {          
                 Accept : "application/vnd.rahnam.api.photo.collection+json; charset=UTF-8",         
                "Content-Type": "application/vnd.rahnam.api.photo.collection+json; charset=UTF-8"   
		},
		type: 'GET',
		crossDomain : true
	})
	.done(function (data, status, jqxhr) {
		var response = $.parseJSON(jqxhr.responseText);
		$('.carousel-inner').empty();
		$.each(response.photos, function(k,v){
		
			var newimageblock = '<div class="col-md-3"><div class="img_block"><a href="single-photo.html?photoid='+response.photos[k].photoid+'">'+
				'<img  class="img-responsive img-hover" src="'+response.photos[k].photoURL+'" alt=""></a><div class="row">'+
				'<p class="p1">'+response.photos[k].title+''+
				'<hr class="h0"><p class="p2">De: <a href="#">'+response.photos[k].username+'</a></p></div></div>';
			
			if (colcont <= 4){
				$('#row1').append(newimageblock) }
			else if (colcont <= 8){
				$('#row2').append(newimageblock) }
			else if (colcont <= 12){
				$('#row3').append(newimageblock) }
			colcont ++;
			console.log(response.photos[k].photoid);

		});
	})
    .fail(function (jqXHR, textStatus) {
    	alert("KO");
		console.log(textStatus);
	});
};