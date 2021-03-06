var API_BASE_URL = "http://147.83.7.158:8080/rahnam-api";
var USERNAME = Cookies.get('username');
var PASSWORD = Cookies.get('userpass');

$.ajaxSetup({
    headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});

$(document).ready(function(){
	$('#navnick').text(USERNAME);
	getCategorias();
	
	var title = getUrlParameter('title');
	var category = getUrlParameter('category');
	
	if (category == undefined) {
		getPhotosByTitle(title);
		console.log('1');
	}
	else{
		getPhotosByCategory(category);
		console.log('2');
	}
	
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


	
function getPhotosByTitle(title) {
	var URL = API_BASE_URL + '/photos/title/' + title;
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
		$.each(response.photos, function(k,v){
			var title = "";
			if (response.photos[k].title == undefined) {
				title = "Sin título";
			}
			else {
				title = response.photos[k].title;
			}
			
			
			var newimageblock = '<div class="col-md-3"><div class="img_block"><a href="single-photo.html?photoid='+response.photos[k].photoid+'">'+
				'<img  class="img-responsive img-hover" src="'+response.photos[k].photoURL+'" alt=""></a><p class="p1">'+title+'</p>'+
				'<hr class="h0"><p class="p2">De: <a href="user.html?username='+response.photos[k].username+'">'+response.photos[k].username+'</a></p></div></div>';;
			
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

function getPhotosByCategory(category) {
	var URL = API_BASE_URL + '/photos/category/' + category;
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
			
			var newimageblock = '<div class="col-md-3"><div class="img_block"><a href="single-photo.html?photoid='+response.photos[k].photoid+'">'+
				'<img  class="img-responsive img-hover" src="'+response.photos[k].photoURL+'" alt=""></a><p class="p1">'+title+'</p>'+
				'<hr class="h0"><p class="p2">De: <a href="user.html?username='+response.photos[k].username+'">'+response.photos[k].username+'</a></p></div></div>';
			
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



