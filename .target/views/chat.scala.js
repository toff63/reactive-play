@(user:String)(implicit r: RequestHeader)

$(function() {
	var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var sock = new WS("@routes.Application.chat(user).webSocketURL()")

	sock.onmessage = function(event) {
		$('#chat').append(event.data + "<br />")
	};

	$(document).keypress(function(e) {
	    if(e.which == 13) {
	    	sock.send($('#msg').val());
			$('#msg').val("");
	    }
	});          
});