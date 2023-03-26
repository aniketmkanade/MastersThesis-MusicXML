package com.project;

import java.util.ArrayList;

public class Measure{
    private Harmony harmony;
    private ArrayList<Note> noteList;
    public Measure() { }
    public Measure(Harmony harmony, ArrayList<Note> noteList){
        this.harmony = harmony;
        this.noteList = new ArrayList<Note>();
    }
    public Harmony getHarmony() {
        return harmony;
    }
    public void setHarmony(Harmony harmony) {
        this.harmony = harmony;
    }
    public ArrayList<Note> getNoteList() {
        return noteList;
    }
    public void setNoteList(ArrayList<Note> noteList) {
        this.noteList = noteList;
    }
}