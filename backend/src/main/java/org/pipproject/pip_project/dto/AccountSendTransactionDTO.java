package org.pipproject.pip_project.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pipproject.pip_project.model.Currency;


//pentru accesarea conturilor unui utilizator de catre alt utilizator doar in scopul de a efectua tranzactii de SEND
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountSendTransactionDTO {
    private Long id;
    private Currency currency;
}
