package com.hickar.restly.repository.dao

import android.database.sqlite.SQLiteException
import androidx.room.*
import com.hickar.restly.repository.models.RequestDTO

@Dao
abstract class RequestDao : BaseDao<RequestDTO>("requests")