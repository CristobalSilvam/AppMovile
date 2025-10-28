// data/local/TaskDao.kt
package com.example.appmovile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.appmovile.data.local.models.TaskEntity

@Dao
interface TaskDao {

    // Obtener todas las tareas, ordenadas por prioridad
    @Query("SELECT * FROM tasks ORDER BY priority DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    // Insertar/Actualizar una tarea.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    // Eliminar una tarea por ID
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)

    //detalle tarea
    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Int): TaskEntity?
}