package com.merchant.management.dao;


import com.merchant.management.beans.Merchant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MerchantRepository extends MongoRepository<Merchant, String> {
}
