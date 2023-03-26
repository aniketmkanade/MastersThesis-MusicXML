package com.project;

import java.util.ArrayList;

public class UpdatedMeasure {
    private UpdatedHarmony harmony;
    private ArrayList<UpdatedNote> noteList;
    
    public UpdatedMeasure() { }
    
    public UpdatedMeasure(UpdatedHarmony harmony, ArrayList<UpdatedNote> noteList){
        this.harmony = harmony;
        this.noteList = new ArrayList<UpdatedNote>();
    }
    
    public UpdatedHarmony getHarmony() {
        return harmony;
    }
    
    public void setHarmony(UpdatedHarmony harmony) {
        this.harmony = harmony;
    }
    
    public ArrayList<UpdatedNote> getNoteList() {
        return noteList;
    }
    
    public void setNoteList(ArrayList<UpdatedNote> noteList) {
        this.noteList = noteList;
    }
}
