package com.pin.recommend.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.pin.recommend.model.dao.AccountDao;
import com.pin.recommend.model.dao.EventDao;
import com.pin.recommend.model.dao.PaymentDao;
import com.pin.recommend.model.dao.PaymentTagDao;
import com.pin.recommend.model.dao.RecommendCharacterDao;
import com.pin.recommend.model.dao.StoryDao;
import com.pin.recommend.model.dao.StoryPictureDao;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.Event;
import com.pin.recommend.model.entity.Payment;
import com.pin.recommend.model.entity.PaymentTag;
import com.pin.recommend.model.entity.RecommendCharacter;
import com.pin.recommend.model.entity.Story;
import com.pin.recommend.model.entity.StoryPicture;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Account.class, RecommendCharacter.class, Story.class, StoryPicture.class, Payment.class, PaymentTag.class, Event.class }, version = 6, exportSchema = true)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract AccountDao accountDao();

    public abstract RecommendCharacterDao recommendCharacterDao();

    public abstract StoryDao storyDao();

    public abstract StoryPictureDao storyPictureDao();

    public abstract PaymentDao paymentDao();

    public abstract PaymentTagDao paymentTagDao();

    public abstract EventDao eventDao();

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
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .addMigrations(MIGRATION_3_4)
                            .addMigrations(MIGRATION_4_5)
                            .addMigrations(MIGRATION_5_6)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN isZeroDayStart INTEGER DEFAULT 0 NOT NULL");
            database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN aboveText TEXT");
            database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN belowText TEXT");
            database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN elapsedDateFormat INTEGER DEFAULT 0 NOT NULL");
            database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN fontFamily TEXT");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN storySortOrder INTEGER DEFAULT 0 NOT NULL");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN backgroundImageOpacity REAL DEFAULT 1 NOT NULL");
            database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN homeTextShadowColor INTEGER");
        }
    };


    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE Payment (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            " characterId INTEGER NOT NULL,"+
                            " paymentTagId INTEGER,"+
                            " amount REAL DEFAULT 0.0 NOT NULL," +
                            " memo TEXT," +
                            " type INTEGER DEFAULT 0 NOT NULL," +
                            " createdAt INTEGER NOT NULL," +
                            " updatedAt INTEGER NOT NULL," +
                            " FOREIGN KEY(`characterId`) REFERENCES `RecommendCharacter`(`id`) ON UPDATE CASCADE ON DELETE CASCADE" +
                            ");");
            database.execSQL(
                    "CREATE TABLE PaymentTag (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            " tagName TEXT NOT NULL," +
                            " type INTEGER DEFAULT 0 NOT NULL," +
                            " createdAt INTEGER NOT NULL," +
                            " updatedAt INTEGER NOT NULL" +
                            ");");
        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE Event (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            " characterId INTEGER NOT NULL," +
                            " title TEXT," +
                            " memo TEXT," +
                            " date INTEGER NOT NULL," +
                            " FOREIGN KEY(`characterId`) REFERENCES `RecommendCharacter`(`id`) ON UPDATE CASCADE ON DELETE CASCADE" +
                            ");");
        }
    };


}

