var app = function () {
    this.checkClientInstalled = function () {
        var args = {};
        args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
        QQSDK.checkClientInstalled(function () {
            alert('client is installed');
        }, function () {
        // if installed QQ Client version is not supported sso,also will get this error
            alert('client is not installed');
        },args);
    };
    this.ssoLogin = function () {
        var args = {};
        args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
        QQSDK.ssoLogin(function (result) {
            alert("token is " + result.access_token);
            alert("userid is " +result.userid);
            alert("expires_time is "+ new Date(parseInt(result.expires_time)) + " TimeStamp is " +result.expires_time);
        }, function (failReason) {
            alert(failReason);
        },args);
    };
    this.logout = function () {
        QQSDK.logout(function () {
            alert('logout success');
        }, function (failReason) {
            alert(failReason);
        });
    };
    this.shareText = function () {
        var args = {};
        args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
        args.scene = QQSDK.Scene.QQ;//QQSDK.Scene.QQZone,QQSDK.Scene.Favorite
        args.text = "这个是Cordova QQ分享文字";
        QQSDK.shareText(function () {
            alert('shareText success');
        }, function (failReason) {
            alert(failReason);
        },args);
    };
    this.shareImage = function () {
        var args = {};
        args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
        args.scene = QQSDK.Scene.QQ;//QQSDK.Scene.QQZone,QQSDK.Scene.Favorite
        args.title = "这个是Cordova QQ图片分享的标题";
        args.description = "这个是Cordova QQ图片分享的描述";
        args.image = "https://cordova.apache.org/static/img/cordova_bot.png";
        QQSDK.shareImage(function () {
            alert('shareImage success');
        }, function (failReason) {
            alert(failReason);
        },args); 
    };
    this.shareBase64Image = function () {
        var base64 = "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";
        var args = {};
        args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
        args.scene = QQSDK.Scene.QQ;//QQSDK.Scene.QQZone,QQSDK.Scene.Favorite
        args.title = "这个是Cordova QQ图片分享的标题";
        args.description = "这个是Cordova QQ图片分享的描述";
        args.image = base64;
        QQSDK.shareImage(function () {
            alert('shareImage success');
        }, function (failReason) {
            alert(failReason);
        },args); 
    };
    this.shareLocalImage = function (){
        var args = {};
        args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
        args.scene = QQSDK.Scene.QQ;//QQSDK.Scene.QQZone,QQSDK.Scene.Favorite
        args.title = "这个是Cordova QQ图片分享的标题";
        args.description = "这个是Cordova QQ图片分享的描述";
        args.image = "https://cordova.apache.org/static/img/cordova_bot.png";//appintance.localImageUrl;
        navigator.camera.getPicture(function(pic){
            args.image = pic.split("://")[1];
            console.log('pic is ',pic);
            QQSDK.shareImage(function () {
                alert('shareImage success');
            }, function (failReason) {
                alert(failReason);
            },args);       
        }, null, {targetWidth:60,targetHeight:60}
        );
    };
    this.shareNews = function () {
        var args = {};
        args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
        args.scene = QQSDK.Scene.QQ;//QQSDK.Scene.QQZone,QQSDK.Scene.Favorite
        args.url = "https://cordova.apache.org/";
        args.title = "这个是Cordova QQ新闻分享的标题";
        args.description = "这个是Cordova QQ新闻分享的描述";
        args.image = "https://cordova.apache.org/static/img/cordova_bot.png";
        QQSDK.shareNews(function () {
            alert('shareNews success');
        }, function (failReason) {
            alert(failReason);
        },args);
    };
    this.shareAudio = function () {
        var args = {};
        args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
        args.scene = QQSDK.Scene.QQ;//QQSDK.Scene.QQZone,QQSDK.Scene.Favorite
        args.url = "https://y.qq.com/portal/song/001OyHbk2MSIi4.html";
        args.title = "十年";
        args.description = "陈奕迅";
        args.image = "https://y.gtimg.cn/music/photo_new/T001R300x300M000003Nz2So3XXYek.jpg";
        args.flashUrl = "http://stream20.qqmusic.qq.com/30577158.mp3";
        QQSDK.shareAudio(function () {
            alert('shareAudio success');
        }, function (failReason) {
            alert(failReason);
        },args);
    };
}
