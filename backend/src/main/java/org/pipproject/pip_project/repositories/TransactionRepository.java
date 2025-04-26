package org.pipproject.pip_project.repositories;

import org.pipproject.pip_project.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccountId(Long id);
    List<Transaction> findByInitiatorEmailOrderByDateDesc(String email);
}
