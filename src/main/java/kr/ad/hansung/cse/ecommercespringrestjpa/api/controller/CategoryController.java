package kr.ad.hansung.cse.ecommercespringrestjpa.api.controller;

import kr.ad.hansung.cse.ecommercespringrestjpa.api.Representationmodel.CategoryModel;
import kr.ad.hansung.cse.ecommercespringrestjpa.api.assembler.CategoryModelAssembler;
import kr.ad.hansung.cse.ecommercespringrestjpa.entity.Category;
import kr.ad.hansung.cse.ecommercespringrestjpa.exception.NotFoundException;
import kr.ad.hansung.cse.ecommercespringrestjpa.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping(path = "/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryModelAssembler categoryModelAssembler;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> retrieveAllCategories() {

        // Getting all categories in application...
        final List<Category> categories = categoryService.getAllCategories();

        return ResponseEntity.ok(
                categoryModelAssembler.toCollectionModel(categories));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> retrieveCategory(@PathVariable Long id) {

        // Getting the requiring category; or throwing exception if not found
        return categoryService.getCategoryById(id)
                .map(categoryModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

        /* 동일한 코드: final Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new NotFoundException(id));
        return ResponseEntity.ok(categoryModelAssembler.toModel(category));*/
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createCategory(@RequestBody @Valid CategoryDto request) {

        // Creating a new category in the application...
        final Category category = categoryService.createCategory(request.getName());
        CategoryModel categoryModel  = categoryModelAssembler.toModel(category);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryModel);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryDto request) {

        // Getting the requiring category; or throwing exception if not found
        final Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new NotFoundException(id));

        // Updating a category in the application...
        categoryService.updateCategory(category, request.getName());

        return ResponseEntity.ok(categoryModelAssembler.toModel(category));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {

        // Getting the requiring category; or throwing exception if not found
        final Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new NotFoundException(id));

        // Deleting category from the application...
        categoryService.deleteCategory(category);

        return ResponseEntity.noContent().build();
    }

    static class CategoryDto {
        @NotNull(message = "name is required")
        @Size(message = "name must be equal to or lower than 100", min = 1, max = 100)
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}