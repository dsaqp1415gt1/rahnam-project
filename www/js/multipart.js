var URL = "http://localhost:8080/rahnam-api/photos";
var USERNAME = "pau";
var PASSWORD = "pau";

var lastFilename;
var photoid;

$('form#imageformunico').submit(function(e){
	e.preventDefault();
	$('progress').toggle();

	var formData = new FormData($('form#imageformunico')[0]);
	console.log(formData);

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
		lastFilename = response.filename;
		photoid = response.photoid;
		$('#uploadedImage').attr('src', response.photoURL);
		$('progress').toggle();
		$('form')[0].reset();
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