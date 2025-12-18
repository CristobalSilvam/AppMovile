package com.example.appmovile.data.remote

import com.example.appmovile.data.remote.dto.AddMemberRequest
import com.example.appmovile.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.*

interface GroupApiService {
    
    @POST("api/groups/add-member")
    suspend fun addMemberByEmail(@Body request: AddMemberRequest): Response<Any>

    @GET("api/groups/my-team")
    suspend fun getGroupMembers(@Query("leaderId") leaderId: Long): List<UserDto>

    // NUEVO: Eliminar un miembro de un grupo (útil para cambiar de grupo)
    @DELETE("api/groups/remove-member")
    suspend fun removeMember(
        @Query("leaderId") leaderId: Long,
        @Query("memberId") memberId: Long
    ): Response<Unit>

    // NUEVO: Eliminar un grupo completo (el backend debería manejar la lógica)
    @DELETE("api/groups/{leaderId}")
    suspend fun deleteGroup(@Path("leaderId") leaderId: Long): Response<Unit>
}
