<!doctype html>
<head>
<#include "header.ftl">
</head>
<body>

<div class="container">
    <div class="row">
        <div class="col-12">
            <h1>Sandkastenliga</h1>
        </div>
    </div>
    <div class="row">
        <div class="text-muted help col-lg-10">Willkommen</div>
    </div>
    <div class="row card m-1">
        <div class="col-12 card-header">
            Login
        </div>
        <div class="col-12 p-3 card-body">
            <form action="login.do" method="post">
                <div class="form-group row">
                    <label for="email"
                           class="col-sm-3 col-form-label d-none d-sm-block">Email</label>
                    <div class="col-12 col-sm-5">
                        <input type="email" class="form-control" id="email" name="email"
                               placeholder='Email'
                               value="${email!}"/>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="password"
                           class="col-sm-3 col-form-label d-none d-sm-block">Passwort</label>
                    <div class="col-12 col-sm-5">
                        <input type="password" id="password" name="password" class="form-control"
                               placeholder='Password' value="${password!}"/>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-12 offset-sm-3 col-sm-3 pt-1">
                        <button type="submit" class="form-control btn btn-primary">Login</button>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-12">
                         Passwort vergessen?
                        <a href="/forgotpassword.do"
                           id="forgotPasswordLink">Gib Deine Email Adresse ein und klicke hier!</a>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <div class="form-group row pt-3">
        <div class="col-12">
            <span class="pr-2">Noch kein Sandkastenliga-Trainer?</span>
            <a class="btn btn-outline-primary"
               href="/registration">Klicke hier, um Dein Team kennenzulernen!</a>
        </div>
    </div>
</div>

<#include "footer.ftl">
</body>
</html>