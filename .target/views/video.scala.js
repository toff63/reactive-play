@()(implicit r: RequestHeader)

$(function() {
	var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var client = new WS("@routes.Application.video.webSocketURL()")
	client.onmessage = function(event) {
		if($('#video > p').length  >= 25){
			$('#video >p:lt(1)').remove();
		}
		$('#video').append(event.data )
	};
});