package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableIndustry
import com.gastocks.server.repositories.IndustryRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class IndustryPersistenceService {

    @Autowired
    IndustryRepository industryRepository

    PersistableIndustry findByCategoryAndSubCategory(String category, String subCategory) {
        industryRepository.findByCategoryAndSubCategory(category, subCategory)
    }

    @Transactional
    PersistableIndustry createPersistableIndustry(String category, String subCategory) {
        industryRepository.save(new PersistableIndustry(category: category, subCategory: subCategory))
    }

}
