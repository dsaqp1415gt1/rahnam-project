var API_BASE_URL = "http://localhost:8080/rahnam-api";
var USERNAME = Cookies.get('username');
var PASSWORD = Cookies.get('userpass');

$.ajaxSetup({
    headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});

$(document).ready(function(){
	$('#navnick').text(USERNAME);
	getCategorias();
	
	getProfile(USERNAME);
	getPhotosMain(USERNAME);
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

$("#upload_photo").click(function(e){
	e.preventDefault();
	window.location.replace("uploadphoto.html");
});

$("#button_to_edit").click(function(e){
	e.preventDefault();
	window.location.replace("edituser.html");
});



function getCategorias() {
	var URL = API_BASE_URL + '/photos/categories';
	$.ajax({
		url: URL,
		headers: {          
                 Accept : "application/vnd.rahnam.api.category.collection+json; charset=UTF-8",         
                "Content-Type": "application/vnd.rahnam.api.category.collection+json; charset=UTF-8"   
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





function getProfile(USERNAME){

	var url = API_BASE_URL + '/users/' + USERNAME;

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
					$("#email").text("E-mail: Añade tu e-mail!")}
					
					if(user.gender !=undefined){
					$("#sexo").text("Sexo: "+user.gender);}
					else{
					$("#sexo").text("Sexo: No especificado")}
			
				
								
				
	})
}

function getPhotosMain(USERNAME) {
	var URL = API_BASE_URL + '/photos/user/'+ USERNAME;
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
			
			var title = "";
			if (response.photos[k].title == undefined) {
				title = "Sin título";
			}
			else {
				title = response.photos[k].title;
			}
			
			
			
			var newimageblock = '<div class="col-md-3">'+
				'<div class="img_block">'+
					'<a href="single-photo.html?photoid='+response.photos[k].photoid+'">'+
						'<img class="img-responsive img-hover" src="'+response.photos[k].photoURL+'" alt="">'+
					'</a>'+	
					'<p class="p1"><div style="padding-left: 30px" class="col-md-9">'+title+'</div><div style="padding-right: 30px" class="col-md-3">'+
							'<a href="#"><button><span class="glyphicon glyphicon-pencil" style="vertical-align:middle"></span></button></a></div>'+
					'</p>'+
					'<hr class="h0">'+
					'<p class="p2">De: <a href="user.html?username='+response.photos[k].username+'">'+response.photos[k].username+'</p>'+
				'</div>'+
            '</div>';
			
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