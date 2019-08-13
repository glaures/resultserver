$(document).ready(function() {
	$.ajaxSetup({
		cache : false
	});
	updateUI();
});

var selectedDate = new Date();

function selectNextDate(back) {
	var newTime = selectedDate.getTime();
	newTime += (back ? -1 : 1) * (1000 * 60 * 60 * 24);
	selectedDate.setTime(newTime);
	updateUI();
}

function updateMatches() {
	$.ajax({
		type : "POST",
		url : "rest/schedule/update/" + selectedDate.getTime(),
		error : function(request, errorMsg, errorObj) {
			alert(errorMsg);
		}
	});
	updateUI();
}

function updateUI() {
	$("#selectedDate").empty();
	$("#selectedDate").append(toDateString(selectedDate));
	$(".matchRow").remove();
	var start = selectedDate.getTime();
	var end = start;// + (1000 * 60 * 60 * 24);
	$.ajax({
		type : "GET",
		url : "rest/schedule/" + start + "/" + end,
		dataType : "json",
		cache : false,
		success : function(data) {
			if (data == null) {
				var row = $(document.createElement("tr"));
				row.addClass("matchRow");
				var col0 = $(document.createElement("td"));
				col0.append("no match");
				col0.attr("colspan", "5");
				row.append(col0);
			} else {
				var matchArr = data;
				var matchTable = $("#matchList");
				for ( var i = 0; i < matchArr.length; i++) {
					var match = matchArr[i];
					var row = $(document.createElement("tr"));
					row.addClass("matchRow");
					
					var colId = $(document.createElement("td"));
					colId.append(match.id);
					row.append(colId);

					var col00 = $(document.createElement("td"));
					if(match.featured)
						col00.append("x");
					row.append(col00);

					var date = new Date(match.start);
					var col0 = $(document.createElement("td"));
					col0.append(toDateString(date));
					row.append(col0);
					var colCh = $(document.createElement("td"));
					colCh.append(match.region + ", " + match.challenge + ", " + match.round);
					row.append(colCh);
					var col1 = $(document.createElement("td"));
					col1.append(match.team1 + " (" + match.posTeam1 + ", " + match.strengthTeam1 + ")");
					row.append(col1);
					var col2 = $(document.createElement("td"));
					col2.append(match.team2 + " (" + match.posTeam2 + ", " + match.strengthTeam2 + ")");
					row.append(col2);
					var col3 = $(document.createElement("td"));
					col3.append(match.goalsTeam1 + ":" + match.goalsTeam2);
					row.append(col3);
					var col4 = $(document.createElement("td"));
					col4.append(match.matchState);
					row.append(col4);
					var col5 = $(document.createElement("td"));
					col5.append(toDateString(new Date(match.lastUpdated)));
					row.append(col5);
					matchTable.append(row);
				}
			}
		},
		error : function(request, errorMsg, errorObj) {
			alert(errorMsg);
		}
	});
}

function toDateString(date) {
	return "" + date.getDate() + "." + (date.getMonth() + 1) + "."
			+ date.getFullYear() + " " + date.getHours() + ":"
			+ date.getMinutes();
}
