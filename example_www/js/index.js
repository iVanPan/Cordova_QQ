var app = function () {
    this.checkClientInstalled = function () {
        QQSDK.checkClientInstalled(function () {
                                   alert('client is installed');
                                   }, function () {
                                   // if installed QQ Client version is not supported sso,also will get this error
                                   alert('client is not installed');
                                   });
    };
    this.ssoLogin = function () {
        QQSDK.ssoLogin(function (args) {
                       alert("token is " + args.access_token);
                       alert("userid is " +args.userid);
                       alert("expires_time is "+ new Date(parseInt(args.expires_time)) + " TimeStamp is " +args.expires_time);
                       }, function (failReason) {
                       alert(failReason);
                       });
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
        args.scene = QQSDK.Scene.QQ;
        args.text = "这个是Cordova QQ分享文字";
        QQSDK.shareText(function () {
                        alert('shareText success');
                        }, function (failReason) {
                        alert(failReason);
                        },args);
    };
    this.shareImage = function () {
        console.log('file path is',appintance.localImageUrl);
        var args = {};
        args.scene = QQSDK.Scene.QQ;
        args.title = "这个是Cordova QQ图片分享的标题";
        args.description = "这个是Cordova QQ图片分享的描述";
        args.image = appintance.localImageUrl;//"https://cordova.apache.org/static/img/cordova_bot.png";
        QQSDK.shareImage(function () {
                         alert('shareImage success');
                         }, function (failReason) {
                         alert(failReason);
                         },args);
    };
    this.shareNews = function () {
        var args = {};
        args.scene = QQSDK.Scene.QQ;
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
        args.scene = QQSDK.Scene.QQ;
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
    this.prepareImage = function() {
        var getBase64Image = function(img) {
            var canvas = document.createElement("canvas");
            canvas.width = 200;
            canvas.height = 200;
            var ctx = canvas.getContext("2d");
            ctx.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);
            var dataURL = canvas.toDataURL();
            return dataURL;
        }
        var zoomOutImage = function(info) {
            var image = new Image();
            image.src = info.img;
            image.onload = function() {
                var imageData = getBase64Image(image);
                function dataURLtoBlob(dataUrl) {
                    var arr = dataUrl.split(','), mime = arr[0].match(/:(.*?);/)[1],
                    bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
                    while(n--){
                        u8arr[n] = bstr.charCodeAt(n);
                    }
                    return new Blob([u8arr], {type:mime});
                }
                var dataObj = dataURLtoBlob(imageData);
                window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, function (fs) {
                                         console.log('file system open: ' + fs.name);
                                         createFile(fs.root, "shareImage.png", false);
                                         }, onErrorLoadFs);
                function createFile(dirEntry, fileName, isAppend) {
                    dirEntry.getFile(fileName, {create: true, exclusive: false}, function(fileEntry) {
                                     writeFile(fileEntry, dataObj, isAppend);
                                     }, onErrorCreateFile);
                }
                function writeFile(fileEntry, dataObj) {
                    fileEntry.createWriter(function (fileWriter) {
                                           fileWriter.onwriteend = function() {
                                           appintance.localImageUrl = fileEntry.toURL().split("://")[1];
                                           console.log(appintance.localImageUrl);
                                           };
                                           fileWriter.onerror = function (e) {
                                           console.log("Failed file write: " + e.toString());
                                           };
                                           fileWriter.write(dataObj);
                                           });
                }
                function onErrorCreateFile(error){
                    console.log("onErrorCreateFile: ", error);
                }
                function  onErrorLoadFs(error){
                    console.log("onErrorCreateFile: ", error);
                }
            }
        }
        var info = {
        img: 'https://avatars2.githubusercontent.com/u/1687276'
        };
        zoomOutImage(info);
    };
}
