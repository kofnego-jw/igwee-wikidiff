/**
 * Main JS File for configuration of AngularJS
 *
 * Polutes the global namespace with 2 variables:
 *
 * useMock:boolean (For switching between TestServer and main app)
 * myApp:ngModule (the AngularJS Module)
 *
 * Created by Joseph on 03.06.2016.
 */

var useMock = false;

var myApp = angular.module("myApp", ['ngSanitize','ngFileUpload', 'ui.router']);

/**
 * loadingService
 * functions
 *   getLoading():boolean
 *   setLoading(boolean)
 *     broadcasts "LoadingChanged"
 */
myApp.service("loadingService", ["$rootScope", 
    function($rootScope) {
        var loading = false;
        this.getLoading = function() {
            return loading;
        };
        this.setLoading = function(bool) {
            loading = bool;
            $rootScope.$broadcast("LoadingChanged");
        }
    }
]);

/**
 * revisionService
 * Uses: $http, $rootScope, loadingService
 *
 * Functions:
 *   fetchRevisions():Unit Fetches Revision to the local field
 *     broadcasts "RevisionListAvailable" after getting the list.
 *   clearRevisionList():Unit clears the revisionList
 *   getRevisionList():RevisionFW[]
 *   getContributorList():ContributorFW[]
 *   getRevisionCount():number Number of revisions
 *   getRevisionPosition(id:string):number Position (0based) of the revision with the id. -1 if not found
 *   getRevIdByPos(position:number):string The ID of the revision at position position. Or null if not found
 *
 */
myApp.service("revisionService", ["$http", "$rootScope", "loadingService",
    function($http, $rootScope, loadingService) {
        
        var revisionList = [];

        var contributorList = [];
        
        this.fetchRevisions = function() {
            if (revisionList.length!==0) return;
            var url = useMock ? "http://localhost:1415/list" : "list";
            loadingService.setLoading(true);
            $http({
                url: url,
                method: 'GET'
            }).then(function(resp) {
                revisionList = resp.data.revisionList;
                contributorList = resp.data.contributorList;
                loadingService.setLoading(false);
                $rootScope.$broadcast("RevisionListAvailable");
            }, function(resp){
                console.log(resp);
                alert("Cannot get the revision list.");
                loadingService.setLoading(false);
            });
        };
        this.clearRevisionList = function() {
            revisionList = [];
            contributorList = [];
        };
        this.getRevisionList = function() {
            return revisionList;
        };
        this.getContributorList = function() {
            return contributorList;
        };
        this.getRevisionCount = function() {
            return revisionList.length;
        };
        this.getRevisonPosition = function(revId) {
            for (var i=0; i<revisionList.length; i++) {
                if (revisionList[i].id === revId) return i;
            }
            return -1;
        };
        this.getRevIdByPos = function(pos) {
            if (pos <0 || pos > revisionList.length - 1) return null;
            return revisionList[pos].id;
        };
    }
]);

/**
 * loadingController
 * Uses: loadingService, $scope
 * $scope fields:
 *   $scope.loading:boolean loading
 * Listens to
 *   "LoadingChanged"
 */
myApp.controller("loadingController", ["loadingService", "$scope",
    function (loadingService, $scope) {
        $scope.loading = loadingService.getLoading();
        $scope.$on("LoadingChanged", function() {
            $scope.loading = loadingService.getLoading();
        });
    }
]);

/**
 * diffController
 * Uses: revisionService, $scope, $http, $anchorScroll
 * $scope fields:
 *   revId1, revId2, diffUrl, showing, sourceNow, breakMode, filterTerm: string
 *   diffFW: object
 * functions:
 *   doDiff():Unit Uses $scope.revId1 and $scope.revId2 to compare revisions.
 *   setShowing(mode:string):Unit sets the $scope.sourceNow to mode. Mode should be "text1", "text2" or "diff"
 *   setRevisionId(position:string, id:string):Unit Sets the id to position. Position should be "1" or "2"
 *   toggleBreakMode():Unit toggles $scope.breakMode between "wspre" and "wsprewrap"
 *   hasPrevRevision():boolean returns true if revId1 has previous revision
 *   hasNextRevision():boolean returns true if revId2 has next revision
 *   diffWithPrev():Unit auto diff revId1 with the previous revision
 *   diffWithnext():Unit auto diff revId2 with the next revision
 *
 * Listens to
 *   "RevisionListAvailable"
 */
myApp.controller("diffController", ["revisionService", "$scope", "$http", "$anchorScroll",
    function(revisionService, $scope, $http, $anchorScroll) {
        var self = this;
        $scope.revId1 = "";
        $scope.revId2 = "";
        $scope.diffUrl = "templates/diffStart.html";
        $scope.diffFW = {};
        $scope.showing = "diff";
        $scope.sourceNow = "";
        $scope.breakMode = "wsprewrap";
        $scope.filterTerm = "";
        revisionService.fetchRevisions();
        this.doDiffHtml = function() {
            var rv1 = $scope.revId1;
            if (rv1==="") rv1 = revisionService.getRevisionList()[0].id;
            var rv2 = $scope.revId2;
            if (rv2==="") rv2 = revisionService.getRevisionList()[1].id;
            var url = useMock ? "http://localhost:1415/diffHtml/" + rv1 + "/" + rv2 :
                "diff/" + rv1 + "/" + rv2 ;
            $scope.diffUrl = url;
        };

        this.doDiff = function() {
            var rv1 = $scope.revId1;
            if (rv1==="") rv1 = revisionService.getRevisionList()[0].id;
            var rv2 = $scope.revId2;
            if (rv2==="") rv2 = revisionService.getRevisionList()[1].id;
            var url = useMock ? "http://localhost:1415/diff/" + rv1 + "/" + rv2 :
            "diff/" + rv1 + "/" + rv2 ;
            $http({
                url: url,
                method: 'GET'
            }).then(function(resp){
                var diffFW = resp.data;
                $scope.diffFW = diffFW;
                $scope.sourceNow = diffFW.diff;
            }, function(resp){
                console.log(resp);
                alert("Cannot perform diff.");
            });
        };

        this.setShowing = function(showing) {
            if (showing === 'text1') {
                $scope.sourceNow = $scope.diffFW.text1;
            } else if (showing === 'text2') {
                $scope.sourceNow = $scope.diffFW.text2;
            } else {
                $scope.sourceNow = $scope.diffFW.diff;
            }
        };

        $scope.revisionList = revisionService.getRevisionList();
        $scope.$on("RevisionListAvailable", function() {
            $scope.revisionList = revisionService.getRevisionList();
        });
        this.setRevisionId = function(position, id) {
            if (position==="1") {
                $scope.revId1 = id;
            } else {
                $scope.revId2 = id;
            }
            $anchorScroll('a' + id);
            $anchorScroll('b' + id);
            $scope.diffFW = {};
        };
        
        this.toggleBreakMode = function() {
            if ($scope.breakMode==='wspre') 
                $scope.breakMode = "wsprewrap";
            else
                $scope.breakMode = "wspre";
        };

        this.hasPrevRevision = function() {
            if (!$scope.revId1) return false;
            var pos = revisionService.getRevisonPosition($scope.revId1);
            return pos >= 1;

        };

        this.hasNextRevision = function() {
            if (!$scope.revId2) return false;
            var pos = revisionService.getRevisonPosition($scope.revId2);
            if (pos<0) return false;
            var count = revisionService.getRevisionCount();
            return pos < count - 1;
        };

        this.diffWithPrev = function() {
            var pos1 = revisionService.getRevisonPosition($scope.revId1);
            var prevId = revisionService.getRevIdByPos(pos1-1);
            if (prevId==null) {
                alert("Cannot find previous id.");
                return;
            }
            $scope.revId2 = $scope.revId1;
            self.setRevisionId("1", prevId);
            self.doDiff();
        };
        this.diffWithNext = function() {
            var pos2 = revisionService.getRevisonPosition($scope.revId2);
            var nextId = revisionService.getRevIdByPos(pos2+1);
            if (nextId==null) {
                alert("Cannot find next id.");
                return;
            }
            $scope.revId1 = $scope.revId2;
            self.setRevisionId("2", nextId);
            self.doDiff();
        };
        
    }]);

/**
 * viewRevisionController
 * Uses: $scope, revisionService, $stateParams, $anchorScroll
 * $scope fields
 *   viewHtml:string content of the html view port
 *   revisionList:RevisionFW[]
 *   revId:string
 * Listens to
 *   "RevisionListAvailable"
 * Functions
 *   setRevisionId(id:string) sets the revision id to be viewed
 *   hasPrevRevision():boolean true if there is a prev revision
 *   hasNextRevision():boolean true if there is a next revision
 *   viewPrevRevision():Unit view the previous revision as HTML
 *   viewNextRevision():Unit view the next revision as HTML
 *
 */
myApp.controller("viewRevisionController", ["$scope", "revisionService", "$stateParams", "$anchorScroll", "$http", "loadingService",
    function ($scope, revisionService, $stateParams, $anchorScroll, $http, loadingService) {
        var self = this;
        $scope.viewHtml = "";
        $scope.filterTerm = "";
        revisionService.fetchRevisions();
        $scope.revisionList = revisionService.getRevisionList();
        $scope.$on("RevisionListAvailable", function() {
            $scope.revisionList = revisionService.getRevisionList();
        });
        $scope.revId = "";
        this.setRevisionId = function(id) {
            $scope.revId = id;
            $anchorScroll('a' + id);
            loadingService.setLoading(true);
            var url = useMock ? "http://localhost:1415/view/" + id : "view/" + id;

            $http({
                method: 'GET',
                url:    url
            }).then(function(resp){
                loadingService.setLoading(false);
                $scope.viewHtml = resp.data;
            }, function(resp){
                alert("Cannot view the revision.");
                console.log(resp);
                loadingService.setLoading(false);
            });
        };
        if (typeof $stateParams.revId !== 'undefined') {
            self.setRevisionId($stateParams.revId);
        } else {
            $http({
                method: 'GET',
                url:     "templates/viewStart.html"
            }).then(function(resp) {
                $scope.viewHtml = resp.data;
            }, function(resp) {
                alert("Cannot load the default start page.");
                console.log(resp);
            });
        }
        this.hasPrevRevision = function() {
            if (!$scope.revId) return false;
            var pos = revisionService.getRevisonPosition($scope.revId);
            return pos>0;
        };
        this.hasNextRevision = function() {
            if (!$scope.revId) return false;
            var pos = revisionService.getRevisonPosition($scope.revId);
            return pos<revisionService.getRevisionCount()-1;
        };
        this.viewPrevRevision = function() {
            var pos = revisionService.getRevisonPosition($scope.revId) - 1;
            if (pos<0) pos = 0;
            var prevId = revisionService.getRevIdByPos(pos);
            self.setRevisionId(prevId);
        };
        this.viewNextRevision = function() {
            var pos = revisionService.getRevisonPosition($scope.revId) + 1;
            var maxPos = revisionService.getRevisionCount() - 1;
            if (pos>maxPos) pos = maxPos;
            var nextId = revisionService.getRevIdByPos(pos);
            self.setRevisionId(nextId);
        };

    }
]);

/**
 * userRevisionController
 * Uses. $scope, revisionService, $stateParams, $anchorScroll, loadingService, $http
 * $scope fields:
 *   @deprecated userRevisionUrl:string
 *   filterTerm:string
 *   breakMode:string def to "wsprewrap"
 *   contentHtml:string
 *   contributorList:ContributorFW[]
 *   showOriginal:boolean
 *   showDiff:boolean
 * Listens to
 *   "RevisionListAvailable"
 * functions
 *   setContributorId(id:string):Unit
 *   resetView():Unit
 *   toggleBreakMode():Unit
 *   toggleOriginal():Unit show/hide original texts
 *   toggleDiff():Unit show/hide diff texts
 *   showOriginal():Unit
 *   hideOriginal():Unit
 *   showDiff():Unit
 *   hideDiff():Unit
 *
 */
myApp.controller("userRevisionController", ["$scope", "revisionService", "$stateParams", "$anchorScroll", "loadingService", "$http",
    function ($scope, revisionService, $stateParams, $anchorScroll, loadingService, $http) {
        var self = this;
        $scope.userRevisionUrl = "";
        $scope.filterTerm = "";
        $scope.breakMode = "wsprewrap";
        $scope.contentHtml = "";
        revisionService.fetchRevisions();
        $scope.contributorList = revisionService.getContributorList();
        $scope.$on("RevisionListAvailable", function() {
            $scope.contributorList = revisionService.getContributorList();
        });
        $scope.showOriginal = true;
        $scope.showDiff = true;
        this.setContributorId = function(id) {
            var url = useMock ? "http://localhost:1415/user/" + id : "user/" + id;
            $scope.userRevisionUrl = url;
            $anchorScroll('a' + id);
            loadingService.setLoading(true);
            $http({
                method: "GET",
                url: url
            }).then(function(resp) {
                $scope.contentHtml = resp.data;
                $anchorScroll('userStart');
                loadingService.setLoading(false);
                setTimeout(self.resetView, 1000);
            }, function(resp) {
                console.log(resp);
                alert("Cannot load the user revisions.");
                loadingService.setLoading(false);
            });
        };
        this.resetView = function() {
            if ($scope.showOriginal) {
                self.showOriginal();
            } else {
                self.hideOriginal();
            }
            if ($scope.showDiff) {
                self.showDiff();
            } else {
                self.hideDiff();
            }
        };
        this.toggleBreakMode = function() {
            if ($scope.breakMode==='wspre')
                $scope.breakMode = "wsprewrap";
            else
                $scope.breakMode = "wspre";
        };
        this.toggleOriginal = function() {
            if ($scope.showOriginal)
                self.hideOriginal();
            else
                self.showOriginal();
        };
        this.toggleDiff = function() {
            if ($scope.showDiff)
                self.hideDiff();
            else
                self.showDiff();
        };
        this.showOriginal = function() {
            angular.element(document).find(".originalText").css("display", "block");
            $scope.showOriginal = true;
        };
        this.hideOriginal = function() {
            angular.element(document).find(".originalText").css("display", "none");
            $scope.showOriginal = false;
        };
        this.showDiff = function() {
            angular.element(document).find(".diffText").css("display", "block");
            $scope.showDiff = true;
        };
        this.hideDiff = function() {
            angular.element(document).find(".diffText").css("display", "none");
            $scope.showDiff = false;
        };
        if (typeof $stateParams.userId !== 'undefined') {
            self.setContributorId($stateParams.userId);
        }
        $http({method: "GET", url: "templates/userStart.html"})
            .then(function(resp) {
                $scope.contentHtml = resp.data;
            });
    }
]);

/**
 * uploadController
 * Uses: $scope, Upload, loadingService, revisionService, $http
 * $scope fields
 *   languageCode:string
 *   languageCodes:string[] predefined list of possible language codes
 *   pageTitle:string
 *   autoIngest:boolean
 *   submit():Unit submit the form and upload the file
 *   upload(file:File):Unit uploads the file
 * functions
 *   downloadFromWikipedia():Unit
 *
 */
myApp.controller('uploadController', ['$scope', 'Upload', 'loadingService', 'revisionService', '$http',
    function ($scope, Upload, loadingService, revisionService, $http) {
        $scope.languageCode = "en";
        $scope.languageCodes = ["en", "de", "es", "fr", "it", "ja", "ru", "sv"];
        $scope.pageTitle = "";
        $scope.autoIngest = true;
        $scope.archiveCount = 0;
        // upload later on form submit or something similar
        $scope.submit = function() {
            if ($scope.file) {
                $scope.upload($scope.file);
            }
        };

        // upload on file select or drop
        $scope.upload = function (file) {
            var url = useMock ? "http://localhost:1415/upload" : "upload";
            loadingService.setLoading(true);
            Upload.upload({
                url: url,
                data: {file: file}
            }).then(function (resp) {
                alert("Upload successful");
                loadingService.setLoading(false);
                console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
                revisionService.clearRevisionList();
            }, function (resp) {
                alert("Error while uploading.");
                loadingService.setLoading(false);
                revisionService.clearRevisionList();
                console.log('Error status: ' + resp.status);
            });
        };

        this.downloadFromWikipedia = function() {
            console.log("languageCode: " + $scope.languageCode + ", pageTitle: " + $scope.pageTitle + ", autoIngest: " + $scope.autoIngest);
            loadingService.setLoading(true);
            $http({
                method: "POST",
                url: useMock ? "http://localhost:1415/downloadFromWikipedia" : "downloadFromWikipedia",
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                data: $.param({
                    languageCode: $scope.languageCode,
                    pageTitle:    $scope.pageTitle,
                    autoIngest:   $scope.autoIngest
                })
            }).then(function(resp) {
                loadingService.setLoading(false);
                revisionService.clearRevisionList();
                var bytes = atob(resp.data.contents);
                var blob = new Blob([bytes], {type: "text/xml;encoding=UTF-8"});
                saveAs(blob, resp.data.filename);
            }, function(resp) {
                loadingService.setLoading(false);
                alert("Cannot download from Wikipedia: " + $scope.pageTitle);
                revisionService.clearRevisionList();
                console.log(resp);
            });
        };

        this.downloadArchiveFromWikipedia = function() {
            console.log("languageCode: " + $scope.languageCode +
                ", pageTitle: " + $scope.pageTitle + ", autoIngest: " + $scope.autoIngest +
                ", archiveCount: " + $scope.archiveCount);
            loadingService.setLoading(true);
            $http({
                method: "POST",
                url: useMock ? "http://localhost:1415/downloadArchiveFromWikipedia" : "downloadArchiveFromWikipedia",
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                data: $.param({
                    languageCode: $scope.languageCode,
                    pageTitle:    $scope.pageTitle,
                    autoIngest:   $scope.autoIngest,
                    archiveCount: $scope.archiveCount
                })
            }).then(function(resp) {
                loadingService.setLoading(false);
                revisionService.clearRevisionList();
                var bytes = atob(resp.data.contents);
                var blob = new Blob([bytes], {type: "text/xml;encoding=UTF-8"});
                saveAs(blob, resp.data.filename);
            }, function(resp) {
                loadingService.setLoading(false);
                alert("Cannot download including Archive from Wikipedia: " + $scope.pageTitle);
                revisionService.clearRevisionList();
                console.log(resp);
            });
        };
    }]);

/**
 * searchController
 * Uses: loadingService, $scope, $http, $anchorScroll
 * $scope fields
 *   searchTerm: string
 *   pageSize:number
 *   pageNumber:number
 *   searchResult:object
 *   pageNumberNext:number
 * functions
 *   doSearch():Unit
 *   doSearchStart():Unit
 *   totalPageNumbers():number
 *   searchPrev():Unit
 *   searchNext():Unit
 *   getFieldContents(rd:ResultDocument,fieldname:string)
 *   getTimestamp(rd:ResultDocument):string
 *   getUsername(rd:ResultDocument):string
 *   getRevisions(rd:ResultDocument):string
 *   getComment(rd:ResultDocument):string
 *   pageNumberList():number[]
 *   gotoPageNumber():Unit
 */
myApp.controller("searchController", ["loadingService", "$scope", "$http", "$anchorScroll",
    function(loadingService, $scope, $http, $anchorScroll) {
        $scope.searchTerm = "";
        $scope.pageSize = 20;
        $scope.pageNumber = 0;
        $scope.searchResult = {};
        $scope.pageNumberNext = 0;
        $scope.sortField = {name:"", ascending:true};
        var self = this;
        this.doSearch = function() {
            loadingService.setLoading(true);
            console.log("Search Term: " + $scope.searchTerm);
            var url = useMock ? "http://localhost:1415/search" : "search";
            //
            var data = $scope.sortField.name.length > 0 ?
            {
                pageNumber: $scope.pageNumber,
                pageSize: $scope.pageSize,
                querySettings: [
                    {
                        fieldname: "revision",
                        queryString: $scope.searchTerm,
                        queryType: 2 // QueryParser
                    }
                ],
                sortSettings: [{fieldname: $scope.sortField.name, ascending: $scope.sortField.ascending}]
            } : {
                    pageNumber: $scope.pageNumber,
                    pageSize: $scope.pageSize,
                    querySettings: [
                        {
                            fieldname: "revision",
                            queryString: $scope.searchTerm,
                            queryType: 2 // QueryParser
                        }
                    ]
                };

            $http({
                url: url,
                method: "POST",
                data: data
            }).then(function(resp) {
                console.log(resp.data);
                $scope.searchResult = resp.data;
                $anchorScroll("searchResultBegin");
                $scope.pageNumberNext = $scope.searchResult.searchRequest.pageNumber;
                loadingService.setLoading(false);
            }, function(resp) {
                alert("Search error!");
                console.log(resp);
                loadingService.setLoading(false);
            });
        };
        this.doSearchStart = function() {
            $scope.pageNumber = 0;
            self.doSearch();
        };
        this.totalPageNumbers = function() {
            if (typeof $scope.searchResult.totalHits === 'undefined' || $scope.searchResult.totalHits === 0) return 0;
            return Math.ceil($scope.searchResult.totalHits / $scope.searchResult.searchRequest.pageSize);
        };
        this.searchPrev = function() {
            if ($scope.pageNumber==0) return;
            $scope.pageNumber = $scope.pageNumber - 1;
            self.doSearch();
        };
        this.searchNext = function() {
            if ($scope.pageNumber >= (self.totalPageNumbers()-1)) return;
            $scope.pageNumber = $scope.pageNumber + 1;
            self.doSearch();
        };
        this.getFieldContents = function(rd, fieldname) {
            if (typeof rd.fields === 'undefined') return "";
            var contents;
            for (var i=0; i<rd.fields.length; i++) {
                if (rd.fields[i].fieldname === fieldname) {
                    contents = rd.fields[i].contents;
                }
            }
            return contents;
        };
        this.getTimestamp = function(rd) {
            return self.getFieldContents(rd, 'timestamp') [0];
        };
        this.getUsername = function(rd) {
            return self.getFieldContents(rd, 'username') [0];
        };
        this.getRevisions = function(rd) {
            return self.getFieldContents(rd, 'revision');
        };
        this.getComment = function(rd) {
            return self.getFieldContents(rd, 'comment');
        };
        this.pageNumberList = function() {
            var result = [];
            for (var i = 0; i < self.totalPageNumbers(); i++) {
                result.push(i);
            }
            return result;
        };
        this.gotoPageNumber = function() {
            $scope.pageNumber = $scope.pageNumberNext;
            self.doSearch();
        }
        this.setSortField = function(fieldname) {

            if (fieldname==="none") {
                $scope.sortField.name="";
            } else if ($scope.sortField.name===fieldname) {
                $scope.sortField.ascending = !$scope.sortField.ascending;
            } else {
                $scope.sortField.name = fieldname;
                $scope.sortField.ascending = true;
            }
            self.doSearch();
        };
    }
]);

/**
 * UI Router Configuration:
 *   Default: /start
 */
myApp.config(function($stateProvider, $urlRouterProvider, $sceProvider) {
    if (useMock) $sceProvider.enabled(false);

    $urlRouterProvider.otherwise("/start");
    //
    // Now set up the states
    $stateProvider
        .state('start', {
            url: "/start",
            templateUrl: "templates/start.html"
        })
        .state('upload', {
            url: "/upload",
            templateUrl: "templates/upload.html"
        })
        .state('diff', {
            url: "/diff",
            templateUrl: "templates/diff.html"
        })
        .state('view', {
            url: "/view",
            templateUrl: "templates/view.html"
        })
        .state('viewSingle', {
            url: "/view/:revId",
            templateUrl: "templates/view.html"
        })
        .state('user', {
            url: "/user",
            templateUrl: "templates/user.html"
        })
        .state('search', {
            url: "/search",
            templateUrl: "templates/search.html"
        })
        .state('impressum', {
            url: "/impressum",
            templateUrl: "templates/impressum.html"
        })
    ;
});
