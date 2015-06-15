var API_BASE_URL = "http://147.83.7.158:8080/rahnam-api";
var USERNAME = Cookies.get('username');
var PASSWORD = Cookies.get('userpass');


$.ajaxSetup({
    headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});

$(document).ready(function(){
	$("#username" ).val(USERNAME);
	
	$('#navnick').text(USERNAME);
	getCategorias();
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





var lastFilename;
var photoid;

$('form#imageformunico').submit(function(e){
	e.preventDefault();
	$('progress').toggle();

	var formData = new FormData($('form#imageformunico')[0]);
	console.log(formData);

	var URL = API_BASE_URL + '/photos';
	console.log(URL);
	$.ajax({
		url: URL,
		type: 'POST',
		xhr: function() {  
	    	var myXhr = $.ajaxSettings.xhr();
	        if(myXhr.upload){ 
	            myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // For handling the progress of the upload
	        }
	        return myXhr;
        },
		crossDomain : true,
		data: formData,
		cache: false,
		contentType: false,
        processData: false
	})
	.done(function (data, status, jqxhr) {
		
		var response = $.parseJSON(jqxhr.responseText);
		//window.location.replace('single-photo.html?photoid='+response.photoid);
		
		var newimageblock = '<div class="img_block"><a href="single-photo.html?photoid='+response.photoid+'">'+
				'<img  class="img-responsive img-hover" src="'+response.photoURL+'" alt=""></a><p class="p1">'+response.title+'</p>'+
				'<hr class="h0"><p class="p2">De: <a href="#">'+response.username+'</a></p></div>';
		$('#resultphoto').append(newimageblock)
			
		//lastFilename = response.filename;
		//photoid = response.photoid;
		//$('#uploadedImage').attr('src', response.photoURL);
		//$('progress').toggle();
		//$('form')[0].reset();
	})
    .fail(function (jqXHR, textStatus) {
    	alert("KO");
		console.log(textStatus);
	});
});

function progressHandlingFunction(e){
    if(e.lengthComputable){
        $('progress').attr({value:e.loaded,max:e.total});
    }
}

$('#myCarousel').carousel({
  interval:false // remove interval for manual sliding
});


//Esta ultima funcion la dejamos por si acaso aglun dia...
$('#uploadedImage').click(function(e){
	e.preventDefault();
	$.ajax({
		url: URL,
		headers: {          
                 Accept : "application/vnd.rahnam.api.photo.collection+json; charset=utf-8",         
                "Content-Type": "application/vnd.rahnam.api.photo.collection+json; charset=utf-8"   
		},
		type: 'GET',
		crossDomain : true
	})
	.done(function (data, status, jqxhr) {
		var response = $.parseJSON(jqxhr.responseText);
		$('.carousel-inner').empty();
		$.each(response.photos, function(k,v){
			if(lastFilename == response.photos[k].filename)
				$('.carousel-inner').append('<div class="item active"><img class="imgcenter" src="'+response.photos[k].photoURL+'" class="img-responsive"><div class="carousel-caption"><h2 align="center">'+response.photos[k].title+'</h2></div></div>');
			else
				$('.carousel-inner').append('<div class="item"><img class="imgcenter" src="'+response.photos[k].photoURL+'" class="img-responsive"><div class="carousel-caption"><h2 align="center">'+response.photos[k].title+'</h2></div></div>');
		});
	
		$('#carousel-modal').modal('toggle');
	})
    .fail(function (jqXHR, textStatus) {
    	alert("KO");
		console.log(textStatus);
	});
});