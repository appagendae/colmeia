package com.agendae.colmeia.infra.adapter.out.db;

import com.agendae.colmeia.domain.model.ExternalAccount;
import com.agendae.colmeia.domain.port.out.ExternalAccountRepositoryPort;
import com.agendae.colmeia.infra.entity.ExternalAccountEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Esta é a implementação REAL da nossa porta de saída.
 * Ele atua como um "adaptador", traduzindo entre os modelos de domínio
 * (puros) e as entidades de banco de dados (JPA).
 */
@Component
// A anotação @RequiredArgsConstructor foi removida.
public class ExternalAccountRepositoryAdapter implements ExternalAccountRepositoryPort {

    private final SpringDataExternalAccountRepository jpaRepository;

    /**
     * Construtor explícito para injeção de dependência.
     * O Spring Boot injetará a implementação do SpringDataExternalAccountRepository aqui.
     */
    public ExternalAccountRepositoryAdapter(SpringDataExternalAccountRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ExternalAccount> findById(UUID id) {
        // Usa o repositório JPA para buscar a entidade do banco
        Optional<ExternalAccountEntity> entityOptional = jpaRepository.findById(id);
        // Mapeia a entidade JPA de volta para o modelo de domínio antes de retornar
        return entityOptional.map(this::toDomainModel);
    }

    @Override
    public ExternalAccount save(ExternalAccount externalAccount) {
        // Mapeia o modelo de domínio para uma entidade JPA
        ExternalAccountEntity entity = toEntity(externalAccount);
        // Salva a entidade usando o repositório JPA
        ExternalAccountEntity savedEntity = jpaRepository.save(entity);
        // Mapeia a entidade salva de volta para o modelo de domínio e retorna
        return toDomainModel(savedEntity);
    }

    // Métodos de mapeamento privados
    private ExternalAccountEntity toEntity(ExternalAccount domainModel) {
        var entity = new ExternalAccountEntity();
        if (domainModel.getId() != null) {
            entity.setId(domainModel.getId());
        } else {
            entity.setId(UUID.randomUUID()); // Garante que a entidade tenha um ID se for nova
        }
        entity.setUserId(domainModel.getUserId());
        entity.setProvider(domainModel.getProvider());
        entity.setProviderUserId(domainModel.getProviderUserId());
        entity.setAccountEmail(domainModel.getAccountEmail());
        entity.setCreatedAt(domainModel.getCreatedAt());
        return entity;
    }

    private ExternalAccount toDomainModel(ExternalAccountEntity entity) {
        return new ExternalAccount(
                entity.getId(),
                entity.getUserId(),
                entity.getProvider(),
                entity.getProviderUserId(),
                entity.getAccountEmail(),
                entity.getCreatedAt(),
                null // O modelo de domínio de Credentials não está na entidade JPA ainda
        );
    }
}

