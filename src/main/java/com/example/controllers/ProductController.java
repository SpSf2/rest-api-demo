package com.example.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Product;
import com.example.services.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/*/**
* La anotacion @RestController es para que todos los metodos que van a ser
* creados dentro de este controlador y reciben peticiones a través del protocolo 
HTTP, mediante los verbos correspondientes (GET, POST, PUT, DELETE, PATCH, etc.) 
devuelvan o reciban datos en formato de JSON (JavaScript Object Notation)
*/
@RestController

/**
* Una API REST esta orientada al recurso, es decir, que el controlador necesita
* que se le especifique que recurso va a responder, por ejemplo en esto seria 
/products, y en dependencia del verbo del protocolo HTTP se estaria haciendo una
 peticion (request) contreta. Por ejemplo: Si el verbo es GET, significa que 
 estamos solicitando todos los productos al recurso /productos. Si el verbo es 
 POST significa que queremos recibir un producto en formato JSON, en el cuerpo de
  la peticion (request) y persistirlo (guardarlo) en las tablas correspondientes
*/
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

     /**
     * 
     * IMPORTANTE!!!
     * 
     * Una API REST tiene que devolver informacion respecto a como ha sido
     * solucionada la peticion (request),
     * por ejemplo: el codigo 200 significa estado OK de la peticion, el codido 201
     * significaria CREATED,
     * el codigo 500 significaria que el servidor no ha podido cumplimentar la
     * peticion, el codigo 401 NO ENCONTRADO,
     * el codigo 403 prohibido, etc. Todos estos codigos se pueden encontrar en el
     * sitio de W3Schools
     * 
     * https://www.w3schools.com/tags/ref_httpmessages.asp
     * 
     */ 
        /**
     * El metodo siguiente va a responder a una peticion (request) del tipo:
     * 
     * http://localhost:8080/products?page=0&size=3 -> de los productos devuelveme la pagina 0
     * de tamaño 3
     * 
     * Donde los parametros page y size seran utilizados para la paginacion, y no
     * seran requeridos, es decir,
     * que no son obligatorios que se suministren. Y en caso de NO ser suministrados
     * (page y size),
     * los productos se van a devolver ordenados.
     */
        @GetMapping        
        public ResponseEntity<Map<String, Object>> dameProductos(
                                @RequestParam(name = "page", required = false) Integer page,
                                @RequestParam(name = "size", required = false) Integer size) {

            List<Product> products = null;
            Map<String, Object> responseAsMap = new HashMap<>();
            Sort sort = Sort.by("name");                        

            /*Comprobar si en la peticion (request) me han suministardo los parametros page y size */
            if (page != null && size != null) {

                Pageable pageable = PageRequest.of(page, size, sort);
                Page<Product> productPage = productService.findAll(pageable);
                products =productPage.getContent();
                responseAsMap.put("totalElements", products);
            }
            else {
                //Devolver todos los productos ordenados por nombre, por ejemplo
                products = productService.findAll(sort);
                responseAsMap.put("totalElements", products);
            }

            return new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.OK);
        }


        /**El Método siguiente recupra un producto por id que se recibe como una variable en
         * la ruta, mediante un endpoint (url o uri) que tiene el fotmato:
         * 
         * http://localhost:8080/products/{id}
         * http://localhost:8080/products/1     donde el valor 1 al final del endpoint sería
         * el id del producto que se quiere recuperar
         */

        @GetMapping("/{id}")
        public ResponseEntity<Map<String, Object>> findProductById(
                            @PathVariable(name = "id", required = true) Integer product_id) {
            
            Map<String, Object> responseAsMap = new HashMap<>();
            ResponseEntity<Map<String, Object>> responseEntity = null;

            try {
                Product product = productService.findById(product_id);
                if (product != null) {
                    String successMessage = "Producto encontrado con id: " + product_id;
                    responseAsMap.put("mensaje todo Ok.", successMessage);
                    responseAsMap.put("producto encontrado", product);
                    responseEntity = new ResponseEntity<Map<String, Object>>(
                                                responseAsMap, HttpStatus.OK);
                } else {
                    String errorMessage = "Producto no encontrado con id: " + product_id;
                    responseAsMap.put("mensaje error: ", errorMessage);
                    responseEntity = new ResponseEntity<Map<String, Object>>(
                                                responseAsMap, HttpStatus.NOT_FOUND);
                }
            } catch (DataAccessException e) {
                
                String errorMessage = "Error Grave al recuperar el producto con id: " 
                + product_id + ", y la causa más probable es: " + 
                                            e.getMostSpecificCause().getMessage();
                responseAsMap.put("Error Grave: ", errorMessage);
                responseEntity = new ResponseEntity<Map<String, Object>>(
                                                responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
            }                    
            //se puede quitar el <Map<String, Object>> y solo dejar <> 
            // porque el compilador puede inferir el tipo de dato
            
            return responseEntity;
        }



}
