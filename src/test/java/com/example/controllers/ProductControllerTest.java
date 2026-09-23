package com.example.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.entities.Presentation;
import com.example.entities.Product;
import com.example.services.ProductService;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;
import com.example.utilities.FileUtil;
import com.example.CreateSamplesData; // Ajusta el paquete exacto si es necesario
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; // Import corregido

@WebMvcTest(controllers = ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private FileUploadUtil productFileUploadUtil;

    @MockitoBean
    private FileDownloadUtil fileDownloadUtil;

    @MockitoBean
    private FileUtil fileUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CreateSamplesData createSamplesData;

    List<Product> productsList = new ArrayList<>();
    Presentation presentation, presentation2;
    Product product, product2;

    @BeforeEach
    void setUp() {

         presentation = Presentation.builder()
                .name("Decenas")
                .description("Por Decenas")
                .build();

         product = Product.builder()
                .name("Cámara")
                .description("Cámara HP 65-12 Megapixeles")
                .price(new BigDecimal("400.99"))           
                .stock(1100)
                .productImage(null)
                .presentation(presentation)
                .build();

         presentation2 = Presentation.builder()
                .name("Unidades")
                .description("Por Unidades")
                .build();

         product2 = Product.builder()
                .name("Frigorifico")
                .description("General Electric")
                .price(new BigDecimal("1100.99"))           
                .stock(150)
                .productImage(null)
                .presentation(presentation2)
                .build();

        productsList.add(product);
        productsList.add(product2);

    }

    @Test
    @DisplayName("Controller Test: Test que recupera todos los productos")
    void testFindAll() throws Exception { // Lanzamos Exception en la firma del método

        // given  
        given(productService.findAll(Sort.by("name"))).willReturn(productsList);

        // when & then
        ResultActions response = mockMvc.perform(get("/products")
                                .accept(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements.size()", is(productsList.size())));
    }  /*Si el controlador envuelve la lista dentro de una clave del mapa/DTO, debes usar el 
    nombre exacto de esa clave en el JSON path (totalElements)*/

    @Test
    @DisplayName("Controller Test: Test para Persistir un producto")
    void testSavedProducts() throws JsonProcessingException {

        // given
        given(productService.save(any(Product.class)))
                             .willAnswer(invocation -> invocation.getArgument(0));
        //when
        String jsonStringProduct = objectMapper.writeValueAsString(product);

        MockMultipartFile bytesArrayProduct = new MockMultipartFile("product", 
                                                             null, 
                                                             "application/json",
                                                             jsonStringProduct.getBytes());
        
        try {
       /* ResultActions response = */       mockMvc.perform(multipart("/products")
                                                                .file(bytesArrayProduct)
                                                                .file("file", null))
                                                                .andDo(print())
                                                                .andExpect(status().isCreated())
                                                                .andExpect(jsonPath("$.producto.name"
                                                                , is(product.getName())));;
        //then                                                  
            /*  response.andDo(print())
                      .andExpect(status().isCreated())
                      .andExpect(jsonPath("$.producto.name", is(product.getName()))); */
                      
/*Lo que se comentó fue para demostrar una forma de hacer un test con el resultado de la petición
en una variable (response) */

        } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }

        


    }
  
}