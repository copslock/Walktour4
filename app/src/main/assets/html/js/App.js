angular.module('ionicApp', ['ionic'])

.controller('MyCtrl', function($scope,$ionicSlideBoxDelegate) {
  $scope.tabNames = [];
  $scope.pages = [];
  $scope.slectIndex= 0;

  $scope.reloadData = function(tabs,ps){
    $scope.pages = ps;
    $scope.tabNames = tabs;
    $scope.activeSlide(0);
    $scope.$apply();
    $ionicSlideBoxDelegate.update();
  };

  $scope.activeSlide=function(index){//点击时候触发
    $scope.slectIndex=index;
    $ionicSlideBoxDelegate.slide(index);
  };

  $scope.slideChanged=function(index){//滑动时候触发
    $scope.slectIndex=index;
  };

  /*
  * if given group is the selected group, deselect it
  * else, select the given group
  */
  $scope.toggleGroup = function(group) {
    group.show = !group.show;
  };
  $scope.isGroupShown = function(group) {
    return group.show;
  };
});