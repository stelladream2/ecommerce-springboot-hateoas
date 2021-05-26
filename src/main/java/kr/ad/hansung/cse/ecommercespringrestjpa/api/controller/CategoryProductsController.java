package kr.ad.hansung.cse.ecommercespringrestjpa.api.controller;

import kr.ad.hansung.cse.ecommercespringrestjpa.api.Representationmodel.CategoryModel;
import kr.ad.hansung.cse.ecommercespringrestjpa.api.Representationmodel.ProductModel;
import kr.ad.hansung.cse.ecommercespringrestjpa.api.assembler.CategoryModelAssembler;
import kr.ad.hansung.cse.ecommercespringrestjpa.api.assembler.ProductModelAssembler;
import kr.ad.hansung.cse.ecommercespringrestjpa.entity.Category;
import kr.ad.hansung.cse.ecommercespringrestjpa.entity.Product;
import kr.ad.hansung.cse.ecommercespringrestjpa.exception.NotFoundException;
import kr.ad.hansung.cse.ecommercespringrestjpa.service.CategoryService;
import kr.ad.hansung.cse.ecommercespringrestjpa.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/* API Endpoint for categories and products association
 *
 * Link / Unlink products
 * 
 * To see the current products for a given category, you can do a GET on
 * 		/api/categories/{categoryid}/products
 * 
 * To link / unlink products with categories you can use the following URL:
 * 		/api/categories/{categoryid}/products/{productid}
 */

@RestController
@RequestMapping(path = "/api/categories/{categoryid}/products")
public class CategoryProductsController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductModelAssembler productModelAssembler;

	@Autowired
	private CategoryModelAssembler categoryModelAssembler;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> retrieveAllProducts(@PathVariable Long categoryid) {

		// Getting the requiring category; or throwing exception if not found
		final Category category = categoryService.getCategoryById(categoryid)
				.orElseThrow(() -> new NotFoundException(categoryid));

		// remove information about parent and child categories
		category.setParent(null);
		category.setChildCategories(null);

		return ResponseEntity.ok(categoryModelAssembler.toModel(category));
	}
	
	@RequestMapping(path = "/{productid}", method = RequestMethod.POST)
	public ResponseEntity<?> addProduct(@PathVariable Long categoryid, @PathVariable Long productid) {

		// Getting the requiring category; or throwing exception if not found
		final Category category = categoryService.getCategoryById(categoryid)
				.orElseThrow(() -> new NotFoundException(categoryid));

		// Getting the requiring product; or throwing exception if not found
		final Product product = productService.getProductById(productid)
						.orElseThrow(() -> new NotFoundException(categoryid));

		// Validating if association does not exist...
		if (product.getCategories() != null &&
				productService.hasCategory(product, category)) {
			throw new IllegalArgumentException(
					"product " + product.getId() + " already contains category " + category.getId());
		}

		// Associating product with category...
		productService.addCategory(product, category);

		ProductModel productModel  = productModelAssembler.toModel(product);

		return ResponseEntity.status(HttpStatus.CREATED).body(productModel);
	}

	@RequestMapping(path = "/{productid}", method = RequestMethod.DELETE)
	public ResponseEntity<?> removeProduct(@PathVariable Long categoryid, @PathVariable Long productid) {
		// Getting the requiring category; or throwing exception if not found
		final Category category = categoryService.getCategoryById(categoryid)
				.orElseThrow(() -> new NotFoundException(categoryid));

		// Getting the requiring product; or throwing exception if not found
		final Product product = productService.getProductById(productid)
				.orElseThrow(() -> new NotFoundException(productid));

		// Validating if association does not exist...
		if (!productService.hasCategory(product, category)) {
			throw new IllegalArgumentException("product " + product.getId() + " does not contain category " + category.getId());
		}

		// Dis-associating product with category...
		productService.removeCategory(product, category);

		return ResponseEntity.noContent().build();
	}
}