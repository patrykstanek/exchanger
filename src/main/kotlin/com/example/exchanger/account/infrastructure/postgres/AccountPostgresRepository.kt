package com.example.exchanger.account.infrastructure.postgres

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
internal interface AccountPostgresRepository : CrudRepository<AccountEntity, Long>
