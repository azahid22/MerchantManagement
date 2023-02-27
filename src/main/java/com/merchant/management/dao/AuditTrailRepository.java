package com.merchant.management.dao;

import com.merchant.management.beans.AuditTrail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditTrailRepository extends MongoRepository<AuditTrail, String> {
}
