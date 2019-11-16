package com.jamesgbennett.whoknowsltesignal.data;

public class LocationData {
    public Integer SIGNALSTRENGTHID;
    public String LTESIGNAL;
    public String CDMASignal;
    public String GSMSignal;
    public String TIME;
    public String LATITUDE;
    public String LONGITUDE;
    public String ALTITUDE;
    public String RSRP;
    public String RSRQ;
    public String DBM;

    public LocationData(Integer signalStrengthId, String lTESIGNAL, String cDMASignal,
                        String gSMSignal, String time, String latitude,
                        String longitude, String altitude, String rsrp, String rsrq, String dBM){
        this.SIGNALSTRENGTHID = signalStrengthId;
        this.LTESIGNAL = lTESIGNAL;
        this.CDMASignal = cDMASignal;
        this.GSMSignal = gSMSignal;
        this.TIME = time;
        this.LATITUDE = latitude;
        this.LONGITUDE = longitude;
        this.ALTITUDE = altitude;
        this.RSRP = rsrp;
        this.RSRQ = rsrq;
        this.DBM = dBM;

    }
}
