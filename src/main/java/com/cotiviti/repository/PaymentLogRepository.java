package com.cotiviti.repository;

import com.cotiviti.entities.PaymentLog;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentLogRepository extends AbstractRepository<PaymentLog, Long> {

}
