package org.pipproject.pip_project.repositories;

import org.pipproject.pip_project.dto.AccountSendTransactionDTO;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
   List<Account> findByUser(Optional<User> user);
   @Query("SELECT new org.pipproject.pip_project.dto.AccountSendTransactionDTO(a.id, a.currency) " +
           "FROM Account a WHERE a.user = :user")
   List<AccountSendTransactionDTO> findAccountSendTransactionDTOByUser(Optional<User> user);
}
