package com.hickar.restly.repository.mappers

interface Mapper<Entity, DTO> {
    fun toDTO(entity: Entity): DTO

    fun toEntity(entityDTO: DTO): Entity

    fun toDTOList(entities: List<Entity>): List<DTO>

    fun toEntityList(dtos: List<DTO>): List<Entity>
}