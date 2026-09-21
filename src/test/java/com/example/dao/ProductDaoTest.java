package com.example.dao;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.example.entities.Presentation;
import com.example.entities.Product;

/*Esta anotacion solamente prueba las entidades y repositorios de datos, no levanta todo el contexto
de Spring. con esta anotación no hace falta que los metodos sean Transactionals porque ya se incluye
por defecto  */
@DataJpaTest 

/*Para indicar que cuando termine el test, la DB se tiene que quedar como estaba inicialmente, es
decir, que se haga roll-back */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
public class ProductDaoTest {

    // Implement test setup logic here
        /**Antes de poder probar que sucede cuando se persiste un Producto, hay que crearlo y 
         * como esto va a ser necesario para todos los demás métodos de test, lo vamos a crear
         * fuera de los métodos, entonces despues de ProductDaoTest, vamos a crear un método que
         *  se llame setUp()
         */

    @Autowired 
    private PresentationDao presentationDao;

    @Autowired
    private ProductDao productDao;

    private Presentation presentationporUnidades;
    private Presentation presentationporDecenas;

    private Product product0;

    @BeforeEach // (antes de cada) antes de ejecutar cualquier test se ejecuta este setup
    void setUp() {
        presentationporUnidades = Presentation.builder()
                .name("Unidad")
                .description(" Producto x Unidades")
                .build();

        presentationporDecenas = Presentation.builder()
                .name("Decenas")
                .description(" Producto x Decenas")
                .build();
        
    }


    @Test
    @DisplayName("Test de Save Product") // Damos un nombre al test
    void testSaveProduct() {
       
        // given

        Presentation presentation0 = presentationDao.save(presentationporUnidades);

        //Creamos un producto
        product0 = Product.builder()
                .name("Huawey Mate 20 Lite")
                .description("64GB Ram, 128Gb SSD")
                .stock(10)                
                .presentation(presentation0)
                .build();

        //when

        Product productoPersistido = productDao.save(product0);

        //then

        assertThat(productoPersistido).isNotNull();
        assertThat(productoPersistido.getId()).isGreaterThan(0);


    }

}
