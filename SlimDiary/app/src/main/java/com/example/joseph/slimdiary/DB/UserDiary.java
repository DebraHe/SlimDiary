package com.example.joseph.slimdiary.DB;

import cn.bmob.v3.BmobObject;

/**
 * Created by Joseph on 2017/3/18.
 */

public class UserDiary extends BmobObject {
    User username;
    String diaryLunch;
    String diaryExercise;
    String diarySelfEvaluation;
    String diaryPrivate;


    public String getDiaryLunch() {
        return diaryLunch;
    }

    public String getDiaryPrivate() {
        return diaryPrivate;
    }

    public String getDiarySelfEvaluation() {
        return diarySelfEvaluation;
    }

    public User getUsername() {
        return username;
    }

    public String getDiaryExercise() {
        return diaryExercise;
    }


    public void setDiaryLunch(String diaryLunch) {
        this.diaryLunch = diaryLunch;
    }

    public void setDiaryPrivate(String diaryPrivate) {
        this.diaryPrivate = diaryPrivate;
    }

    public void setDiarySelfEvaluation(String diarySelfEvaluation) {
        this.diarySelfEvaluation = diarySelfEvaluation;
    }

    public void setUsername(User username) {
        this.username = username;
    }

    public void setDiaryExercise(String diaryExercise) {
        this.diaryExercise = diaryExercise;
    }
}
