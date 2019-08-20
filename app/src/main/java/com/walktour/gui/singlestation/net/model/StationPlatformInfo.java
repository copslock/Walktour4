package com.walktour.gui.singlestation.net.model;

import com.google.gson.annotations.SerializedName;
import com.walktour.base.gui.model.BaseNetModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 平台获取到的基站测试配置
 * Created by wangk on 2017/8/22.
 */

public class StationPlatformInfo implements BaseNetModel {
    /**
     * 基站类型 1宏站、2室分
     */
    public static final int MAPTYPE_OUTDOOR = 1, MAPTYPE_INDOOR = 2;
    /**
     * 设备类型
     */
    @SerializedName("DeviceType")
    private String deviceType;
    /**
     * EnodeBId
     */
    @SerializedName("ENodeBID")
    private int eNodeBID;
    @SerializedName("Lat")
    private double lat;
    @SerializedName("Lon")
    private double lon;
    @SerializedName("SiteAddress")
    private String siteAddress;
    @SerializedName("SiteId")
    private int siteId;
    @SerializedName("SiteName")
    private String siteName;
    /**
     * 基站类型 1宏站、2室分
     */
    @SerializedName("SiteType")
    private int siteType;
    @SerializedName("Cells")
    private List<Cell> cells;
    @SerializedName("TestScenes")
    private ArrayList<TestScene> testScenes;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getENodeBID() {
        return eNodeBID;
    }

    public void setENodeBID(int eNodeBID) {
        this.eNodeBID = eNodeBID;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getSiteType() {
        return siteType;
    }

    public void setSiteType(int siteType) {
        this.siteType = siteType;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public ArrayList<TestScene> getTestScenes() {
        return testScenes;
    }

    public void setTestScenes(ArrayList<TestScene> testScenes) {
        this.testScenes = testScenes;
    }

    /**
     * 小区信息
     */
    public class Cell {
        @SerializedName("ANT_BEAM_WIDTH")
        private int antBeamWidth;
        @SerializedName("Azimuth")
        private int azimuth;
        @SerializedName("CellName")
        private String cellName;
        @SerializedName("ECI")
        private int ECI;
        @SerializedName("Lat")
        private double lat;
        @SerializedName("Lon")
        private double lon;
        @SerializedName("PA")
        private int PA;
        @SerializedName("PB")
        private int PB;
        @SerializedName("PCI")
        private int PCI;
        @SerializedName("RsPower")
        private int rsPower;
        @SerializedName("TAC")
        private int TAC;

        public int getAntBeamWidth() {
            return antBeamWidth;
        }

        public void setAntBeamWidth(int antBeamWidth) {
            this.antBeamWidth = antBeamWidth;
        }

        public int getAzimuth() {
            return azimuth;
        }

        public void setAzimuth(int azimuth) {
            this.azimuth = azimuth;
        }

        public String getCellName() {
            return cellName;
        }

        public void setCellName(String cellName) {
            this.cellName = cellName;
        }

        public int getECI() {
            return ECI;
        }

        public void setECI(int ECI) {
            this.ECI = ECI;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public int getPA() {
            return PA;
        }

        public void setPA(int PA) {
            this.PA = PA;
        }

        public int getPB() {
            return PB;
        }

        public void setPB(int PB) {
            this.PB = PB;
        }

        public int getPCI() {
            return PCI;
        }

        public void setPCI(int PCI) {
            this.PCI = PCI;
        }

        public int getRsPower() {
            return rsPower;
        }

        public void setRsPower(int rsPower) {
            this.rsPower = rsPower;
        }

        public int getTAC() {
            return TAC;
        }

        public void setTAC(int TAC) {
            this.TAC = TAC;
        }
    }

    /**
     * 测试场景
     */
    public class TestScene {
        /**
         * 业务名称
         */
        @SerializedName("Name")
        private String name;

        /**
         * 场景名称，如切换类型下可能有：handover_gate、handover_indoor、handover_park
         */
        @SerializedName("SceneName")
        private String sceneName;
        /**
         * 场景ID
         */
        @SerializedName("SceneId")
        private int sceneId;

        @SerializedName("SceneType")
        private int sceneType;
        /**
         * 任务组
         */
        @SerializedName("TestGroup")
        private TestGroup testGroup;

        public String getSceneName() {
            return sceneName;
        }

        public void setSceneName(String sceneName) {
            this.sceneName = sceneName;
        }

        public TestGroup getTestGroup() {
            return testGroup;
        }

        public void setTestGroup(TestGroup testGroup) {
            this.testGroup = testGroup;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSceneId() {
            return sceneId;
        }

        public void setSceneId(int sceneId) {
            this.sceneId = sceneId;
        }

        public int getSceneType() {
            return sceneType;
        }

        public void setSceneType(int sceneType) {
            this.sceneType = sceneType;
        }
    }

    public class TestGroup {
        @SerializedName("GroupId")
        private int groupId;//1
        @SerializedName("Interval")
        private int Interval;// 0
        @SerializedName("Name")
        private String name;//"组1"
        @SerializedName("RepeatCount")
        private int repeatCount;//1
        @SerializedName("Tests")
        private ArrayList<TestTask> tests;

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public int getInterval() {
            return Interval;
        }

        public void setInterval(int interval) {
            Interval = interval;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRepeatCount() {
            return repeatCount;
        }

        public void setRepeatCount(int repeatCount) {
            this.repeatCount = repeatCount;
        }

        public ArrayList<TestTask> getTests() {
            return tests;
        }

        public void setTests(ArrayList<TestTask> tests) {
            this.tests = tests;
        }
    }

    /**
     * 测试任务
     */
    public class TestTask {
        /**
         * 任务序号
         */
        @SerializedName("ItemId")
        private int itemId;
        /**
         * 重复次数
         */
        @SerializedName("RepeatCount")
        private int repeatCount;
        /**
         * 测试名称
         */
        @SerializedName("TestName")
        private String testName;

        @SerializedName("TaskType")
        private String taskType;
        /**
         * 测试计划
         */
        @SerializedName("TestPlan")
        private String testPlan;

        public String getTaskType() {
            return taskType;
        }

        public void setTaskType(String taskType) {
            this.taskType = taskType;
        }

        @SerializedName("Conditions")
        private ArrayList<Condition> conditions;

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public int getRepeatCount() {
            return repeatCount;
        }

        public void setRepeatCount(int repeatCount) {
            this.repeatCount = repeatCount;
        }

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public String getTestPlan() {
            return testPlan;
        }

        public void setTestPlan(String testPlan) {
            this.testPlan = testPlan;
        }

        public ArrayList<Condition> getConditions() {
            return conditions;
        }

        public void setConditions(ArrayList<Condition> conditions) {
            this.conditions = conditions;
        }

        /**
         * 通过条件
         */
        public class Condition {
            @SerializedName("KPI")
            private String KPI;
            @SerializedName("Task")
            private String task;
            @SerializedName("operator")
            private String operator;
            @SerializedName("unit")
            private String unit;
            @SerializedName("value")
            private float value;

            public String getKPI() {
                return KPI;
            }

            public void setKPI(String KPI) {
                this.KPI = KPI;
            }

            public String getTask() {
                return task;
            }

            public void setTask(String task) {
                this.task = task;
            }

            public String getOperator() {
                return operator;
            }

            public void setOperator(String operator) {
                this.operator = operator;
            }

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }

            public float getValue() {
                return value;
            }

            public void setValue(float value) {
                this.value = value;
            }
        }
    }

}

