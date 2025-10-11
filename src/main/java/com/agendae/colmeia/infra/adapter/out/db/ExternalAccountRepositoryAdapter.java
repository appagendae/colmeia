package com.agendae.colmeia.infra.adapter.out.db;

import com.agendae.colmeia.domain.model.ExternalAccount;
import com.agendae.colmeia.domain.port.out.ExternalAccountRepositoryPort;
import com.agendae.colmeia.infra.entity.ExternalAccountEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ExternalAccountRepositoryAdapter implements ExternalAccountRepositoryPort {

    private final SpringDataExternalAccountRepository jpaRepository;

    public ExternalAccountRepositoryAdapter(SpringDataExternalAccountRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ExternalAccount> findById(UUID id) {
        Optional<ExternalAccountEntity> entityOptional = jpaRepository.findById(id);
        return entityOptional.map(this::toDomainModel);
    }

    @Override
    public ExternalAccount save(ExternalAccount externalAccount) {
        ExternalAccountEntity entity = toEntity(externalAccount);
        ExternalAccountEntity savedEntity = jpaRepository.save(entity);
        return toDomainModel(savedEntity);
    }

    @Override
    public List<ExternalAccount> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList());
    }

    // Métodos de mapeamento privados
    private ExternalAccountEntity toEntity(ExternalAccount domainModel) {
        ExternalAccountEntity entity = new ExternalAccountEntity();
        if (domainModel.getId() != null) {
            entity.setId(domainModel.getId());
        } else {
            entity.setId(UUID.randomUUID());
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
                null
        );
    }
}

