package com.flavor.recipes.dash.repositories

import com.flavor.recipes.dash.entities.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleRepository : JpaSpecificationExecutor<RoleEntity>, JpaRepository<RoleEntity, String> {
    fun findByUserId(userId: String): Optional<RoleEntity>
}