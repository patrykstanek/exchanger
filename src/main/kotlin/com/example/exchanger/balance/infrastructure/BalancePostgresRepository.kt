package com.example.exchanger.balance.infrastructure

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BalancePostgresRepository : CrudRepository<BalanceEntity, Long> {

    @Query("SELECT e FROM BalanceEntity e WHERE e.account.id = :accountId")
    fun findByAccountId(@Param("accountId") accountId: Long): List<BalanceEntity>
}