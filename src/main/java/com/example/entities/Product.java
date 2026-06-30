package com.example.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "El producto debe tener un nombre")
    @NotEmpty(message = "El nombre del producto no puede estar vacio")
    @Size(min = 3, max = 50, message = "El nombre del producto debe tener entre 3 y 50 caracteres")
    private String name;

    @NotNull(message = "El producto debe tener una descripcion")
    @NotBlank(message = "La descripcion del producto no puede tener espacios vacios solamente")
    @Size(min = 3, max = 50, message = "La descripcion del producto debe tener entre 3 y 50 caracteres")
    private String description;

    @Min(value = 1, message = "El stock no puede ser negativo")
    private int stock;

    @Min(value = 0, message = "El precio no puede ser un número negativo")
    private BigDecimal price;

    @NotNull(message = "La Presentación del producto es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Presentation presentation;

}
