package com.example.controllers;

import com.example.CreateSamplesData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.Product;
import com.example.models.FileUploadResponse;
import com.example.services.ProductService;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;
import com.example.utilities.FileUtil;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


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

    private final CreateSamplesData createSamplesData;
    private final ProductService productService;
    private final FileUploadUtil fileUploadUtil;
    private final FileDownloadUtil fileDownloadUtil;
    private final FileUtil fileUtil;

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


        /**El Método siguiente recupera un producto por id que se recibe como una variable en
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

        
        /** Primero: Hay que cambiar lo que recibe el metodo saveProduct, porque ya el producto
        * no viene ocupando todo el cuerpo de la peticion (request), sino una parte, y la otra
        * parte la ocupa la imagen del producto
        * 
        * Y, muy importante, que no se nos olvide anotar este metodo y todos los que insertan, crean,
        * y eliminan registros en las tablas con la anotacion @Transactional,
        * y tambien hay que especificar el tipo de archivo que va a consumir este metodo 
        * @throws IOException */
        //Metodo que recibe por el Post el Producto para ser persistido, y que valida el JSON
        //recibido para comprobar si esta bien formado o no:

        @PostMapping(consumes = "multipart/form-data")
        @Transactional
        public ResponseEntity<Map<String, Object>> saveProduct(@Valid
                            @RequestPart Product product,
                            BindingResult result, @RequestPart(name = "file", required = false)
                            MultipartFile imagenDelProducto) throws IOException {
            
            List<String> mensajesDeError = new ArrayList<>();
            Map<String, Object> responseAsMap = new HashMap<>();
            ResponseEntity<Map<String, Object>> responseEntity = null;
            
            /**Primero comprobar si hay errores en el producto recibido:  */
            if (result.hasErrors()) {
                //Recuperamos los errores que tiene el producto recibido y se lo informamos
                //al que realizó la petición (request) de persistir el producto: 
                List<ObjectError> objectErrors =result.getAllErrors();

                objectErrors.stream().forEach(objetcError ->
                    mensajesDeError.add(objetcError.getDefaultMessage()));
                responseAsMap.put("El Producto tiene los siguientes errores: ", 
                                 mensajesDeError);
                responseAsMap.put("Producto mal formado", product);

                responseEntity = new ResponseEntity<Map<String, Object>>(
                                                responseAsMap, HttpStatus.BAD_REQUEST);

                return responseEntity;
            }
                //Antes vamos a comprobar si hemos recibido imagen del producto  para guardarla
                //en el sistema de archivos
                if (imagenDelProducto != null && !imagenDelProducto.isEmpty()) {
                    /**
             * Para guardar la imagen del producto en primer lugar le agregaremos como prefijo un codigo
             * alfanumerico (de letras y numero), generado aleatoriamente a partir de un metodo que se 
             * encuentra en la biblioteca Apache Commons text 1.15, que hay que descargar la dependencia desde
             * el repositorio central de maven y agregarla al pom.xml 
             */
            /**Vamos a crear un componente en un paquete que podría ser com.example.utilities, y
             * este componente va a tener un metodo para guardar la imagen recibida en una carpeta del file
             * system y devolver un código alfanumerico generado aleatoriamente que llevará como prefijo el
             * nombre del fichero de imagen recibido.
             * Se hará uso intensivo de Nio.2 y se comprobara si la carpeta existe o no para crearla.
             */
                    String fileCode = fileUploadUtil.saveFile(imagenDelProducto.getOriginalFilename(), imagenDelProducto);
                    product.setProductImage(fileCode + '-' + imagenDelProducto.getOriginalFilename());

                    /**Como es una ApiRest hay que devolver información al que ha realizado la request respecto
                     * a la imagen subida, para lo cual vamos a crear en un paquete llamado com.example.models
                     * un Record, donde devolveremos la información de la imagen
                     */
                    FileUploadResponse fileUploadResponse = new FileUploadResponse
                        (fileCode + '-' + imagenDelProducto.getOriginalFilename(),
                        "/products/fileDownload",
                        imagenDelProducto.getSize());

                    responseAsMap.put("Información de la imagen del Producto:", fileUploadResponse);



                }

                /**Persistimos el producto porque si hemos llegado a este punto, es que está
                 * bien formado
                 */
                try {
                    Product productoPersistido = productService.save(product);
                    responseAsMap.put("mensaje", "Producto Persistido exitosamente");
                    responseAsMap.put("producto", productoPersistido);
                    responseEntity = new ResponseEntity<Map<String, Object>>(
                                                responseAsMap, HttpStatus.CREATED);
                } catch (DataAccessException e) {
                    responseAsMap.put("mensaje", "Error al persistir el producto y" +
                    "la causa más probable es: " + 
                                            e.getMostSpecificCause().getMessage());
                    responseEntity = new ResponseEntity<Map<String, Object>>(
                                                responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
                }

            return responseEntity;      
        }


                /**
        * Metodo que recupera la imagen de un producto, dado el codigo que 
        * tiene como prefijo el nombre de la imagen
                */
        @GetMapping("/fileDownload/{fileCode}")
        public ResponseEntity<?> downloadFile(@PathVariable String fileCode) {

            Resource resource = null;

            try {
                resource = fileDownloadUtil.getFileAsResource(fileCode);
            } catch (IOException e) {
                return ResponseEntity.internalServerError()
                                     .build();
            }

            if (resource == null) {
                return new ResponseEntity<>("Imagen del Producto no encontrada",
                            HttpStatus.NOT_FOUND);
            }
                
                            /**
                * Si estamos en este punto quiere decir que el fichero (imagen del producto) ha sido
                * encontrado y podemos enviarlo como respuesta a la peticion, como un fichero adjunto
                * en el cuerpo de la respuesta */

            String contentType = "application/octet-stream";
            String headerValue = "attachment; fileName=\"" + resource.getFilename() + "\"";

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
                        }


        /*Metodo que actualiza un producto cuyo id se recibe en la petición conjuntamente
        con el Json del producto y la imagen del producto, queno es requerida
        
        El método es practicamente igual al metodo que persiste a un producto con la
         imagen recibida, con lo cual, podemos copiar y pegar el contenido del metodo
         saveProduct
         */               
        @PutMapping(value = "/{id}", consumes = "multipart/form-data")
        @Transactional
        public ResponseEntity<Map<String, Object>> updateProduct(@Valid
                            @RequestPart Product product,
                            BindingResult result, @RequestPart(name = "file", required = false)
                            MultipartFile imagenDelProducto, 
                            @PathVariable("id") int product_id) throws IOException {
            
            List<String> mensajesDeError = new ArrayList<>();
            Map<String, Object> responseAsMap = new HashMap<>();
            ResponseEntity<Map<String, Object>> responseEntity = null;
            
            /**Primero comprobar si hay errores en el producto recibido:  */
            if (result.hasErrors()) {
                //Recuperamos los errores que tiene el producto recibido y se lo informamos
                //al que realizó la petición (request) de persistir el producto: 
                List<ObjectError> objectErrors =result.getAllErrors();

                objectErrors.stream().forEach(objetcError ->
                    mensajesDeError.add(objetcError.getDefaultMessage()));
                responseAsMap.put("El Producto tiene los siguientes errores: ", 
                                 mensajesDeError);
                responseAsMap.put("Producto mal formado", product);

                responseEntity = new ResponseEntity<Map<String, Object>>(
                                                responseAsMap, HttpStatus.BAD_REQUEST);

                return responseEntity;
            }
                //Actualizamos el producto pero Antes vamos a comprobar si hemos recibido
                // imagen del producto  para guardarla
                //actualizarla, en cuyo caso debemos, primero, eliminar la imagen asociada al producto
                //guardado en el sistema de  archivos (file system)

                //Recuperando el producto cuyo id hemos recibido como parte de la petición:
                Product productoGuardado = productService.findById(product_id);

                if (productoGuardado == null) {
                    responseAsMap.put("mensaje de error: ", "producto con id " + product_id + " no encontrado");
                    return new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.NOT_FOUND);
                }

                if (imagenDelProducto != null && !imagenDelProducto.isEmpty()) {
                    /*Comprobar si el producto guardado tiene imagen y eliminarla */
                    if (productoGuardado.getProductImage() != null) {
                        /*Eliminar la imagen asociada al Producto guardado para lo cual vamos a necesitar
                        de un metodo en un componente que reciba el nombre del fichero de imagen y
                         y lo busque en la carpeta a donde hemos subido las imagenes, y lo elimine*/
                        fileUtil.eliminarArchivo(productoGuardado.getProductImage());
                    
                    }

                    /**
             * Para guardar la imagen del producto en primer lugar le agregaremos como prefijo un codigo
             * alfanumerico (de letras y numero), generado aleatoriamente a partir de un metodo que se 
             * encuentra en la biblioteca Apache Commons text 1.15, que hay que descargar la dependencia desde
             * el repositorio central de maven y agregarla al pom.xml 
             */
            /**Vamos a crear un componente en un paquete que podría ser com.example.utilities, y
             * este componente va a tener un metodo para guardar la imagen recibida en una carpeta del file
             * system y devolver un código alfanumerico generado aleatoriamente que llevará como prefijo el
             * nombre del fichero de imagen recibido.
             * Se hará uso intensivo de Nio.2 y se comprobara si la carpeta existe o no para crearla.
             */
                    String fileCode = fileUploadUtil.saveFile(imagenDelProducto.getOriginalFilename(), imagenDelProducto);
                    product.setProductImage(fileCode + "-" + imagenDelProducto.getOriginalFilename());

                    /**Como es una ApiRest hay que devolver información al que ha realizado la request respecto
                     * a la imagen subida, para lo cual vamos a crear en un paquete llamado com.example.models
                     * un Record, donde devolveremos la información de la imagen
                     */
                    FileUploadResponse fileUploadResponse = new FileUploadResponse
                        (fileCode + '-' + imagenDelProducto.getOriginalFilename(),
                        "/products/fileDownload",
                        imagenDelProducto.getSize());

                    responseAsMap.put("Información de la imagen del Producto:", fileUploadResponse);



                }

                /**Persistimos el producto porque si hemos llegado a este punto, es que está
                 * bien formado
                 */
                try {
                    product.setId(product_id);
                    Product productoPersistido = productService.save(product);
                    responseAsMap.put("mensaje", "Producto Actualizado exitosamente");
                    responseAsMap.put("producto Actualizado", productoPersistido);
                    responseEntity = new ResponseEntity<Map<String, Object>>(
                                                responseAsMap, HttpStatus.CREATED);
                } catch (DataAccessException e) {
                    responseAsMap.put("mensaje", "Error al actualizar el producto y" +
                    "la causa más probable es: " + 
                                            e.getMostSpecificCause().getMessage());
                    responseEntity = new ResponseEntity<Map<String, Object>>(
                                                responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
                }

            return responseEntity;      
        }

}
