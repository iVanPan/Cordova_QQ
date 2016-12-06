var app = function () {
  this.checkClientInstalled = function () {
    YCQQ.checkClientInstalled(function () {
      alert('client is installed');
    }, function () {
      // if installed QQ Client version is not supported sso,also will get this error
      alert('client is not installed');
    });
  };
  this.ssoLogin = function () {
    var checkClientIsInstalled = 0;//default is 0,only for iOS
    YCQQ.ssoLogin(function (args) {
      alert("token is " + args.access_token);
      alert("userid is " +args.userid);
      alert("expires_time is "+ new Date(parseInt(args.expires_time)) + " TimeStamp is " +args.expires_time);
    }, function (failReason) {
      alert(failReason);
    }, checkClientIsInstalled);
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

        function  onErrorLoadFs(error){
          console.log("onErrorCreateFile: ", error);
        }
      }
    }

    var info = {
      img: 'https://avatars2.githubusercontent.com/u/1687276'
    };

    zoomOutImage(info);
  };
  this.shareToQQ = function () {
    var args = {};
    args.url = "http://www.baidu.com";
    args.title = "这个是cordova QQ 分享测试";
    args.description = "这个是cordova QQ 分享测试";
    args.imageUrl = "https://www.baidu.com/img/bdlogo.png";
    args.appName = "cordova—QQ";
    YCQQ.shareToQQ(function () {
      alert("share success");
    }, function (failReason) {
      alert(failReason);
    }, args);
  };
  this.shareLocalImageToQQ = function () {
    var args = {};
    args.url = "http://www.aliyun.com";
    args.title = "这个是cordova QQ 分享本地图片测试";
    args.description = "这个是cordova QQ 分享本地图片测试";
    args.imageUrl = appintance.localImageUrl;
    args.appName = "cordova—QQ";
    YCQQ.shareToQQ(function () {
      alert("share success");
    }, function (failReason) {
      alert(failReason);
    }, args);
  };
  this.shareToQzone = function () {
    var args = {};
    args.url = "http://www.baidu.com";
    args.title = "这个是cordova QZone 分享测试";
    args.description = "这个是cordova QZone 分享测试";
    var imgs =['https://www.baidu.com/img/bdlogo.png',
      'https://www.baidu.com/img/bdlogo.png',
      'https://www.baidu.com/img/bdlogo.png'];
    args.imageUrl = imgs;
    YCQQ.shareToQzone(function () {
      alert("share success");
    }, function (failReason) {
      alert(failReason);
    }, args);
  };
  this.shareLocalImageToQzone = function () {
    var args = {};
    args.url = "http://www.baidu.com";
    args.title = "这个是cordova QZone 分享本地图片测试";
    args.description = "这个是cordova QZone 分享本地图片测试";
    args.imageUrl = [];
    args.imageUrl.push(appintance.localImageUrl);
    YCQQ.shareToQzone(function () {
      alert("share success");
    }, function (failReason) {
      alert(failReason);
    }, args);
  };
  this.logout = function () {
    YCQQ.logout(function () {
      alert('logout success');
    }, function (failReason) {
      alert(failReason);
    });
  };
  this.addToFavirtes = function(){
    var args = {};
    args.url = "http://www.baidu.com";
    args.title = "这个是cordova QQ 收藏测试";
    args.description = "这个是cordova QQ 收藏测试";
    args.imageUrl = "https://www.baidu.com/img/bdlogo.png";
    args.appName = "cordova—QQ";
    YCQQ.addToQQFavorites(function () {
      alert("share success");
    }, function (failReason) {
      alert(failReason);
    }, args);
  }
}
