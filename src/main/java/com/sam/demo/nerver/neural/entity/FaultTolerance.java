package com.sam.demo.nerver.neural.entity;

import java.io.Serializable;

/**
 * 容错配置信息
 * <p>支持参数配置和默认配置</p>
 * @lry
 */
public class FaultTolerance implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public FaultTolerance(){
    }
    public FaultTolerance(String ftKey){
        this.ftKey=ftKey;
    }
    /**********************************************************************************************/
    /*******************************************参数字段********************************************/
    /**********************************************************************************************/
    public static final String FTKEY_KEY = "ft_key";
    public static final String CBENABLED_KEY = "cb_enabled";
    public static final String CBREQVOLTHRESHOLD_KEY = "cb_req_vol_threshold";
    public static final String CBSLEEPWINDOW_KEY = "cb_sleep_window";
    public static final String CBERRORTHRESHOLD_KEY = "cb_error_threshold";
    public static final String CBFORCEOPEN_KEY = "cb_force_open";
    public static final String CBFORCECLOSED_KEY = "cb_force_closed";
    public static final String EXECTIMEOUT_KEY = "exec_timeout";
    public static final String EXECTIMEOUTENABLED_KEY = "exec_timeout_enabled";
    public static final String EXECISOSTRATEGY_KEY = "exec_iso_strategy";
    public static final String EXECISOTHREADINTETIMEOUT_KEY = "exec_iso_thread_inte_timeout";
    public static final String EXECISOTHREADTIMEOUT_KEY = "exec_iso_thread_timeout";
    public static final String EXECISOMAXCONCUREQ_KEY = "exec_iso_max_concu_req";
    public static final String MRSTATWINDOW_KEY = "mr_stat_window";
    public static final String MRSTATWINDOWBUCKETS_KEY = "mr_stat_window_buckets";
    public static final String MRPENABLED_KEY = "mrp_enabled";
    public static final String MRPWINDOW_KEY = "mrp_window";
    public static final String MRPWINDOWBUCKETS_KEY = "mrp_window_buckets";
    public static final String MRPBUCKETSIZE_KEY = "mrp_bucket_size";
    public static final String REQCACHEENABLED_KEY = "req_cache_enabled";
    public static final String REQLOGENABLED_KEY = "req_log_enabled";
    public static final String FALLBACKISOMAXCONCUREQ_KEY = "fallback_iso_max_concu_req";
    public static final String FALLBACKENABLED_KEY = "fallback_enabled";
    public static final String MHSNAPSHOTINTERVAL_KEY = "mh_snapshot_interval";

    /**********************************************************************************************/
    /*******************************************内部字段********************************************/
    /**********************************************************************************************/
    /**
     * 容错KEY值
     */
    private String ftKey="FTKEY";


    /**
     * 熔断开关
     */
    private boolean cbEnabled = true;
    /**
     * default => statisticalWindowVolumeThreshold: 20 requests in 10 seconds must occur before statistics matter
     */
    private int cbReqVolThreshold = 20;
    /**
     * default => sleepWindow: 5000 = 5 seconds that we will sleep before trying again after tripping the circuit
     */
    private int cbSleepWindow = 5000;
    /**
     * default => errorThresholdPercentage = 50 = if 50%+ of requests in 10 seconds are failures or latent then we will trip the circuit
     */
    private int cbErrorThreshold = 50;
    /**
     * default => forceCircuitOpen = false (we want to allow traffic)
     */
    private boolean cbForceOpen = false;
    /**
     * default => ignoreErrors = false
     */
    private boolean cbForceClosed = false;


    /**
     * default => executionTimeoutInMilliseconds: 60000 = 1 second
     */
    private int execTimeout = 60000;
    /**
     * 执行超时开关
     */
    private boolean execTimeoutEnabled = true;


    /**
     * 执行隔离策略:THREAD(线程), SEMAPHORE(信号量)
     */
    private String execIsoStrategy = "THREAD";
    /**
     * 执行隔离线程中断超时开关
     */
    private boolean execIsoThreadInteTimeout = true;
    /**
     * 隔离线程超时时间
     */
    private int execIsoThreadTimeout = 60000;
    /**
     * 执行隔离信号最大并发请求数
     */
    private int execIsoMaxConcuReq = 10;


    /**
     * default => statisticalWindow: 10000 = 10 seconds (and default of 10 buckets so each bucket is 1 second)
     */
    private int mrStatWindow = 10000;
    /**
     * default => statisticalWindowBuckets: 10 = 10 buckets in a 10 second window so each bucket is 1 second
     */
    private int mrStatWindowBuckets = 10;


    /**
     * 指标
     */
    private boolean mrpEnabled = true;
    /**
     * default to 1 minute for RollingPercentile
     */
    private int mrpWindow = 60000;
    /**
     * default to 6 buckets (10 seconds each in 60 second window)
     */
    private int mrpWindowBuckets = 6;
    /**
     * default to 100 values max per bucket
     */
    private int mrpBucketSize = 100;


    /**
     * 请求缓存开关
     */
    private boolean reqCacheEnabled = true;
    /**
     * 请求日志开关
     */
    private boolean reqLogEnabled = true;


    /**
     * 隔离信号最大并发请求数
     */
    private int fallbackIsoMaxConcuReq = 10;
    /**
     * 容错启用开关
     */
    private boolean fallbackEnabled = true;


    /**
     * default to 500ms as max frequency between allowing snapshots of health (error percentage etc)
     */
    private int mhSnapshotInterval = 500;


    public String getFtKey() {
        return ftKey;
    }

    public void setFtKey(String ftKey) {
        this.ftKey = ftKey;
    }

    public boolean isCbEnabled() {
        return cbEnabled;
    }

    public void setCbEnabled(boolean cbEnabled) {
        this.cbEnabled = cbEnabled;
    }

    public int getCbReqVolThreshold() {
        return cbReqVolThreshold;
    }

    public void setCbReqVolThreshold(int cbReqVolThreshold) {
        this.cbReqVolThreshold = cbReqVolThreshold;
    }

    public int getCbSleepWindow() {
        return cbSleepWindow;
    }

    public void setCbSleepWindow(int cbSleepWindow) {
        this.cbSleepWindow = cbSleepWindow;
    }

    public int getCbErrorThreshold() {
        return cbErrorThreshold;
    }

    public void setCbErrorThreshold(int cbErrorThreshold) {
        this.cbErrorThreshold = cbErrorThreshold;
    }

    public boolean isCbForceOpen() {
        return cbForceOpen;
    }

    public void setCbForceOpen(boolean cbForceOpen) {
        this.cbForceOpen = cbForceOpen;
    }

    public boolean isCbForceClosed() {
        return cbForceClosed;
    }

    public void setCbForceClosed(boolean cbForceClosed) {
        this.cbForceClosed = cbForceClosed;
    }

    public int getExecTimeout() {
        return execTimeout;
    }

    public void setExecTimeout(int execTimeout) {
        this.execTimeout = execTimeout;
    }

    public boolean isExecTimeoutEnabled() {
        return execTimeoutEnabled;
    }

    public void setExecTimeoutEnabled(boolean execTimeoutEnabled) {
        this.execTimeoutEnabled = execTimeoutEnabled;
    }

    public String getExecIsoStrategy() {
        return execIsoStrategy;
    }

    public void setExecIsoStrategy(String execIsoStrategy) {
        this.execIsoStrategy = execIsoStrategy;
    }

    public boolean isExecIsoThreadInteTimeout() {
        return execIsoThreadInteTimeout;
    }

    public void setExecIsoThreadInteTimeout(boolean execIsoThreadInteTimeout) {
        this.execIsoThreadInteTimeout = execIsoThreadInteTimeout;
    }

    public int getExecIsoThreadTimeout() {
        return execIsoThreadTimeout;
    }

    public void setExecIsoThreadTimeout(int execIsoThreadTimeout) {
        this.execIsoThreadTimeout = execIsoThreadTimeout;
    }

    public int getExecIsoMaxConcuReq() {
        return execIsoMaxConcuReq;
    }

    public void setExecIsoMaxConcuReq(int execIsoMaxConcuReq) {
        this.execIsoMaxConcuReq = execIsoMaxConcuReq;
    }

    public int getMrStatWindow() {
        return mrStatWindow;
    }

    public void setMrStatWindow(int mrStatWindow) {
        this.mrStatWindow = mrStatWindow;
    }

    public int getMrStatWindowBuckets() {
        return mrStatWindowBuckets;
    }

    public void setMrStatWindowBuckets(int mrStatWindowBuckets) {
        this.mrStatWindowBuckets = mrStatWindowBuckets;
    }

    public boolean isMrpEnabled() {
        return mrpEnabled;
    }

    public void setMrpEnabled(boolean mrpEnabled) {
        this.mrpEnabled = mrpEnabled;
    }

    public int getMrpWindow() {
        return mrpWindow;
    }

    public void setMrpWindow(int mrpWindow) {
        this.mrpWindow = mrpWindow;
    }

    public int getMrpWindowBuckets() {
        return mrpWindowBuckets;
    }

    public void setMrpWindowBuckets(int mrpWindowBuckets) {
        this.mrpWindowBuckets = mrpWindowBuckets;
    }

    public int getMrpBucketSize() {
        return mrpBucketSize;
    }

    public void setMrpBucketSize(int mrpBucketSize) {
        this.mrpBucketSize = mrpBucketSize;
    }

    public boolean isReqCacheEnabled() {
        return reqCacheEnabled;
    }

    public void setReqCacheEnabled(boolean reqCacheEnabled) {
        this.reqCacheEnabled = reqCacheEnabled;
    }

    public boolean isReqLogEnabled() {
        return reqLogEnabled;
    }

    public void setReqLogEnabled(boolean reqLogEnabled) {
        this.reqLogEnabled = reqLogEnabled;
    }

    public int getFallbackIsoMaxConcuReq() {
        return fallbackIsoMaxConcuReq;
    }

    public void setFallbackIsoMaxConcuReq(int fallbackIsoMaxConcuReq) {
        this.fallbackIsoMaxConcuReq = fallbackIsoMaxConcuReq;
    }

    public boolean isFallbackEnabled() {
        return fallbackEnabled;
    }

    public void setFallbackEnabled(boolean fallbackEnabled) {
        this.fallbackEnabled = fallbackEnabled;
    }

    public int getMhSnapshotInterval() {
        return mhSnapshotInterval;
    }

    public void setMhSnapshotInterval(int mhSnapshotInterval) {
        this.mhSnapshotInterval = mhSnapshotInterval;
    }

    @Override
    public String toString() {
        return "FaultTolerance{" +
                "ftKey='" + ftKey + '\'' +
                ", cbEnabled=" + cbEnabled +
                ", cbReqVolThreshold=" + cbReqVolThreshold +
                ", cbSleepWindow=" + cbSleepWindow +
                ", cbErrorThreshold=" + cbErrorThreshold +
                ", cbForceOpen=" + cbForceOpen +
                ", cbForceClosed=" + cbForceClosed +
                ", execTimeout=" + execTimeout +
                ", execTimeoutEnabled=" + execTimeoutEnabled +
                ", execIsoStrategy='" + execIsoStrategy + '\'' +
                ", execIsoThreadInteTimeout=" + execIsoThreadInteTimeout +
                ", execIsoThreadTimeout=" + execIsoThreadTimeout +
                ", execIsoMaxConcuReq=" + execIsoMaxConcuReq +
                ", mrStatWindow=" + mrStatWindow +
                ", mrStatWindowBuckets=" + mrStatWindowBuckets +
                ", mrpEnabled=" + mrpEnabled +
                ", mrpWindow=" + mrpWindow +
                ", mrpWindowBuckets=" + mrpWindowBuckets +
                ", mrpBucketSize=" + mrpBucketSize +
                ", reqCacheEnabled=" + reqCacheEnabled +
                ", reqLogEnabled=" + reqLogEnabled +
                ", fallbackIsoMaxConcuReq=" + fallbackIsoMaxConcuReq +
                ", fallbackEnabled=" + fallbackEnabled +
                ", mhSnapshotInterval=" + mhSnapshotInterval +
                '}';
    }

}