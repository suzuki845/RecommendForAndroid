package com.pin.recommend.model

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.translation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.ObjectUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BackupExportModel(
    private val db: AppDatabase
) {

    val onCreate = MutableLiveData(false)

    private fun serializableEntity(): AccountExportable? {
        val account = db.accountDao().findById(Account.ACCOUNT_ID)
        val characters = db.recommendCharacterDao().findAll().map { character ->
            val stories = db.storyDao().findByCharacterId(character.id).map { story ->
                val pictures = db.storyPictureDao().findByStoryId(story.id).map {
                    StoryPictureExportable(it)
                }
                StoryExportable(story).apply { this.pictures = pictures }
            }

            val payments = db.paymentDao().findByCharacterId(character.id).map { payment ->
                val tag = payment.paymentTagId?.let {
                    db.paymentTagDao().findById(it)?.let { it1 -> PaymentTagExportable(it1) }
                }
                PaymentExportable(payment).apply { this.tag = tag }
            }

            val events = db.eventDao().findByCharacterId(character.id).map { EventExportable(it) }

            val anniversaries = db.customAnniversaryDao().findByCharacterId(character.id)
                .map { CustomAnniversaryExportable(it) }

            RecommendCharacterExportable(character).apply {
                this.stories = stories
                this.payments = payments
                this.events = events
                this.anniversaries = anniversaries
            }
        }
        return AccountExportable(account).apply { this.characters = characters }
    }

    suspend fun export(context: Context, external: DocumentFile) =
        suspendCoroutine { continuation ->
            val account = serializableEntity()
            val jsonString = kotlin.runCatching {
                GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss Z")
                    .create()
                    .toJson(account)
            }.fold(
                onSuccess = { it },
                onFailure = { return@suspendCoroutine continuation.resumeWithException(Exception("serialize error")) }
            )
            val backupDirName = "oshi_backup${SimpleDateFormat("yyyy_MM_dd").format(Date())}"
            val backupDir = external.createDirectory(backupDirName)!!
            val dataFile = backupDir.createFile("application/json", "data.json")
            val resolver = context.contentResolver;

            try {
                resolver.openOutputStream(dataFile!!.uri).use { outputStream ->
                    outputStream?.write(jsonString.toByteArray())
                }
            } catch (e: Exception) {
                return@suspendCoroutine continuation.resumeWithException(Exception("json output error"))
            }

            account?.characters?.forEach { character ->
                character.iconImageSrc?.let { uri ->
                    backupDir.createFile("image/*", uri)?.uri?.let { exportUri ->
                        resolver.openOutputStream(exportUri).use { output ->
                            context.openFileInput(uri).use { input ->
                                val buffer = ByteArray(1024)
                                var read: Int
                                while (input.read(buffer).also { read = it } !== -1) {
                                    output?.write(buffer, 0, read)
                                }
                            }
                        }
                    }
                }
                character.backgroundImageSrc?.let { uri ->
                    backupDir.createFile("image/*", uri)?.uri?.let { exportUri ->
                        resolver.openOutputStream(exportUri).use { output ->
                            context.openFileInput(uri).use { input ->
                                val buffer = ByteArray(1024)
                                var read: Int
                                while (input.read(buffer).also { read = it } !== -1) {
                                    output?.write(buffer, 0, read)
                                }
                            }
                        }
                    }
                }
                character.stories?.forEach { story ->
                    story.pictures?.forEach { picture ->
                        picture.src?.let { uri ->
                            backupDir.createFile("image/*", uri)?.uri?.let { exportUri ->
                                resolver.openOutputStream(exportUri).use { output ->
                                    context.openFileInput(uri).use { input ->
                                        val buffer = ByteArray(1024)
                                        var read: Int
                                        while (input.read(buffer).also { read = it } !== -1) {
                                            output?.write(buffer, 0, read)
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }

            return@suspendCoroutine continuation.resume("")
        }

}


class BackupImportModel(
    private val db: AppDatabase
) {

    suspend fun import(context: Context, external: DocumentFile) =
        suspendCoroutine { continuation ->
            if (!external.isDirectory) {
                return@suspendCoroutine continuation.resumeWithException(Exception("is not directory"))
            }
            val data = external.listFiles().firstOrNull { it.name == "data.json" }
                ?: return@suspendCoroutine continuation.resumeWithException(Exception("missing backup data file"))
            external.listFiles().dropWhile { it.name == "data.json" }
            val resolver = context.contentResolver;
            val dataString = resolver.openInputStream(data.uri).use { input ->
                input?.bufferedReader().use { it?.readText() }  // defaults to UTF-8
            }

            val accountExportable = kotlin.runCatching {
                GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss Z")
                    .create()
                    .fromJson(dataString, AccountExportable::class.java)
            }.fold(
                onSuccess = { it },
                onFailure = { return@suspendCoroutine continuation.resumeWithException(Exception("deserialize error")) }
            )

            try {
                db.runInTransaction {
                    db.accountDao().deleteAll()
                    db.recommendCharacterDao().deleteAll()
                    db.eventDao().deleteAll()
                    db.storyDao().deleteAll()
                    db.paymentDao().deleteAll()
                    db.storyPictureDao().deleteAll()
                    db.paymentTagDao().deleteAll()
                    db.customAnniversaryDao().deleteAll()

                    val account = accountExportable.importable()
                    db.accountDao().insertAccount(account)

                    account.characters.forEach { character ->
                        character.accountId = account.id
                        character.id = db.recommendCharacterDao().insertCharacter(character)

                        character.stories?.forEach { story ->
                            story.characterId = character.id
                            story.id = db.storyDao().insertStory(story)
                            story.pictures?.forEach { picture ->
                                picture.storyId = story.id
                                picture.id = db.storyPictureDao().insertStoryPicture(picture)
                            }
                        }

                        character.payments?.forEach { payment ->
                            payment.characterId = character.id
                            payment.paymentTag?.let { tag ->
                                tag.id = db.paymentTagDao().insertPaymentTag(tag)
                                payment.paymentTagId = tag.id
                            }
                            payment.id = db.paymentDao().insertPayment(payment)
                        }

                        character.events?.forEach { event ->
                            event.characterId = character.id
                            event.id = db.eventDao().insertEvent(event)
                        }

                        character.anniversaries?.forEach { a ->
                            a.characterId = character.id
                            a.id = db.customAnniversaryDao().insertAnniversary(a)
                        }
                    }
                }
            } catch (e: Exception) {
                return@suspendCoroutine continuation.resumeWithException(Exception("database error"))
            }

            val privateDir = context.filesDir
            privateDir.delete()

            external.listFiles().forEach {
                if (it.isFile) {
                    val outputStream = context.openFileOutput(it.name, Context.MODE_PRIVATE)
                    val inputStream = context.contentResolver.openInputStream(it.uri)
                    val buffer = ByteArray(1024)
                    var read = 0
                    inputStream?.let { input ->
                        while (input.read(buffer).also { read = it } !== -1) {
                            outputStream?.write(buffer, 0, read)
                        }
                        input.close()
                        read = 0
                    }
                    outputStream?.close()
                }
            }

            continuation.resume("")
        }

    private fun debug(account: AccountExportable) {

        account.characters.forEach { character ->
            println("exportable-----" + character.name)

            character.stories?.forEach { story ->
                println("-- " + story.comment)
                story.pictures?.forEach { picture ->
                    println("---- " + picture.src)
                }
            }

            character.payments?.forEach { payment ->
                println("-- " + payment.amount)
            }

            character.events?.forEach { event ->
                println("-- " + event.title)
            }

            character.anniversaries?.forEach { a ->
                println("-- " + a.name)
            }
        }

    }

}