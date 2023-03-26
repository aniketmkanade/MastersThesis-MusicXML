package com.project;

public class Note {
    private String step;
    private String duration;
    private String type;
    private String beam;
    public Note(){};

    public Note(String step, String duration, String type, String beam) {
        this.step = step;
        this.duration = duration;
        this.type = type;
        this.beam = beam;
    }
    public String getStep() {
        return step;
    }
    public void setStep(String step) {
        this.step = step;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getBeam() {
        return beam;
    }
    public void setBeam(String beam) {
        this.beam = beam;
    }
}
