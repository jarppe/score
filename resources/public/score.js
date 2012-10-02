var score;

;(function() {
	
	function draw() {
		if (!score) return;
		
		var e = $("#score");
		var g = e[0].getContext("2d");
		var w = e.width();
		var h = e.height();
		
		var data = score.data;
		var users = 0;
		var maxScore = 0;
		
		for (var name in data) {
			var s = data[name][0];
			if (s > maxScore) maxScore = s;
			users++;
		}

		var barHeight = h / users;
		var scoreWidth = w / maxScore;
		
		g.fillStyle = "#8F0000";
		g.font="20px Arial";
		
		var i = 0;
		
		var grd = g.createLinearGradient(0, 0, w, 0);
		grd.addColorStop(0, "#200000");
		grd.addColorStop(1, "#FF0000");

		for (var name in data) {
			var s = data[name][0];
			console.log(name, s, barHeight, scoreWidth);
			g.fillStyle = grd;
			g.fillRect(0, i * barHeight, s * scoreWidth, barHeight);
			g.strokeStyle = "white";
			g.fillStyle = "white";
			g.fillText(name + " (" + s + ")", 10, 30 + i * barHeight);
			i++;
		}
	}
	
	function parseScores(e) {
		return e;
	}
	
	function getParams() {
		return score ? {since: score.created} : {};
	}
	
	function updateScore() {
		$.ajax({
			url:       "rest/score",
			type:      "GET",
			dataType:  "json",
			data:      getParams(),
			cache:     false,
			timeout:   10000,
			success: function(e) {
				score = parseScores(e);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				console.log("Failed to load score updates", textStatus);
				score = null;
			},
			complete: function(jqXHR, textStatus) {
				draw();
				setTimeout(updateScore, 60000);
			}
		});
	}
	
	function resize() {
		var bodyWidth = document.body.offsetWidth;
		var bodyHeight = document.body.offsetHeight;
		$("#score")
			.attr("width", "" + bodyWidth)
			.attr("height", "" + bodyHeight);
		draw();
	}
	
	var resizeTimeout;
	
	$(function() {
		$(window).resize(function() {
			if (resizeTimeout) {
				clearTimeout(resizeTimeout);
				resizeTimeout = null;
			}
			resizeTimeout = setTimeout(resize, 200);
		});
		resize();
		setTimeout(updateScore, 1);
	});

})();
