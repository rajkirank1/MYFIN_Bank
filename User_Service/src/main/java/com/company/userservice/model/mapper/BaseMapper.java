package com.company.userservice.model.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generic mapper base providing collection helpers.
 *
 * E = Entity type
 * D = DTO type
 */
public abstract class BaseMapper<E, D> {

    public abstract E convertToEntity(D dto, Object... args);

    public abstract D convertToDto(E entity, Object... args);

    public Collection<E> convertToEntity(Collection<D> dtos, Object... args) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(d -> convertToEntity(d, args))
                .collect(Collectors.toList());
    }

    public Collection<D> convertToDto(Collection<E> entities, Object... args) {
        if (entities == null) return null;
        return entities.stream()
                .map(e -> convertToDto(e, args))
                .collect(Collectors.toList());
    }

    public List<E> convertToEntityList(Collection<D> dtos, Object... args) {
        return (List<E>) convertToEntity(dtos, args); // common implementation returns List
    }

    public List<D> convertToDtoList(Collection<E> entities, Object... args) {
        return (List<D>) convertToDto(entities, args);
    }

    public Set<E> convertToEntitySet(Collection<D> dtos, Object... args) {
        if (dtos == null) return null;
        return convertToEntity(dtos, args).stream().collect(Collectors.toSet());
    }

    public Set<D> convertToDtoSet(Collection<E> entities, Object... args) {
        if (entities == null) return null;
        return convertToDto(entities, args).stream().collect(Collectors.toSet());
    }
}
