package com.pin.recommend.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pin.recommend.model.dao.AccountDao
import com.pin.recommend.model.dao.BadgeDao
import com.pin.recommend.model.dao.BadgeSummaryDao
import com.pin.recommend.model.dao.CharacterDeleteLogic
import com.pin.recommend.model.dao.CustomAnniversaryDao
import com.pin.recommend.model.dao.EventDao
import com.pin.recommend.model.dao.PaymentDao
import com.pin.recommend.model.dao.PaymentTagDao
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.dao.StoryDao
import com.pin.recommend.model.dao.StoryPictureDao
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.Badge
import com.pin.recommend.model.entity.BadgeSummary
import com.pin.recommend.model.entity.CustomAnniversary
import com.pin.recommend.model.entity.Event
import com.pin.recommend.model.entity.Payment
import com.pin.recommend.model.entity.PaymentTag
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.entity.Story
import com.pin.recommend.model.entity.StoryPicture
import java.util.concurrent.Executors

@Database(
    entities = [Account::class, RecommendCharacter::class, Story::class, StoryPicture::class, Payment::class, PaymentTag::class, Event::class, CustomAnniversary::class, Badge::class, BadgeSummary::class],
    version = 9,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun recommendCharacterDao(): RecommendCharacterDao
    abstract fun storyDao(): StoryDao
    abstract fun storyPictureDao(): StoryPictureDao
    abstract fun paymentDao(): PaymentDao
    abstract fun paymentTagDao(): PaymentTagDao
    abstract fun eventDao(): EventDao
    abstract fun customAnniversaryDao(): CustomAnniversaryDao
    abstract fun badgeDao(): BadgeDao
    abstract fun badgeSummary(): BadgeSummaryDao

    fun characterDeleteLogic(): CharacterDeleteLogic {
        return CharacterDeleteLogic(
            recommendCharacterDao(),
            storyDao(),
            storyPictureDao()
        )
    }

    companion object {
        private const val NUMBER_OF_THREADS = 4
        val executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        @Volatile
        private var INSTANCE: AppDatabase? = null
        const val DATABASE_NAME = "recommend"
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_5_6)
                    .addMigrations(MIGRATION_6_7)
                    .addMigrations(MIGRATION_7_8)
                    .addMigrations(MIGRATION_8_9)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        @JvmField
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN isZeroDayStart INTEGER DEFAULT 0 NOT NULL")
                database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN aboveText TEXT")
                database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN belowText TEXT")
                database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN elapsedDateFormat INTEGER DEFAULT 0 NOT NULL")
                database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN fontFamily TEXT")
            }
        }

        @JvmField
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN storySortOrder INTEGER DEFAULT 0 NOT NULL")
            }
        }

        @JvmField
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN backgroundImageOpacity REAL DEFAULT 1 NOT NULL")
                database.execSQL("ALTER TABLE RecommendCharacter ADD COLUMN homeTextShadowColor INTEGER")
            }
        }

        @JvmField
        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE Payment (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            " characterId INTEGER NOT NULL," +
                            " paymentTagId INTEGER," +
                            " amount REAL DEFAULT 0.0 NOT NULL," +
                            " memo TEXT," +
                            " type INTEGER DEFAULT 0 NOT NULL," +
                            " createdAt INTEGER NOT NULL," +
                            " updatedAt INTEGER NOT NULL," +
                            " FOREIGN KEY(`characterId`) REFERENCES `RecommendCharacter`(`id`) ON UPDATE CASCADE ON DELETE CASCADE" +
                            ");"
                )
                database.execSQL(
                    "CREATE TABLE PaymentTag (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            " tagName TEXT NOT NULL," +
                            " type INTEGER DEFAULT 0 NOT NULL," +
                            " createdAt INTEGER NOT NULL," +
                            " updatedAt INTEGER NOT NULL" +
                            ");"
                )
            }
        }

        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE Event (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            " characterId INTEGER NOT NULL," +
                            " title TEXT," +
                            " memo TEXT," +
                            " date INTEGER NOT NULL," +
                            " FOREIGN KEY(`characterId`) REFERENCES `RecommendCharacter`(`id`) ON UPDATE CASCADE ON DELETE CASCADE" +
                            ");"
                )
            }
        }

        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE INDEX paymentCharacterId ON Payment(characterId)"
                )
                database.execSQL(
                    "CREATE INDEX eventCharacterId ON Event(characterId)"
                )
            }
        }

        val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE CustomAnniversary (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            " characterId INTEGER NOT NULL," +
                            " uuid TEXT NOT NULL," +
                            " name TEXT NOT NULL," +
                            " date INTEGER NOT NULL," +
                            " topText TEXT," +
                            " bottomText TEXT," +
                            " FOREIGN KEY(`characterId`) REFERENCES `RecommendCharacter`(`id`) ON UPDATE CASCADE ON DELETE CASCADE" +
                            ")"
                )
                database.execSQL(
                    "CREATE INDEX customAnniversaryCharacterId ON CustomAnniversary(characterId)"
                )
            }
        }

        val MIGRATION_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE Badge (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            " characterId INTEGER NOT NULL," +
                            " uuid TEXT NOT NULL," +
                            " createdAt INTEGER NOT NULL," +
                            " updatedAt INTEGER NOT NULL," +
                            " FOREIGN KEY(`characterId`) REFERENCES `RecommendCharacter`(`id`) ON UPDATE CASCADE ON DELETE CASCADE" +
                            ")"
                )
                database.execSQL(
                    "CREATE INDEX badgeCharacterId ON Badge(characterId)"
                )
                database.execSQL(
                    "CREATE TABLE BadgeSummary (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            " characterId INTEGER NOT NULL," +
                            " uuid TEXT NOT NULL," +
                            " amount INTEGER DEFAULT 0 NOT NULL," +
                            " createdAt INTEGER NOT NULL," +
                            " updatedAt INTEGER NOT NULL," +
                            " FOREIGN KEY(`characterId`) REFERENCES `RecommendCharacter`(`id`) ON UPDATE CASCADE ON DELETE CASCADE" +
                            ")"
                )
                database.execSQL(
                    "CREATE INDEX badgeSummaryCharacterId ON BadgeSummary(characterId)"
                )
            }
        }
    }
}