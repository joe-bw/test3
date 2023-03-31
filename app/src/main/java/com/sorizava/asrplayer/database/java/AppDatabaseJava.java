/*
 * Create by jhong on 2022. 8. 8.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.database.java;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Room 설정 추상 클래스 */
@Database(entities = {BookmarkJava.class}, version = 1, exportSchema = false)
public abstract class AppDatabaseJava extends RoomDatabase {

    public abstract BookmarkDaoJava bookmarkDao();
    public static final int NUMBER_OF_THREADS = 4;

    private static final String KEY_FILENAME = "initbookmarks.json";

    private static volatile AppDatabaseJava INSTANCE;
    public static final ExecutorService databaseWriteExecutor
            = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabaseJava getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabaseJava.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabaseJava.class, "earzoom-db")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);

                                    databaseWriteExecutor.execute( () -> {
                                        try {
                                            InputStream is = context.getApplicationContext().getAssets().open(KEY_FILENAME);
                                            BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                            String line;

                                            StringBuilder sb = new StringBuilder();
                                            while ((line = br.readLine()) != null){
                                                sb.append(line);
                                            }
                                            br.close();
                                            Type type = new TypeToken<List<BookmarkJava>>() {}.getType();
                                            List<BookmarkJava> list = new Gson().fromJson(sb.toString(), type);

                                            INSTANCE.runInTransaction( () -> INSTANCE.bookmarkDao().insertAll(list));

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
