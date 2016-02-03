/**
 * Share Extension plugin
 * @iOS
 */
(function(cordova){
    var shareExtensionPlugin = function() {};

    shareExtensionPlugin.install = function(){
        if (!window.plugins) {
            window.plugins = {};
        }

        window.plugins.shareExtensionPlugin = new shareExtensionPlugin();
    };

    shareExtensionPlugin.prototype.storeInformation = function(params, success, fail) {
        return cordova.exec(function(args) {
            success(args);
        }, function(args) {
            fail(args);
        }, 'ShareExtensionPlugin', 'shareExtensionPluginStoreInformation', [params]);
    };
    
    shareExtensionPlugin.prototype.manageExtra = function (success, fail) {
        return cordova.exec(function(args) {
            success(args);
        }, function(args) {
            fail(args);
        }, 'ShareExtensionPlugin', 'getDeepLinkingStatus', []);
    };

    shareExtensionPlugin.prototype.logout = function (success, fail) {
        return cordova.exec(function(args) {
            success(args);
        }, function(args) {
            fail(args);
        }, 'ShareExtensionPlugin', 'shareExtensionPluginLogout', []);
    };

    shareExtensionPlugin.install();

})(window.cordova);