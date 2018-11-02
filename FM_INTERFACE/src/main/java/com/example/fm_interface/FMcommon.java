package com.example.fm_interface;

public interface FMcommon {

    public boolean openDev();
    public boolean closeDev();
    public boolean powerUp(float frequency);
    public boolean powerDown(int type);
    public boolean tune(float frequency);
    public float seek(float frequency, boolean isUp);
    public short[] autoScan(int startFreq);
    public boolean stopScan();
    public int setRds(boolean rdson);
    public short readRds();
    public byte[] getPs();
    public byte[] getLrText();
    public short activeAf();
    public int setMute(boolean mute);
    public int isRdsSupport();
    public int switchAntenna(int antenna);




}
