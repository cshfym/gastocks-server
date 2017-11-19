package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableIndustry
import org.springframework.data.repository.CrudRepository

interface IndustryRepository extends CrudRepository<PersistableIndustry, String> {

    PersistableIndustry findByCategoryAndSubCategory(String category, String subCategory)

}