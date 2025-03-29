package org.pipproject.pip_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pipproject.pip_project.model.Currency;
import org.pipproject.pip_project.model.User;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateDTO {
    private Currency currency;
    private User user;
}
