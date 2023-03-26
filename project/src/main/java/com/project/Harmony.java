package com.project;

public class Harmony {
    private String rootStep;
    private String kind;
    public Harmony(){ }

    public Harmony(String rootStep, String kind){
        this.rootStep = rootStep;
        this.kind = kind;
    }
    public String getRootStep() {
        return rootStep;
    }
    public void setRootStep(String rootStep) {
        this.rootStep = rootStep;
    }
    public String getKind() {
        return kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }
}
