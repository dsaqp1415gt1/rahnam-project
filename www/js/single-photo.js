var API_BASE_URL = "http://localhost:8080/rahnam-api";
var USERNAME = Cookies.get('username');
var PASSWORD = Cookies.get('userpass');
var idphoto = "";


$.ajaxSetup({
    headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});

$(document).ready(function(){
    $('#navnick').text(USERNAME);
	getCategorias();
	
	idphoto = getUrlParameter('photoid');
	console.log(idphoto);
	getPhoto();
	getComments();
	
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
			//console.log(cats[c].categoryid);
			//console.log(cats[c].name);
			$('#menucats').append('<li><a href="search.html?category='+cats[c].name+'">'+cats[c].name+'</a></li>');
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


$("#btn_post").click(function(e) {
	e.preventDefault();
	
	if($("#newcomment").val() != ""){
		var newComment = new Object();
		newComment.username = USERNAME;
		newComment.content = $("#newcomment").val();
		createComment(newComment);
	}
	else {
		$("#newcomment").val('tienes que escribir algo antes')
	}
});


function getPhoto() {
	var url = API_BASE_URL +'/photos/photo/' + idphoto;
	var contador = 0;
	console.log(url);

	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		
	}).done(function(data, status, jqxhr) {

		var photo = data;	
		$('#title').append(photo.title + ':<small> subida por <a href="#">'+photo.username+'</a></small>');
		$('#infophoto').append('<p><i class="fa fa-clock-o"></i> Subida en August 24, 2013 a las 9:00 PM</p>'+
		'<hr><img class="img-responsive" src="' +photo.photoURL+ '" alt=""><hr>'+
		'<p class="lead">Descripción: </p>'+'<p>'+photo.description+'</p>');
		$('#bloqueautor').append('<a href="user.html?username='+photo.username+'"><h4>'+photo.username+'</h4></a><p>Clica en el nombre para ver más fotos</p>');
		
		
		var cats = data.categories
		$.each(cats, function(c,v){
			if (contador < 4) {
				$('#cats1').append('<li><a href="search.html?category='+cats[c].name+'">'+cats[c].name+'</a></li>');
			}
			else if (contador < 8) {
				$('#cats2').append('<li><a href="search.html?category='+cats[c].name+'">'+cats[c].name+'</a></li>');
			}
			else if (contador < 12) {
				$('#cats3').append('<li><a href="search.html?category='+cats[c].name+'">'+cats[c].name+'</a></li>');
			}
			else {
				$('#cats4').append('<li><a href="search.html?category='+cats[c].name+'">'+cats[c].name+'</a></li>');
			}
			contador++;
		});
	})
			
}
	
	function createComment(comment) {
	var url = API_BASE_URL + '/photos/photo/' + idphoto + '/comments';
	var data = JSON.stringify(comment);
	console.log(url);

	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : data,
		contentType: 'application/vnd.rahnam.api.comment+json',
		
	}).done(function(data, status, jqxhr) {
	
	
				var comment = data;
									
				$('#comments').append('<div class="media"><a class="pull-left" href="#">'+
				'<img class="media-object" src="http://placehold.it/64x64" alt=""></a><div class="media-body">'+
				'<h4 class="media-heading">'+comment.username+'<small> August 25, 2014 at 9:30 PM</small></h4>'+
				comment.content+'</div></div>');
			
				
				})
			
  	.fail(function() {
		$('<div class="alert alert-danger"> <strong>Ups!</strong>No se ha podido subir el comentario, inténtelo de nuevo :)</div>').appendTo($("#create_result"));
	});

}

function getComments(){
	var url = API_BASE_URL + '/photos/photo/' + idphoto + '/comments';
	
	
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
				
				var comments = data.comments;
				if (comments == "")
				{
					$('#comments').append('<h4>No hay comentarios</h4>');
				}
				$.each(comments, function(i, v) {
					var comment = v;
				
					$('#comments').append('<div class="media"><a class="pull-left" href="#">'+
					'<img class="media-object" src="http://placehold.it/64x64" alt=""></a><div class="media-body">'+
					'<h4 class="media-heading">'+comment.username+'<small> August 25, 2014 at 9:30 PM</small></h4>'+
					comment.content+'</div></div>');

				});
				

	}).fail(function() {
		$("#list_coments").text("No hay comentarios en esta foto");
	});

}