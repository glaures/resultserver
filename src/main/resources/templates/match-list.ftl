<!doctype html>
<#import "/spring.ftl" as spring/>
<html lang="en">
<head>
    <#include "header.ftl">
</head>
<body>

<div id="app">
    <div class="container">
        <h1 class="page-title">Spiele ${startDate}-${endDate}</h1>
        <div v-for="(m, index) in matchList" :key="m.id" class="row nowrap pb-1">
            <div v-if="isShowChallenge(index)" class="col-12 text-muted pt-2 pb-1">{{m.challenge}}</div>
            <div class="col" style="height: 50px">
                <img :id="m.team1" :src="m.team1EmblemUrl"/>&nbsp;{{m.team1}}<br/>
                Stärke: {{m.strengthTeam1}} | Platz  {{m.posTeam1}}
            </div>
            <div class="col" style="height: 50px">
                -&nbsp;<img :src="m.team2EmblemUrl"/>&nbsp;{{m.team2}}
            </div>
        </div>
    </div>
</div>

<#include "footer.ftl">
</body>
<script>

    var app = new Vue({
        el: '#app',
        data: {
            matchList: ${matchListJson},
            lastChallenge: ""
        },
        methods: {
            isShowChallenge: function (realMatchIdx) {
                if (realMatchIdx === 0)
                    return true;
                return this.matchList[realMatchIdx - 1].challenge !== this.matchList[realMatchIdx].challenge;
            },
            replaceBrokenImage: function(teamId) {

                document.getElementById(teamId).src = 'https://www.google.com/images/srpr/logo11w.png';
            }
        }
    });

</script>
</html>
