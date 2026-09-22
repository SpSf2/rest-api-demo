package com.example.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

import com.example.dao.PresentationDao;
import com.example.dao.ProductDao;
import com.example.entities.Presentation;
import com.example.entities.Product;

/** Los Test hay que realizarlos en aislamiento.  Los test a la capa de servicio y se consideran
 * test de integración porque dependen de la capa repositorio, pero dichas dependencias se 
 * simulan (mock) en lugar de inyectarlas realmente, y aqui entra el framework (Mockito)
 */
@ExtendWith (MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock /*La dependencia simula en lugar de inyectarlas realmente para aislar el test */
    private ProductDao productDao;

    @Mock
    private PresentationDao presentationDao;

    @InjectMocks // sirve para inyectar los mocks en la clase que se va a testear
    private ProductServiceImpl productServiceImpl;

    /* Paso 2: Necesitamos crear productos para todos los test de la capa de servicios */
    //Declaramos un Product fuera de setup para poder utilizarlo en todos los métodos:
    Product product1, product2;

    List<Product> productsList = new ArrayList<>();

    @BeforeEach 
    void setUp() {

        Presentation presentation = Presentation.builder()
                .name("Unidades")
                .description("Por Unidades")
                .build();

        product1 = Product.builder()
                .name("Huawey Mate 20 Lite")
                .description("64GB Ram, 128Gb SSD")
                .price(new BigDecimal("299.99"))           
                .stock(10)
                .productImage(null)
                .presentation(presentation)
                .build();

        product2 = Product.builder()
                .name("Samsung Galaxy S20")
                .description("128GB Ram, 256Gb SSD")
                .price(new BigDecimal("375.99"))           
                .stock(500)
                .productImage(null)
                .presentation(presentation)
                .build();

        productsList.add(product1);
        productsList.add(product2);
    } // paso 2 hasta aqui
    
                                     // Paso 1: Primero creamos este test:
    @Test
    @DisplayName ("Test del Servicio para persistir un Product") // Damos un nombre al test
    void testSave() {

        // Paso 3:
        // given: se debe importar BDDMockito para poder trabajar con given, when, then
        // el service usa el repository (Dao) para persistir los productos
        given(productDao.save(product1)).willReturn(product1); // simulamos el comportamiento del Dao

        // when: cuando se guarde el producto utilizando el servicio:
        Product productoPersistido = productServiceImpl.save(product1);

        //then: 
        assertThat(productoPersistido).isNotNull();  
    }
            //Paso 4: Creamos un nuevo método (personalizado)
    @Test 
    @DisplayName ("Test para Recuperar una Lista vacia de Product")
    void testEmptyProductList(){

        //given
        given(productDao.findAll()).willReturn(Collections.emptyList());

        //when
        List<Product> products = productServiceImpl.findAll();

        //then
        assertThat(products).isEmpty();
    }

    @Test
    @DisplayName ("Test para Recuperar los dos Productos Creados")
    void testFindAll() {
       
        when(productServiceImpl.findAll()).thenReturn(productsList);
        
        List<Product> result = productServiceImpl.findAll();
        
        assertEquals( 2, result.size());
    }

      @Test
    void testDelete() {

    }

    @Test
    void testFindAll2() {

    }

    @Test
    void testFindAll3() {

    }

    @Test
    void testFindById() {

    }
}
