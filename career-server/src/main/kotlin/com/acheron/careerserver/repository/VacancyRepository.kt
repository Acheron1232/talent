package com.acheron.careerserver.repository

import com.acheron.careerserver.entity.Vacation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID
@Repository
interface VacationRepository: JpaRepository<Vacation, UUID> {
}