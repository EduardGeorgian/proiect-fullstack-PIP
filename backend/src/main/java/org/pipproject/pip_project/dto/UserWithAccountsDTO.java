package org.pipproject.pip_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.User;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWithAccountsDTO {
    private User user;
    private List<Account> accounts;
}
