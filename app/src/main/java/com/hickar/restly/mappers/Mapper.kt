package com.hickar.restly.mappers

interface Mapper<Entity, DTO> {
    fun toDTO(entity: Entity): DTO

    fun toEntity(entityDTO: DTO): Entity

    fun toDTOMutableList(entities: MutableList<Entity>): MutableList<DTO>

    fun toEntityMutableList(dtos: MutableList<DTO>): MutableList<Entity>
}