package com.example.entities;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="presentation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class Presentation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "La Presentación debe tener un nombre")
    @NotEmpty(message = "El nombre de la presentación no puede estar vacio")
    @Size(min = 4, max = 25, message = "El nombre de la presentación debe tener entre 3 y 25 caracteres")
    private String name;

    @NotNull(message = "La Presentación debe tener una descripcion")
    @NotEmpty(message = "La descripcion de la presentación no puede estar vacia")
    @Size(max = 30, message = "La descripcion de la presentación debe tener entre 3 y 30 caracteres")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "presentation")
    private List<Product> productos;
}
