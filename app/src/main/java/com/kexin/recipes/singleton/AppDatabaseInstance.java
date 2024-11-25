package com.kexin.recipes.singleton;
import android.content.Context;

import androidx.room.Room;

import com.kexin.recipes.db.AppDatabase;

public class AppDatabaseInstance {
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "recipes").build();
        }
        return instance;
    }
}
