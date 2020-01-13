<!doctype html>
<#import "/spring.ftl" as spring/>
<html lang="en">

<head>
    <#include "header.ftl">
</head>

<body>

<div id="app">
    <div class="container">
        <h1 class="page-title">${team.name}</h1>
        <div v-for="rm in betPeriod.realMatches" v-bind:key="rm.id" class="row nowrap bet-row">
            <div class="col-9">
                <div class="row nowrap">
                    <div class="col-6">
                        <img :src="rm.team1ImgUrl"/>&nbsp;
                        <span class="team-name">{{rm.team1}}</span>
                    </div>
                    <div class="col-2">
                        <button class="btn btn-sm btn-primary" @click.prevent="adjustBet(rm, -1, true)">-</button>
                    </div>
                    <div class="col-2">
                        <span v-if="getBetByRealMatch(rm)" class="bet-goal">{{getBetByRealMatch(rm).goalsTeam1}}</span>
                        <span v-else class="bet-goal">-</span>
                    </div>
                    <div class="col-2">
                        <button class="btn btn-sm btn-primary" @click.prevent="adjustBet(rm, 1, true)">+</button>
                    </div>
                </div>
                <div class="row nowrap">
                    <div class="col-6">
                        <img :src="rm.team2ImgUrl"/>&nbsp;
                        <span class="team-name">{{rm.team2}}</span>
                    </div>
                    <div class="col-2">
                        <button class="btn btn-sm btn-primary" @click.prevent="adjustBet(rm, -1, false)">-</button>
                    </div>
                    <div class="col-2">
                        <span v-if="getBetByRealMatch(rm)" class="bet-goal">{{getBetByRealMatch(rm).goalsTeam2}}</span>
                        <span v-else class="bet-goal">-</span>
                    </div>
                    <div class="col-2">
                        <button class="btn btn-sm btn-primary" @click.prevent="adjustBet(rm, 1, false)">+</button>
                    </div>
                </div>
            </div>
            <div class="col-1">
                <span>{{rm.quotaTeam1.toFixed(1)}}</span><br/>
                {{rm.quotaDraw.toFixed(1)}}<br/>
                {{rm.quotaTeam2.toFixed(1)}}
            </div>
            <div v-if="rm.goalsTeam1 > 0" class="col-2">
                <span class="game-goal">{{rm.goalsTeam1}}</span><br/>
                <span class="game-goal">{{rm.goalsTeam2}}</span><br/>
            </div>
            <div v-else class="col-2">
                <span class="game-goal">-</span><br/>
                <span class="game-goal">-</span><br/>
            </div>
        </div>
    </div>
</div>
<#include "footer.ftl">

<script>
    var app = new Vue({
        el: '#app',
        data: {
            strengthSnapshots: ${strengthSnapshots},
            betSlip: ${betSlipJson}
        },
        methods: {
            getBetByRealMatch(realMatch) {
                for (i = 0; i < this.betSlip.bets.length; i++) {
                    if (this.betSlip.bets[i].realMatchId == realMatch.id)
                        return this.betSlip.bets[i];
                }
            },
            adjustBet: function (realMatch, summand, home) {
                event.preventDefault();
                var bet = this.getBetByRealMatch(realMatch);
                if (!bet) {
                    bet = {
                        goalsTeam1: 0,
                        goalsTeam2: 0,
                        realMatchId: realMatch.id
                    };
                    this.betSlip.bets.push(bet);
                } else {
                    if (home) {
                        bet.goalsTeam1 += summand;
                    } else {
                        bet.goalsTeam2 += summand;
                    }
                    if (bet.goalsTeam1 < 0)
                        bet.goalsTeam1 = 0;
                    if (bet.goalsTeam2 < 0)
                        bet.goalsTeam2 = 0;
                }
                // post the bet
                $.ajax({
                    method: "POST",
                    url: "/betslip/" + this.betSlip.id,
                    data: bet,
                    context: this
                }).done(function (betSlip) {
                    this.betSlip = betSlip;
                });
            }
        }
    })

</script>

</body>

</html>
