package com.felipeserver.site.glyph.data.local

import android.content.Context
import androidx.activity.addCallback
import androidx.activity.result.launch
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
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
                ).addCallback(NoteDatabaseCallback(CoroutineScope(SupervisorJob()))).build()
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

                        // Apague notas existentes se necessário (opcional)
                        noteDao.deleteAll() // Se você tiver esse método

                        // Crie e insira suas notas de teste
                        noteDao.insertNote(NoteEntity(title = "Nota de Boas-Vindas", content = "Esta é sua primeira nota!", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Como Usar", content = "Toque no botão '+' para criar uma nova nota.",timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Lembrete Importante", content = "Não esquecer a reunião às 10h amanhã.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Ideias de Projeto", content = "Discutir a nova interface de usuário com a equipe de design.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Lista de Compras", content = "Leite, pão, ovos, café e frutas.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Configuração do Servidor", content = "Verificar credenciais SSH para o novo servidor de produção.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Feedback do Cliente", content = "O cliente X gostou do recurso A, mas relatou um bug no recurso B.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Anotação de Aula", content = "Revisar conceitos de arquitetura MVVM para o próximo módulo.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Plano de Treino", content = "Segunda: Peito/Tríceps, Quarta: Costas/Bíceps, Sexta: Pernas/Ombro.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Contato Novo", content = "Nome: João Silva, Telefone: (11) 98765-4321, Email: joao@exemplo.com.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Receita de Bolo", content = "2 xícaras de farinha, 1 xícara de açúcar, 3 ovos, 1/2 xícara de leite, 1 colher de fermento.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Metas da Semana", content = "Finalizar a documentação técnica e enviar o relatório de progresso.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Livros para Ler", content = "Clean Code, The Pragmatic Programmer, Design Patterns.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Manutenção do Carro", content = "Trocar óleo e filtro de ar no próximo mês.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Lembrete de Aniversário", content = "Aniversário da Maria na próxima terça-feira. Comprar presente.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Código de Desconto", content = "Cupom de 20% para a loja online: DESC20OFF.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Progresso do App", content = "Módulo de autenticação concluído e testado com sucesso.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Ideia de Viagem", content = "Pesquisar pacotes de viagem para Porto de Galinhas em Março.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Reunião de Equipe", content = "Agendada para 15h na sala 3. Tópicos: Sprint Review.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Exercício Físico", content = "Fazer 30 minutos de cardio hoje após o trabalho.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Tarefa Doméstica", content = "Lavar a louça e estender a roupa antes de dormir.", timeStamp = Instant.now()))
                        noteDao.insertNote(NoteEntity(title = "Planejamento Financeiro", content = "Revisar o orçamento mensal e ajustar gastos com lazer.", timeStamp = Instant.now()))

                    }
                }
            }
        }


}}