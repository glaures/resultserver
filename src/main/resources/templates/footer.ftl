<#assign collapsed=false/>
<#if info??>
    <#assign alertType="alert-success"/>
<#elseif warning??>
    <#assign alertType="alert-warning"/>
<#elseif error??>
    <#assign alertType="alert-danger"/>
<#else>
    <#assign collapsed=true>
</#if>

<div class="container-fluid">
    <div class="row">
        <div class="col-9">
            <div id="info" class="${(collapsed)?then("collapse", "")} info">
                <div id="infoText" class="alert alert-dismissible ${alertType!}">
                    <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                    <#if warning??>
                        <strong>Achtung</strong>:&nbsp;${warning!}
                    <#elseif error??>
                        <strong>Fehler</strong>:&nbsp;${error!}
                    <#else>
                        ${info!}
                    </#if>
                </div>
            </div>
        </div>
    </div>
</div>

<hr/>
<div class="container-fluid">
    <div class="row">
        <div class="col-12 col-md-6 footer-links text-center text-sm-right">
            <span class="small help">
                &copy;Sandkastenliga 2017-2019
            </span>
        </div>
    </div>
</div>

<link rel="icon" href="/favicon.ico" type="image/vnd.microsoft.icon">
<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.3.1.min.js" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<!-- development version, includes helpful console warnings -->
<script src="/js/vue.min.js"></script>
<script src="/js/main.js"></script>
