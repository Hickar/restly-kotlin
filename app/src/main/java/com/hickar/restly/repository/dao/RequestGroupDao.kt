package com.hickar.restly.repository.dao

//import androidx.room.Dao
//import androidx.room.Query
//import com.hickar.restly.repository.models.RequestDTO
//
//@Dao
//abstract class RequestGroupDao : BaseDao<RequestGroupDTO>("request_groups") {
//    @Query("SELECT * FROM request_groups WHERE id = :groupId")
//    abstract suspend fun getByCollectionId(groupId: String): List<RequestDTO>
//
//    @Query("DELETE FROM request_groups WHERE id = :groupId")
//    abstract suspend fun deleteByCollectionId(groupId: String)
//}