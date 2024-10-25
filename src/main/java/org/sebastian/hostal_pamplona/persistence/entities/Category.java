package org.sebastian.hostal_pamplona.persistence.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "TBL_PERSONS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Entidad que representa a las categorías de habitación en BD")
public class Category {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Comment("Clave primaria")
    @Schema(description = "Clave primaria autogenerada")
    private Long id;

    @Column(name = "NAME", nullable = false, length = 150 )
    @Comment("Nombre de la categoría")
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 1000 )
    @Comment("Descripción de la categoría")
    private String description;

    @Column(name = "POPULATE", nullable = false )
    @Comment("Popularidad en cantidad de la categoría")
    private Integer populate;

    @Column(name = "STATUS" )
    @Comment("Estado de la categoría")
    private Boolean status;

}
