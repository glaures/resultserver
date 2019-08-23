<!doctype html>
<#import "/spring.ftl" as spring/>
<html lang="en">
<head>
    <#include "header.ftl">

    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
        google.charts.load('current', {'packages':['corechart']});
        google.charts.setOnLoadCallback(drawChart);
        function drawChart() {
            var data = new google.visualization.DataTable();
            data.addColumn('date', 'Datum');
            data.addColumn('number', 'Stärke');
            data.addRows([
                <#list team.teamStrengthSnapshots as tss>
                    <#if (tss_index > 0)>,</#if>[new Date('${tss.snapshotDate}'), ${tss.strength}]
                </#list>
            ]);
            var options = {
                title: 'Stärkeverlauf',
                curveType: 'function',
                legend: { position: 'bottom' }
            };

            var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

            chart.draw(data, options);
        }
    </script>
</head>
<body>

<div id="app">
    <div class="container">
        <h1 class="page-title">${team.name}</h1>
        <div id="curve_chart"></div>
    </div>
</div>

<#include "footer.ftl">
</body>

</html>
