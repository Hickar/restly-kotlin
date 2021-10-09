package com.hickar.restly.repository.dao

import androidx.room.Dao
import com.hickar.restly.repository.models.RequestDTO

@Dao
abstract class RequestDao : BaseDao<RequestDTO>("requests")