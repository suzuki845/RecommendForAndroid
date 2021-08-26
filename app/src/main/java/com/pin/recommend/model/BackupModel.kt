package com.pin.recommend.model

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pin.recommend.model.dao.*
import com.pin.recommend.model.entity.Account
import kotlinx.android.synthetic.main.col_horizontal_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class BackupExportModel(
        private val db: AppDatabase
){

    val onCreate = MutableLiveData(false)

    private suspend fun serializableEntity(): Account? {
        return withContext(Dispatchers.Default) {
            val account = db.accountDao().findById(Account.ACCOUNT_ID.toLong())
            val paymentTags = db.paymentTagDao().findAll()
            account.paymentTags = paymentTags
            val characters = db.recommendCharacterDao().findAll()
            characters.forEach{ character ->
                val stories = db.storyDao().findByCharacterId(character.id)
                stories.forEach{ story ->
                    story.pictures = db.storyPictureDao().findByStoryId(story.id)
                }
                character.stories = stories

                val payments = db.paymentDao().findByCharacterId(character.id)
                payments.forEach{ payment ->
                    payment.paymentTag = payment.paymentTagId?.let { db.paymentTagDao().findById(it) }
                }
                character.payments = payments

                val events = db.eventDao().findByCharacterId(character.id)
                character.events = events
            }
            account.characters = characters
            account
        }
    }

    suspend fun export(context: Context, external: DocumentFile){
        val account = serializableEntity()
        val jsonString = Gson().toJson(account)

        val backupDirName = "oshi_backup${SimpleDateFormat("yyyy_MM_dd").format(Date())}"
        val backupDir = external.createDirectory(backupDirName)!!
        val dataFile = backupDir.createFile("application/json", "data.json")
        val resolver = context.contentResolver;

        try {
            resolver.openOutputStream(dataFile!!.uri).use { outputStream ->
                outputStream?.write(jsonString.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        account?.characters?.forEach{ character ->
            character.iconImageUri?.let{uri ->
                val iconFile = backupDir.createFile("image/*", character.iconImageUri)
                resolver.openOutputStream(iconFile!!.uri).use { outputStream ->
                    val input = context.openFileInput(uri)
                    val buffer = ByteArray(1024)
                    var read = 0
                    while (input.read(buffer).also { read = it } !== -1) {
                        outputStream?.write(buffer, 0, read)
                    }
                    input.close()
                    outputStream?.close()
                }
            }
            character.backgroundImageUri?.let {uri ->
                val backgroundFile = backupDir.createFile("image/*", character.backgroundImageUri)
                resolver.openOutputStream(backgroundFile!!.uri).use { outputStream ->
                    val input = context.openFileInput(uri)
                    val buffer = ByteArray(1024)
                    var read = 0
                    while (input.read(buffer).also { read = it } !== -1) {
                        outputStream?.write(buffer, 0, read)
                    }
                    input.close()
                    outputStream?.close()
                }
            }
            character.stories?.forEach{story ->
                story.pictures?.forEach{picture ->
                    picture.uri?.let {uri ->
                        val imageFile = backupDir.createFile("image/*", picture.uri)
                        resolver.openOutputStream(imageFile!!.uri).use { outputStream ->
                            val input = context.openFileInput(uri)
                            val buffer = ByteArray(1024)
                            var read = 0
                            while (input.read(buffer).also { read = it } !== -1) {
                                outputStream?.write(buffer, 0, read)
                            }
                            input.close()
                            outputStream?.close()
                        }
                    }
                }
            }

        }

    }

}


class BackupImportModel(
        private val db: AppDatabase
) {

    fun import(context: Context, external: DocumentFile) {
        if (!external.isDirectory) {
            throw java.lang.RuntimeException("is not directory")
        }
        val data = external.listFiles().firstOrNull { it.name == "data.json" }
                ?: throw java.lang.RuntimeException("missing backup data file")
        val resolver = context.contentResolver;
        val dataString = resolver.openInputStream(data.uri!!).use { input ->
            input?.bufferedReader().use { it?.readText() }  // defaults to UTF-8
        }

        val account = Gson().fromJson(dataString, Account::class.java)

        debug(account)
    }

    private fun debug(account: Account){
        account.paymentTags?.forEach{tag ->
            println("-- peymentTags"+ tag.id)
            println("-- tagName"+ tag.tagName)
            println("-- type"+ tag.type)
            println("-- createdAt"+ tag.createdAt)
            println("-- updatedAt"+ tag.updatedAt)
        }

        account.characters.forEach{character ->
            println("---- character" + character.id)
            println("---- name" + character.name)

            character.stories?.forEach { story ->
                println("-- story"+ story.id)
                println("-- "+ story.comment)
                story.pictures?.forEach { picture ->
                    println("---- picture" + picture.id)
                    println("---- " + picture.uri)
                }
            }

            character.payments?.forEach{payment ->
                println("-- peyment"+ payment.id)
                println("-- "+ payment.amount)
            }

            character.events?.forEach{event ->
                println("-- events"+ event.id)
                println("-- "+ event.title)
            }
        }

    }

}