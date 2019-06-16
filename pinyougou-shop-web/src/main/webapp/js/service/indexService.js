app.service('indexService',function ($http) {
    this.getSellerName=function () {
        return $http.get('../index/getName.do');
    }
})