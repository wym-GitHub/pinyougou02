 //控制层 
app.controller('userController' ,function($scope,userService){
	
//注册

	$scope.reg=function () {
		//判断确认密码是否正确
			if($scope.entity.password!=$scope.password){
				alert("两次密码输入不一致,请重新输入");
				return;
			}
			if($scope.code==null||$scope.code==""){
				alert("请填写验证码")
				return;
			}
			userService.add($scope.entity,$scope.code).success(
				function (response) {
					if(response.success){
                        alert(response.message);
                        $scope.entity={};
                        $scope.code="";
                        $scope.password="";
					}else{
                        alert(response.message);

                    }


                }
			)

    }

    $scope.sendCode=function () {

		if($scope.entity.phone==null||$scope.entity.phone==""){
			alert("手机号不能为空")

			return ;
		}
        userService.sendCode($scope.entity.phone).success(
        	function (response) {
				if(response.success){
					alert(response.message);
				}else{
                    alert(response.message);
				}
            }
		)
    }



});	
