app.controller('searchController',function ($scope,$location,searchService) {

$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'',pageNo:1,pageSize:40,'sort':'','sortField':''};
    $scope.search=function () {
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);//当前页转为int类型
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;

                buildPageLabel();

            }
        )
    }

    //添加搜索项

    $scope.addSearchItem=function (key,value) {
        if(key=='category'||key=='brand'||'price'){
            $scope.searchMap[key]=value;

        }else{
            $scope.searchMap.spec[key]=value;

        }

        $scope.search();

    }

    $scope.removeSearchItem=function (key) {
        if(key=='category'||key=='brand'||'price'){
            $scope.searchMap[key]="";

        }else{
            delete $scope.searchMap.spec[key];//移除属性

        }
        $scope.search();
    }


    //显示标签页
    buildPageLabel=function () {
        $scope.pageLabel=[];//每次刷新,清空标签页数组
        var firstPage=1; //起始页
        var lastPage=$scope.resultMap.totalPages;//截止页
        $scope.firstDot=true;
        $scope.lastDot=true;
        if($scope.resultMap.totalPages>5){

            if($scope.searchMap.pageNo<=3){
                lastPage=5;
                $scope.firstDot=false;
            }
            else if($scope.searchMap.pageNo>=$scope.resultMap.totalPages-2){
                firstPage=$scope.resultMap.totalPages-4;
                $scope.lastDot=false;
            }
            else{
                //显示当前页为中心的五页
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;

            }
        }else{
            $scope.firstDot=false;
            $scope.lastDot=false;
        }

        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }
    //根据页码查询
    $scope.queryByPage=function (pageNo) {
        if(pageNo<1||pageNo>$scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search()

    }

    //上一页不可用判断
    $scope.isTopPage=function () {
        if( $scope.searchMap.pageNo==1){
            return true;
        }else{
            return false;
        }
    }
    //下一页不可用判断
    $scope.isEndPage=function () {
        if( $scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    }
    //排序
    $scope.sortSearch=function (sort,sortField) {
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField=sortField;
        $scope.search();
    }

    //隐藏品牌列表
    $scope.keywordsIsBrand=function () {
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                    return true;
            }
        }
        return false;
    }
    //接受广告业传递的页面参数,进行搜索
$scope.loadkeywords=function () {

    $scope.searchMap.keywords=$location.search()['keywords'];
    $scope.search();
}

})