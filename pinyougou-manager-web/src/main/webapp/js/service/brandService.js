app.service('brandService',function ($http) {
    //按条件分页查询
    this.findPage=function (pageNum,pageSize,conditions) {
        return $http.post('../brand/findPage.do?pageNum='+pageNum+'&pageSize='+pageSize,conditions);
    }

    //品牌新增
    this.insert=function (entity) {
        return $http.post("../brand/insert.do",entity);
    }
    //修改品牌信息
    this.update=function(entity){
        return $http.post("../brand/update.do",entity);
    }
    //查找单个
    this.findOne=function (id) {
        return $http.get("../brand/findById.do?id="+id);
    }
    //删除
    this.delete=function (ids) {
        return $http.get("../brand/delete.do?ids="+ids);
    }

    this.selectOptionList=function () {
        return $http.get("../brand/selectOptionList.do");
    }
})