#!/usr/bin/env node
module.exports = function (context) {
    var fs = context.requireCordovaModule('fs');
    var options = context.opts;
    var androidLibDir = options.projectRoot + '/platforms/android/libs/';
    var android_support_V4_file = 'android-support-v4.jar';  
    var V4JarFileTarget = androidLibDir + android_support_V4_file;
    console.log(V4JarFileTarget);
    if (context.opts.cordova.platforms.indexOf("android") === -1) {
        console.info("Android platform has not been added.");
        return ;
    }
    if (context.hook == 'before_plugin_install') {
        if (fs.existsSync(V4JarFileTarget)) {
            try{
                fs.unlinkSync(V4JarFileTarget);
            }catch(e){
             console.error('Error caught by catch block:', e);
         }
     }
 }
};
