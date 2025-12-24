package com.felipeserver.site.glyph.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant

@Database(
    entities = [NoteEntity::class, TagEntity::class, NoteTagCrossRef::class],
    version = 2, // Incremented version
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null
        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration() // Use with caution!
                    .addCallback(NoteDatabaseCallback(CoroutineScope(SupervisorJob())))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class NoteDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        val noteDao = database.noteDao()

                        // Clear old data
                        noteDao.deleteAllNotes()


                        // Add sample tags
                        val tagId1 = noteDao.insertTag(TagEntity(name = "Welcome"))
                        val tagId2 = noteDao.insertTag(TagEntity(name = "Tutorial"))
                        val tagId3 = noteDao.insertTag(TagEntity(name = "Life"))
                        val tagId4 = noteDao.insertTag(TagEntity(name = "Work"))

                        // Add sample notes
                        val noteId1 = noteDao.insertNote(NoteEntity(title = "Welcome Note", content = "This is your first note!", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId1.toInt(), tagId1.toInt()))

                        val noteId2 = noteDao.insertNote(NoteEntity(title = "How to Use", content = "Tap the '+' button to create a new note.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId2.toInt(), tagId2.toInt()))

                        var noteId: Long
                        noteId = noteDao.insertNote(NoteEntity(title = "Reunião de Projeto", content = "Discutir o progresso e os próximos passos.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId4.toInt())) // Work

                        noteId = noteDao.insertNote(NoteEntity(title = "Lista de Compras", content = "Leite, pão, ovos, e frutas.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId3.toInt())) // Life

                        noteId = noteDao.insertNote(NoteEntity(title = "Ideias para o App", content = "Adicionar nova funcionalidade de compartilhamento.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId4.toInt())) // Work

                        noteId = noteDao.insertNote(NoteEntity(title = "Lembrete: Médico", content = "Consulta com Dr. Silva, dia 25, às 10h.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId3.toInt())) // Life

                        noteId = noteDao.insertNote(NoteEntity(title = "Estudar Kotlin Coroutines", content = "Revisar os conceitos de escopos e dispatchers.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId2.toInt())) // Tutorial

                        noteId = noteDao.insertNote(NoteEntity(title = "Aniversário da Maria", content = "Comprar presente e organizar a festa surpresa.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId3.toInt())) // Life

                        noteId = noteDao.insertNote(NoteEntity(title = "Relatório Semanal", content = "Preparar e enviar o relatório de atividades.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId4.toInt())) // Work

                        noteId = noteDao.insertNote(NoteEntity(title = "Filmes para assistir", content = "Duna 2, Pobres Criaturas, Anatomia de uma Queda.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId3.toInt())) // Life

                        noteId = noteDao.insertNote(NoteEntity(title = "Configurar novo ambiente", content = "Instalar Android Studio e configurar o emulador.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId2.toInt())) // Tutorial

                        noteId = noteDao.insertNote(NoteEntity(title = "Planejamento de Férias", content = "Pesquisar destinos e preços de passagens.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId3.toInt())) // Life

                        noteId = noteDao.insertNote(NoteEntity(title = "Bug #123", content = "Investigar crash na tela de login.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId4.toInt())) // Work

                        noteId = noteDao.insertNote(NoteEntity(title = "Receita de Bolo", content = "Farinha, ovos, açucar, chocolate.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId3.toInt())) // Life

                        noteId = noteDao.insertNote(NoteEntity(title = "Aprender Jetpack Compose", content = "Fazer o tutorial oficial do Google.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId2.toInt())) // Tutorial

                        noteId = noteDao.insertNote(NoteEntity(title = "Ligar para o cliente X", content = "Feedback sobre a última entrega.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId4.toInt())) // Work

                        noteId = noteDao.insertNote(NoteEntity(title = "Exercícios da Academia", content = "Série A: Peito e Tríceps.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId3.toInt())) // Life

                        noteId = noteDao.insertNote(NoteEntity(title = "Refatorar NoteDao", content = "Otimizar as queries de busca.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId4.toInt())) // Work

                        noteId = noteDao.insertNote(NoteEntity(title = "Como usar o Room", content = "Ver documentação de migrações.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId2.toInt())) // Tutorial

                        noteId = noteDao.insertNote(NoteEntity(title = "Presente Dia das Mães", content = "Verificar o que ela está precisando.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId3.toInt())) // Life

                        noteId = noteDao.insertNote(NoteEntity(title = "Revisar PR #45", content = "Pull request do novo layout.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId4.toInt())) // Work

                        noteId = noteDao.insertNote(NoteEntity(title = "Limpar a casa", content = "Fazer a faxina semanal.", timeStamp = Instant.now()))
                        noteDao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tagId3.toInt())) // Life
                    }
                }
            }
        }
    }
}
