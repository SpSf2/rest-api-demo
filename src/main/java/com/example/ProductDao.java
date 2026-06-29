package com.example;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entities.Product;

public interface ProductDao extends JpaRepository<Product, Integer> {

    /*Vamos a necesitar 3 metodos personalizados que recuperen la presentación
     en una sola consulta. Esto no es más eficiente pero si es más rápido y estará
     disponible la presentación para cada uno de los productos.  Esto consume más
     recursos puesto que el patrón LAZY (perezozo) se trae la entidad principal e
     Hibernate no trae las entidades relacionadas hasta su primr uso y se requiere
     más veocidad. podemos mejorar esta caracteristica trayendo todo en una query.
     
     Los metodos personalizados serán las siguientes:

     1- Metodo que recupera los productos paginados y ordenados, de 10 en 10, de 
     20 en 20, etc.
     
     2- Metodo que recupera los productos ordenados, sin paginacion.  Ordenados por 
     el nombre, por ejemplo, porque podria ser por cualquier otro campo de la entidad
     o por varios campos inclusive (stock, por ejemplo).
     
     3- Dado el id del produccto, recupere el producto con su presentación correspondiente
     
      Para implementar los metodos anteriores vamos a hacer uso de HQL/JPQL, que son 
    * lenguajes basados en SQL pero orientados a las entidades y no a las tablas de la
    * base de datos, es decir, que se consultan las entidades y no las tablas de la 
    * base de datos como en SQL     */

      /*Metodo 1. que recupera los productos paginados */
      @Query(value = "select p from Product p left join fetch p.presentation",
            countQuery = "select count(p) from Product p left join p.presentation")
      public Page<Product> findAll(Pageable pageable);

      //Metodo 2.
      /*Metodo 1. que recupera los productos paginados */ 
      @Query(value = "select p from Product p left join fetch p.presentation")
      public List<Product> findAll(Sort sort);

      /* Metodo 3. Dado el id del producto, recupera el producto con su presentación
       * correspondiente */
      @Query(value = "select p from Product p left join fetch p.presentation where p.id = :id")
      public Product findById(int id);

}
