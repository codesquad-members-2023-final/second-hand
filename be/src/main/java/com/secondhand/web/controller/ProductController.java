package com.secondhand.web.controller;

import com.secondhand.domain.product.Product;
import com.secondhand.domain.login.LoginCheck;
import com.secondhand.domain.login.LoginValue;
import com.secondhand.service.ProductQueryService;
import com.secondhand.service.ProductService;
import com.secondhand.util.BasicResponse;
import com.secondhand.web.dto.filtercondition.ProductCategorySearchCondition;
import com.secondhand.web.dto.filtercondition.ProductSalesSearchCondition;
import com.secondhand.web.dto.filtercondition.ProductSearchCondition;
import com.secondhand.web.dto.requset.ProductSaveRequest;
import com.secondhand.web.dto.requset.ProductUpdateRequest;
import com.secondhand.web.dto.requset.StatusOrLikeRequest;
import com.secondhand.web.dto.response.MainPageCategoryResponse;
import com.secondhand.web.dto.response.MainPageResponse;
import com.secondhand.web.dto.response.ProductResponse;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Api(tags = "상품")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductQueryService productQueryService;

    @Operation(
            summary = "상품 10개씩 리스트", description = "사용자는 상품을 10개씩 상품 리스프로 볼 수 있다(지역 과 카테고리) 좋아요유무.."
    )
    @LoginCheck
    @GetMapping
    public BasicResponse<MainPageResponse> viewPage(ProductSearchCondition productSearchCondition,
                                                    Pageable pageable,
                                                    @LoginValue long userId) {

        MainPageResponse mainPageResponse = productQueryService.getProductList(productSearchCondition, pageable, userId);

        return BasicResponse.send("사용자는 상품을 10개씩 상품 리스프로 볼 수 있다(지역 과 카테고리)", mainPageResponse);

    }

    @Operation(
            summary = "관심 목록을 카테고리 별로 확인", description = "사용자는 자시의 관심 목록을 카테고리 뱔로 확인 가능."
    )
    @LoginCheck
    @GetMapping("/like")
    public BasicResponse<MainPageCategoryResponse> productLikeCategoryView(ProductCategorySearchCondition condition,
                                                                           Pageable pageable,
                                                                           @LoginValue long userId) {

        MainPageCategoryResponse likeProductList = productQueryService.getLikeProductList(condition, pageable, userId);

        return BasicResponse.send("사용자는 자시의 관심 목록을 카테고리 뱔로 확인 가능", likeProductList);

    }

    @Operation(
            summary = "상품 관심 상품 등록/해제  상품의 상태를 변경할 수 있다", description = "사용자는상품을 과 관심상품 / 해제 할수 있다 또는 특정 상품의 상태를 변경할 수 있다."
    )
    @LoginCheck
    @PatchMapping("/{productId}")
    public BasicResponse<ProductResponse> changeLike(final @Valid @RequestBody StatusOrLikeRequest request,
                                                     @PathVariable long productId,
                                                     @LoginValue long userId) {

        if (request.getStatus() == null) {  //like
            productService.changeLike(productId, userId);
        } else {
            productService.changeStatus(productId, userId, request.getStatus());
        }

        Product product = productService.findById(productId);
        ProductResponse response = ProductResponse.of(true, product);

        return BasicResponse.send("사용자는상품을 과 관심상품 / 해제 할수 있다.", response);

    }


    @Operation(
            summary = "판매/세일 중인 상품", description = "사용자는 세일/판매 중인 상품을 볼수 있다."
    )
    @LoginCheck
    @GetMapping("/sales")
    public BasicResponse<MainPageResponse> productSalesView(@Valid ProductSalesSearchCondition productSearchCondition,
                                                            Pageable pageable,
                                                            @LoginValue long userId) {

        MainPageResponse products = productQueryService.getMemberSalesProducts(productSearchCondition, pageable, userId);

        return BasicResponse.send("판매/세일 중인 상품.", products);

    }

    @Operation(
            summary = "상품 등록", description = "사용자는 단일 상품을 등록할 수 있다 저장된 product id반환."
    )
    @LoginCheck
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public BasicResponse<Long> save(@LoginValue long userId,
                                    @Valid ProductSaveRequest productSaveRequest) {
        log.debug("bdoy  ========================================================== {}", productSaveRequest);
        Long save = productService.save(userId, productSaveRequest);

        return BasicResponse.send("상품 등록.", save);
    }

    @Operation(
            summary = "상품 수정", description = "사용자는 단일 상품을 수정할 수 있다."
    )
    @LoginCheck
    @PutMapping("/{productId}")
    public BasicResponse<ProductResponse> update(@LoginValue long userId,
                                                 @PathVariable long productId,
                                                 @Valid @RequestBody ProductUpdateRequest updateRequest) {
        productService.update(productId, updateRequest, userId);
        ProductResponse productUpdateResponse = productQueryService.isValidMinePage(productId, userId);

        return BasicResponse.send("상품 수정.", productUpdateResponse);
    }


    @Operation(
            summary = "상품 디테일 페이지", description = "사용자는 단일 상품을 조회할 수 있다."
    )
    @LoginCheck
    @GetMapping("/{productId}")
    public BasicResponse<ProductResponse> readDetail(@LoginValue long userId, @PathVariable long productId) {

        ProductResponse detailPage = productQueryService.getDetailPage(productId, userId);

        return BasicResponse.send("상품 디테일 페이지.", detailPage);
    }

    @Operation(
            summary = "상품 삭제", description = "사용자는 단일 상품 삭제 가능합니다.."
    )
    @LoginCheck
    @DeleteMapping("/{productId}")
    public BasicResponse<String> deleteProduct(@LoginValue long userId, @PathVariable long productId) {
        productService.delete(userId, productId);

        return BasicResponse.send(" 상품 삭제");
    }
}
