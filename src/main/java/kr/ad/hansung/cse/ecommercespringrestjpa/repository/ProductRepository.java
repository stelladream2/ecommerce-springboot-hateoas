package kr.ad.hansung.cse.ecommercespringrestjpa.repository;

import kr.ad.hansung.cse.ecommercespringrestjpa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
