package kr.ad.hansung.cse.ecommercespringrestjpa.api.assembler;

import kr.ad.hansung.cse.ecommercespringrestjpa.api.Representationmodel.CategoryModel;
import kr.ad.hansung.cse.ecommercespringrestjpa.api.Representationmodel.ProductModel;
import kr.ad.hansung.cse.ecommercespringrestjpa.api.controller.CategoryController;
import kr.ad.hansung.cse.ecommercespringrestjpa.api.controller.CategorySubcategoriesController;
import kr.ad.hansung.cse.ecommercespringrestjpa.api.controller.ProductController;
import kr.ad.hansung.cse.ecommercespringrestjpa.entity.Category;
import kr.ad.hansung.cse.ecommercespringrestjpa.entity.Product;
import kr.ad.hansung.cse.ecommercespringrestjpa.util.CollectionCheck;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Transform {@link Category} into {@link CategoryModel} DTO
 *
 * @author dnardelli
 */
@Component
public class CategoryModelAssembler
        extends RepresentationModelAssemblerSupport<Category, CategoryModel> {

    public CategoryModelAssembler() {
        super(CategoryController.class, CategoryModel.class);
    }

    @Override
    public CategoryModel toModel(Category entity) {

        CategoryModel categoryModel = instantiateModel(entity);

        categoryModel.add( linkTo(methodOn(CategoryController.class).
                retrieveCategory(entity.getId()))
                .withSelfRel() );

        categoryModel.setId(entity.getId());
        categoryModel.setName(entity.getName());

        if (entity.getParent() != null) {
            categoryModel.add(linkTo(methodOn(CategoryController.class)
                    .retrieveCategory(entity.getParent().getId())).withRel("parent"));
        }

        if(!CollectionCheck.isEmpty(entity.getProducts())) {
            categoryModel.setProducts(toProdcutModel(entity.getProducts()));
        }

        if(!CollectionCheck.isEmpty(entity.getChildCategories())) {
            categoryModel.add(linkTo(methodOn(CategorySubcategoriesController.class)
                    .retrieveAllSubcategories(entity.getId())).withRel("subcategories"));
        }
        return categoryModel;
    }

    @Override
    public CollectionModel<CategoryModel> toCollectionModel(Iterable<? extends Category> entities) {

        CollectionModel<CategoryModel> categoryModels = super.toCollectionModel(entities);

        categoryModels.add(linkTo(methodOn(CategoryController.class).retrieveAllCategories())
                .withSelfRel());

        return categoryModels;
    }

    private List<ProductModel> toProdcutModel(List<Product> products) {

        if (products.isEmpty())
            return Collections.emptyList();

        return products.stream()
                .map(product -> ProductModel.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .build()
                        .add(linkTo(methodOn(ProductController.class).retrieveProduct(product.getId()))
                                .withRel("product")))
                .collect(Collectors.toList());
    }
}