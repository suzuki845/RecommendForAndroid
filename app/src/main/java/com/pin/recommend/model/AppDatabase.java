package com.pin.recommend.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.pin.recommend.model.dao.AccountDao;
import com.pin.recommend.model.dao.RecommendCharacterDao;
import com.pin.recommend.model.dao.StoryDao;
import com.pin.recommend.model.dao.StoryPictureDao;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.RecommendCharacter;
import com.pin.recommend.model.entity.Story;
import com.pin.recommend.model.entity.StoryPicture;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Account.class, RecommendCharacter.class, Story.class, StoryPicture.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract AccountDao accountDao();

    public abstract RecommendCharacterDao recommendCharacterDao();

    public abstract StoryDao storyDao();

    public abstract StoryPictureDao storyPictureDao();

    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService executor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static volatile AppDatabase INSTANCE;

    public static final String DATABASE_NAME = "recommend";

    public static AppDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}

