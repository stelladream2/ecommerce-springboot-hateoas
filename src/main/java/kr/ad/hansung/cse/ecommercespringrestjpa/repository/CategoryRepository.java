package kr.ad.hansung.cse.ecommercespringrestjpa.repository;

import kr.ad.hansung.cse.ecommercespringrestjpa.entity.Category;
import kr.ad.hansung.cse.ecommercespringrestjpa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
