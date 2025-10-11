package com.agendae.colmeia.domain.port.out;

import com.agendae.colmeia.domain.model.ExternalAccount;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExternalAccountRepositoryPort {

    /**
     * Busca uma conta externa pelo seu ‘ID’ único.
     *
     * @param id O UUID da conta externa.
     * @return um Optional contendo a ExternalAccount se encontrada, ou vazio caso contrário.
     */
    Optional<ExternalAccount> findById(UUID id);

    /**
     * Salva (cria ou atualiza) uma conta externa.
     *
     * @param externalAccount O objeto a ser salvo.
     * @return A instância da ExternalAccount salva, potencialmente com o ‘ID’ preenchido.
     */
    ExternalAccount save(ExternalAccount externalAccount);
    List<ExternalAccount> findAll();
}
