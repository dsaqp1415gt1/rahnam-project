var API_BASE_URL = "http://localhost:8080/rahnam-api";
var USERNAME = Cookies.get('username');
var PASSWORD = Cookies.get('userpass');
USERNAME = "pau";
PASSWORD = "pau";
var idphoto = "";

$.ajaxSetup({
    headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});

$(document).ready(function(){
	$("#username" ).val(USERNAME);
	$('#navnick').text(USERNAME);
	idphoto = getUrlParameter('photoid');
	console.log(idphoto);
	
	getCategorias();
	getPhoto();
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
			$('#new_photo_category').append('<option value="'+cats[c].name+'">');
		});
	})
	.fail(function (jqXHR, textStatus) {
    	alert("KO");
		console.log(textStatus);
	});
}

function getUrlParameter(sParam)
{
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



var lastFilename;
var photoid;


$("#editButton").click(function(e) {
	e.preventDefault();
	var photo = {};
	var categories = [];
	photo.title = $("#title").val();
	photo.description = $("#description").val();
	photo.categories = categories;
	var name  = $("#category").val();
	var category = {
		"name": name
	}
	photo.categories.push(category);
	console.log(photo);
	console.log(JSON.stringify(photo));
	
	editPhoto(photo);
});


function editPhoto(photo) {
	var url = API_BASE_URL + '/photos?photoid=' + idphoto;
	var data = JSON.stringify(photo);
	console.log(data);

	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		data : data,
		contentType: 'application/vnd.rahnam.api.photo+json'
		
	}).done(function(data, status, jqxhr) {
				var response = $.parseJSON(jqxhr.responseText);
				window.location.replace('single-photo.html?photoid='+response.photoid);
	})		
  	.fail(function (jqXHR, textStatus) {
    	alert("KO");
		console.log(textStatus);
	});

}



function getPhoto() {
	var url = API_BASE_URL +'/photos/photo/' + idphoto;

	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		
	}).done(function(data, status, jqxhr) {

		var response = $.parseJSON(jqxhr.responseText);
		var newimageblock = '<div class="img_block"><a href="single-photo.html?photoid='+response.photoid+'">'+
				'<img  class="img-responsive img-hover" src="'+response.photoURL+'" alt=""></a><p class="p1">'+response.title+'</p>'+
				'<hr class="h0"><p class="p2">De: <a href="#">'+response.username+'</a></p></div>';
		$('#resultphoto').append(newimageblock)
		
	})
	.fail(function (jqXHR, textStatus) {
    	alert("KO");
		console.log(textStatus);
	});
			
}
